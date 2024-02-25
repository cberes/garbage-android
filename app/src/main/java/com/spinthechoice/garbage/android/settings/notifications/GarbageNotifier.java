package com.spinthechoice.garbage.android.settings.notifications;

import android.Manifest;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.util.Log;

import com.spinthechoice.garbage.Garbage;
import com.spinthechoice.garbage.GarbageDay;
import com.spinthechoice.garbage.android.R;
import com.spinthechoice.garbage.android.garbage.PickupItemFormatter;
import com.spinthechoice.garbage.android.mixins.NotificationStatusAware;
import com.spinthechoice.garbage.android.mixins.WithGarbageScheduleService;
import com.spinthechoice.garbage.android.mixins.WithPreferencesService;
import com.spinthechoice.garbage.android.preferences.GarbagePreferences;
import com.spinthechoice.garbage.android.preferences.NotificationPreferences;
import com.spinthechoice.garbage.android.text.Text;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

import static com.spinthechoice.garbage.android.settings.notifications.NotificationUtils.*;

public class GarbageNotifier extends BroadcastReceiver
        implements NotificationStatusAware, WithGarbageScheduleService, WithPreferencesService {
    private static final String CHANNEL_ID = "com.spinthechoice.garbage.android.GARBAGE_REMINDER";
    private static final String ACTION_ALARM = "com.spinthechoice.garbage.android.action.NOTIFICATION_ALARM";
    private static final String ACTION_SEND = "com.spinthechoice.garbage.android.action.NOTIFICATION_SEND";
    private static final String TAG = "garbage-notify";

    @Override
    public void onReceive(final Context context, final Intent intent) {
        if (!isNotificationEnabled(context)) {
            Log.d(TAG, "Notification is disabled. Doing nothing in broadcast receiver.");
        }
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            startNotificationAlarmRepeating(context);
        } else {
            handleGarbageCheckIntent(context, intent);
        }
    }

    public static void startNotificationAlarmRepeating(final Context context) {
        final AlarmManager alarms = context.getSystemService(AlarmManager.class);
        final Intent intent = new Intent(context, GarbageNotifier.class);
        intent.setAction(ACTION_ALARM);
        final PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        alarms.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), TimeUnit.DAYS.toMillis(1), pendingIntent);
    }

    private void startNotificationAlarm(final Context context, final long delayMillis) {
        final AlarmManager alarms = context.getSystemService(AlarmManager.class);
        final Intent intent = new Intent(context, GarbageNotifier.class);
        intent.setAction(ACTION_SEND);
        final PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);
        alarms.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + delayMillis, pendingIntent);
    }

    private void handleGarbageCheckIntent(final Context context, final Intent intent) {
        final GarbagePreferences prefs = preferencesService().readGarbagePreferences(context);
        final boolean sendRequested = ACTION_SEND.equals(intent.getAction());
        final Garbage garbage = garbageScheduleService(context).createGarbage(prefs);
        handleGarbageCheckIntent(context, sendRequested, garbage);
    }

    private void handleGarbageCheckIntent(final Context context, final boolean sendRequested,
                                          final Garbage garbage) {
        final NotificationPreferences prefs = preferencesService().readNotificationPreferences(context);
        notificationDays(garbage, prefs.getLastNotificationId())
                .forEachOrdered(day -> {
                    final LocalDateTime notificationTime = prefs.getNotificationTime(day.getDate());

                    if (sendRequested || isWithinSendThreshold(notificationTime)) {
                        sendNotification(context, day);
                    } else if (notificationTime.isBefore(LocalDateTime.now().plusDays(1L))) {
                        startNotificationAlarm(context, ChronoUnit.MILLIS.between(LocalDateTime.now(), notificationTime));
                    }
                });
    }

    private void sendNotification(final Context context, final GarbageDay garbageDay) {
        final NotificationManager notifications = context.getSystemService(NotificationManager.class);
        createNotificationChannel(context, notifications);
        final int id = createNotification(context, garbageDay);
        saveNotificationId(context, id);
    }

    private void createNotificationChannel(final Context context, final NotificationManager notifications) {
        final String name = context.getString(R.string.channel_name);
        final String description = context.getString(R.string.channel_description);
        final NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_DEFAULT);
        channel.setDescription(description);
        notifications.createNotificationChannel(channel);
    }

    private int createNotification(final Context context, final GarbageDay garbageDay) {
        final NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.status_bar)
                .setContentTitle(getNotificationTitle(context, garbageDay))
                .setContentText(getNotificationBody(context, garbageDay))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        final NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        final int notificationId = NotificationId.fromDate(garbageDay.getDate()).asInt();
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
                == PackageManager.PERMISSION_GRANTED) {
            notificationManager.notify(notificationId, builder.build());
        }
        return notificationId;
    }

    private static String getNotificationTitle(final Context context, final GarbageDay day) {
        final String items = joinItems(context, day);
        return context.getString(R.string.notification_title, items);
    }

    private static String getNotificationBody(final Context context, final GarbageDay day) {
        final String items = joinItems(context, day);
        final String date = Text.formatDate(context, day.getDate());
        return context.getString(R.string.notification_body, Text.capitalize(context, items), date);
    }

    private static String joinItems(final Context context, final GarbageDay day) {
        final PickupItemFormatter formatter = new PickupItemFormatter(context,
                R.string.notification_item_garbage,
                R.string.notification_item_bulk,
                R.string.notification_item_recycling);
        return formatter.format(day, ", ", "& ");
    }

    private void saveNotificationId(final Context context, final int id) {
        final NotificationPreferences preferences = preferencesService().readNotificationPreferences(context);
        preferences.setLastNotificationId(id);
        preferencesService().writeNotificationPreferences(context, preferences);
    }
}
