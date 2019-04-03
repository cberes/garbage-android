package com.spinthechoice.garbage.android.preferences;

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

    public int getLastNotificationId() {
        return lastNotificationId;
    }

    public void setLastNotificationId(final int lastNotificationId) {
        this.lastNotificationId = lastNotificationId;
    }
}
