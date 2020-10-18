package com.spinthechoice.garbage.android.settings.notifications;

import java.time.LocalDate;

final class NotificationId {
    private final int id;

    private NotificationId(final int id) {
        this.id = id;
    }

    int asInt() {
        return id;
    }

    static NotificationId fromDate(final LocalDate date) {
        return new NotificationId(date.getYear() * (int) 10e3 +
                date.getMonthValue() * (int) 100 +
                date.getDayOfMonth());
    }
}
