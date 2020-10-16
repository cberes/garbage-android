package com.spinthechoice.garbage.android;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import android.util.Log;

import com.spinthechoice.garbage.Garbage;
import com.spinthechoice.garbage.GarbageDay;
import com.spinthechoice.garbage.android.preferences.GarbagePreferences;
import com.spinthechoice.garbage.android.preferences.NotificationPreferences;
import com.spinthechoice.garbage.android.service.GarbageScheduleService;
import com.spinthechoice.garbage.android.service.HolidayService;
import com.spinthechoice.garbage.android.service.PickupItemFormatter;
import com.spinthechoice.garbage.android.service.PreferencesService;
import com.spinthechoice.garbage.android.util.TextUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

public class GarbageNotifier extends BroadcastReceiver {
    private static final String CHANNEL_ID = "com.spinthechoice.garbage.android.GARBAGE_REMINDER";
    private static final String ACTION_ALARM = "com.spinthechoice.garbage.android.action.NOTIFICATION_ALARM";
    private static final String ACTION_SEND = "com.spinthechoice.garbage.android.action.NOTIFICATION_SEND";
    private static final String TAG = "garbage-notify";

    private final GarbageScheduleService scheduleService = new GarbageScheduleService();
    private final PreferencesService prefsService = new PreferencesService();

    @Override
    public void onReceive(final Context context, final Intent intent) {
        if (!isNotificationEnabled(context)) {
            Log.d(TAG, "Notification is disabled. Doing nothing in broadcast receiver.");
        } if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            startNotificationAlarmRepeating(context);
        } else {
            handleGarbageCheckIntent(context, intent);
        }
    }

    static void startNotificationAlarmRepeatingIfEnabled(final Context context) {
        if (isNotificationEnabled(context)) {
            startNotificationAlarmRepeating(context);
        }
    }

    private static boolean isNotificationEnabled(final Context context) {
        return new PreferencesService().readNotificationPreferences(context).isNotificationEnabled();
    }

    static void startNotificationAlarmRepeating(final Context context) {
        final AlarmManager alarms = context.getSystemService(AlarmManager.class);
        final Intent intent = new Intent(context, GarbageNotifier.class);
        intent.setAction(ACTION_ALARM);
        final PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        alarms.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), TimeUnit.DAYS.toMillis(1), pendingIntent);
    }

    private void startNotificationAlarm(final Context context, final long delayMillis) {
        final AlarmManager alarms = context.getSystemService(AlarmManager.class);
        final Intent intent = new Intent(context, GarbageNotifier.class);
        intent.setAction(ACTION_SEND);
        final PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        alarms.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + delayMillis, pendingIntent);
    }

    private void handleGarbageCheckIntent(final Context context, final Intent intent) {
        final GarbagePreferences prefs = prefsService.readGarbagePreferences(context, R.raw.holidays);
        final boolean sendRequested = ACTION_SEND.equals(intent.getAction());
        final HolidayService holidays = new HolidayService(prefsService, context);
        final Garbage garbage = scheduleService.createGarbage(prefs, holidays);
        handleGarbageCheckIntent(context, sendRequested, garbage);
    }

    private void handleGarbageCheckIntent(final Context context, final boolean sendRequested,
                                          final Garbage garbage) {
        final NotificationPreferences prefs = prefsService.readNotificationPreferences(context);
        Stream.iterate(LocalDate.now(), date -> date.plusDays(1))
                .limit(3)
                .map(garbage::compute)
                .filter(day -> day.isGarbageDay() || day.isRecyclingDay())
                .filter(day -> getNotificationId(day) != prefs.getLastNotificationId())
                .forEachOrdered(day -> {
                    final LocalDateTime notificationTime = getNotificationTime(day, prefs);

                    if (sendRequested || isWithinSendThreshold(notificationTime)) {
                        sendNotification(context, day);
                    } else if (notificationTime.isBefore(LocalDateTime.now().plusDays(1L))) {
                        startNotificationAlarm(context, ChronoUnit.MILLIS.between(LocalDateTime.now(), notificationTime));
                    }
                });
    }

    private static LocalDateTime getNotificationTime(final GarbageDay day, final NotificationPreferences prefs) {
        return day.getDate().atStartOfDay().plusSeconds(prefs.getOffset());
    }

    private static boolean isWithinSendThreshold(final LocalDateTime time) {
        final LocalDateTime now = LocalDateTime.now();
        return time.isAfter(now.minusHours(2L)) && time.isBefore(now.plusHours(1L));
    }

    private void sendNotification(final Context context, final GarbageDay garbageDay) {
        final NotificationManager notifications = context.getSystemService(NotificationManager.class);
        createNotificationChannel(context, notifications);
        final int id = createNotification(context, garbageDay);
        saveNotificationId(context, id);
    }

    private void createNotificationChannel(final Context context, final NotificationManager notifications) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            final String name = context.getString(R.string.channel_name);
            final String description = context.getString(R.string.channel_description);
            final NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(description);
            notifications.createNotificationChannel(channel);
        }
    }

    private int createNotification(final Context context, final GarbageDay garbageDay) {
        final NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.status_bar)
                .setContentTitle(getNotificationTitle(context, garbageDay))
                .setContentText(getNotificationBody(context, garbageDay))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        final NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        final int notificationId = getNotificationId(garbageDay);
        notificationManager.notify(notificationId, builder.build());
        return notificationId;
    }

    private static String getNotificationTitle(final Context context, final GarbageDay day) {
        final String items = joinItems(context, day);
        return context.getString(R.string.notification_title, items);
    }

    private static String getNotificationBody(final Context context, final GarbageDay day) {
        final String items = joinItems(context, day);
        final String date = TextUtils.formatDate(context, day.getDate());
        return context.getString(R.string.notification_body, TextUtils.capitalize(context, items), date);
    }

    private static String joinItems(final Context context, final GarbageDay day) {
        final PickupItemFormatter formatter = new PickupItemFormatter(context,
                R.string.notification_item_garbage,
                R.string.notification_item_bulk,
                R.string.notification_item_recycling);
        return formatter.format(day, ", ", "& ");
    }

    private static int getNotificationId(final GarbageDay day) {
        final LocalDate date = day.getDate();
        return date.getYear() * (int) 10e4 + date.getMonthValue() * (int) 10e2 + date.getDayOfMonth();
    }

    private void saveNotificationId(final Context context, final int id) {
        final NotificationPreferences preferences = prefsService.readNotificationPreferences(context);
        preferences.setLastNotificationId(id);
        prefsService.writeNotificationPreferences(context, preferences);
    }
}
