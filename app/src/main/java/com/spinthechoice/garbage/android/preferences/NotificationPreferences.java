package com.spinthechoice.garbage.android.preferences;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class NotificationPreferences {
    private boolean notificationEnabled;
    private int offset;
    private int lastNotificationId;

    public boolean isNotificationEnabled() {
        return notificationEnabled;
    }

    public void setNotificationEnabled(final boolean notificationEnabled) {
        this.notificationEnabled = notificationEnabled;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(final int offset) {
        this.offset = offset;
    }

    public LocalDateTime getNotificationDateTime() {
        return LocalDate.now().atStartOfDay().plusSeconds(offset);
    }

    public LocalTime getNotificationTime() {
        return getNotificationDateTime().toLocalTime();
    }

    public int getLastNotificationId() {
        return lastNotificationId;
    }

    public void setLastNotificationId(final int lastNotificationId) {
        this.lastNotificationId = lastNotificationId;
    }
}
