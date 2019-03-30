package com.spinthechoice.garbage.android;

import java.time.DayOfWeek;

public class GarbageConfiguration {
    private String optionId;
    private DayOfWeek dayOfWeek;
    private String garbageWeek;
    private String recyclingWeek;
    private boolean notificationEnabled;

    public String getOptionId() {
        return optionId;
    }

    public void setOptionId(final String optionId) {
        this.optionId = optionId;
    }

    public DayOfWeek getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(final DayOfWeek dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public String getGarbageWeek() {
        return garbageWeek;
    }

    public void setGarbageWeek(final String garbageWeek) {
        this.garbageWeek = garbageWeek;
    }

    public String getRecyclingWeek() {
        return recyclingWeek;
    }

    public void setRecyclingWeek(final String recyclingWeek) {
        this.recyclingWeek = recyclingWeek;
    }

    public boolean isNotificationEnabled() {
        return notificationEnabled;
    }

    public void setNotificationEnabled(final boolean notificationEnabled) {
        this.notificationEnabled = notificationEnabled;
    }
}
