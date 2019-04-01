package com.spinthechoice.garbage.android;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.text.format.DateFormat;
import android.util.Log;

import com.spinthechoice.garbage.Garbage;
import com.spinthechoice.garbage.GarbageDay;
import com.spinthechoice.garbage.UserGarbageConfiguration;
import com.spinthechoice.garbage.android.preferences.GarbagePreferences;
import com.spinthechoice.garbage.android.service.GarbageOption;
import com.spinthechoice.garbage.android.service.GarbagePresetService;
import com.spinthechoice.garbage.android.service.GarbageScheduleService;
import com.spinthechoice.garbage.android.service.PreferencesService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Locale;
import java.util.Optional;
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

    private static boolean isNotificationEnabled(final Context context) {
        return new PreferencesService().readNotificationPreferences(context).isNotificationEnabled();
    }

    private void handleGarbageCheckIntent(final Context context, final Intent intent) {
        final GarbagePresetService presetService = new GarbagePresetService(context, R.raw.data);
        final GarbagePreferences prefs = prefsService.readGarbagePreferences(context);
        final Optional<GarbageOption> option = presetService.findPresetById(prefs.getOptionId());
        if (option.isPresent()) {
            final boolean sendRequested = ACTION_SEND.equals(intent.getAction());
            final Garbage garbage = createGarbage(prefs, option.get());
            handleGarbageCheckIntent(context, sendRequested, garbage);
        }
    }

    private void handleGarbageCheckIntent(final Context context, final boolean sendRequested,
                                          final Garbage garbage) {
        Stream.iterate(LocalDate.now(), date -> date.plusDays(1))
                .limit(3)
                .map(garbage::compute)
                .filter(day -> day.isGarbageDay() || day.isRecyclingDay())
                .forEachOrdered(day -> {
                    final int offset = getNotificationOffset(context);
                    final LocalDateTime notificationTime = day.getDate().atStartOfDay().plusSeconds(offset);

                    // TODO make sure notifications aren't sent after the notification was already dismissed
                    // TODO remember that interval can be inexact
                    if (sendRequested || isWithinSendThreshold(notificationTime)) {
                        sendNotification(context, day);
                    } else if (notificationTime.isBefore(LocalDateTime.now().plusDays(1L))) {
                        startNotificationAlarm(context, ChronoUnit.MILLIS.between(LocalDateTime.now(), notificationTime));
                    }
                });
    }

    private Garbage createGarbage(final GarbagePreferences prefs, final GarbageOption option) {
        final UserGarbageConfiguration userConfig = new UserGarbageConfiguration(
                prefs.getDayOfWeek(), prefs.getGarbageWeek(), prefs.getRecyclingWeek());
        return scheduleService.createGarbage(option.getConfiguration(), userConfig);
    }

    private static int getNotificationOffset(final Context context) {
        return new PreferencesService().readNotificationPreferences(context).getOffset();
    }

    private static boolean isWithinSendThreshold(final LocalDateTime time) {
        final LocalDateTime now = LocalDateTime.now();
        return time.isAfter(now.minusHours(2L)) && now.isBefore(now.plusHours(2L));
    }

    private void sendNotification(final Context context, final GarbageDay garbageDay) {
        final NotificationManager notifications = context.getSystemService(NotificationManager.class);
        createNotificationChannel(context, notifications);
        createNotification(context, garbageDay);
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

    private void createNotification(final Context context, final GarbageDay garbageDay) {
        final NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle(getNotificationTitle(context, garbageDay))
                .setContentText(getNotificationBody(context, garbageDay))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(getNotificationId(garbageDay), builder.build());
    }

    private static String getNotificationTitle(final Context context, final GarbageDay day) {
        final String garbage = context.getString(R.string.notification_item_garbage);
        final String recycling = context.getString(R.string.notification_item_recycling);

        if (day.isGarbageDay() && day.isRecyclingDay()) {
            return context.getString(R.string.notification_title_both, garbage, recycling);
        } else {
            return context.getString(R.string.notification_title, day.isGarbageDay() ? garbage : recycling);
        }
    }

    private static String getNotificationBody(final Context context, final GarbageDay day) {
        final String garbage = context.getString(R.string.notification_item_garbage);
        final String recycling = context.getString(R.string.notification_item_recycling);
        final String date = formatDate(context, day.getDate());

        if (day.isGarbageDay() && day.isRecyclingDay()) {
            return context.getString(R.string.notification_body_both, capitalize(context, garbage), recycling, date);
        } else {
            return context.getString(R.string.notification_body, capitalize(context, day.isGarbageDay() ? garbage : recycling), date);
        }
    }

    private static String formatDate(final Context context, final LocalDate date) {
        final java.text.DateFormat format = DateFormat.getDateFormat(context);
        final Date legacyDate = Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());
        return format.format(legacyDate);
    }

    private static String capitalize(final Context context, final String s) {
        final Locale locale = context.getResources().getConfiguration().getLocales().get(0);
        return s.substring(0, 1).toUpperCase(locale) + s.substring(1);
    }

    private static int getNotificationId(final GarbageDay day) {
        final LocalDate date = day.getDate();
        return date.getYear() * (int) 10e4 + date.getMonthValue() * (int) 10e2 + date.getDayOfMonth();
    }
}
