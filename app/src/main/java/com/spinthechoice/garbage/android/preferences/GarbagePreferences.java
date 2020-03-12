package com.spinthechoice.garbage.android.preferences;

import com.spinthechoice.garbage.android.service.HolidayRef;

import java.time.DayOfWeek;
import java.util.Set;

public class GarbagePreferences {
    private int garbageWeeks = 1;
    private int recyclingWeeks = 1;
    private int garbageWeekIndex = 0;
    private int recyclingWeekIndex = 0;
    private DayOfWeek dayOfWeek;
    private Set<HolidayRef> holidays;

    public boolean isGarbageEnabled() {
        return garbageWeeks > 0;
    }

    public int getGarbageWeeks() {
        return garbageWeeks;
    }

    public void setGarbageWeeks(final int garbageWeeks) {
        this.garbageWeeks = garbageWeeks;
    }

    public boolean isRecyclingEnabled() {
        return recyclingWeeks > 0;
    }

    public int getRecyclingWeeks() {
        return recyclingWeeks;
    }

    public void setRecyclingWeeks(final int recyclingWeeks) {
        this.recyclingWeeks = recyclingWeeks;
    }

    public int getGarbageWeekIndex() {
        return garbageWeekIndex;
    }

    public void setGarbageWeekIndex(final int garbageWeekIndex) {
        this.garbageWeekIndex = garbageWeekIndex;
    }

    public int getRecyclingWeekIndex() {
        return recyclingWeekIndex;
    }

    public void setRecyclingWeekIndex(final int recyclingWeekIndex) {
        this.recyclingWeekIndex = recyclingWeekIndex;
    }

    public DayOfWeek getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(final DayOfWeek dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public Set<HolidayRef> getHolidays() {
        return holidays;
    }

    public void setHolidays(final Set<HolidayRef> holidays) {
        this.holidays = holidays;
    }
}
