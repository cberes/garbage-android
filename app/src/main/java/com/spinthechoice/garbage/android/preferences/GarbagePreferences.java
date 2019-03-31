package com.spinthechoice.garbage.android.preferences;

import java.time.DayOfWeek;

public class GarbagePreferences {
    private String optionId;
    private DayOfWeek dayOfWeek;
    private String garbageWeek;
    private String recyclingWeek;

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
}
