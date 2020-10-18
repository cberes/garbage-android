package com.spinthechoice.garbage.android.settings.notifications;

import com.spinthechoice.garbage.Garbage;
import com.spinthechoice.garbage.GarbageDay;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.stream.Stream;

final class NotificationUtils {
    private NotificationUtils() {
        throw new UnsupportedOperationException("cannot instantiate " + getClass());
    }

    static boolean isWithinSendThreshold(final LocalDateTime time) {
        final LocalDateTime now = LocalDateTime.now();
        return time.isAfter(now.minusHours(2L)) && time.isBefore(now.plusHours(1L));
    }

    static Stream<GarbageDay> notificationDays(final Garbage garbage, final int lastNotificationId) {
        return Stream.iterate(LocalDate.now(), date -> date.plusDays(1))
                .limit(3)
                .map(garbage::compute)
                .filter(day -> day.isGarbageDay() || day.isRecyclingDay())
                .filter(day -> NotificationId.fromDate(day.getDate()).asInt() != lastNotificationId);
    }
}
