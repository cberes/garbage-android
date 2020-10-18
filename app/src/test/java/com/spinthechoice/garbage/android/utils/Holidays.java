package com.spinthechoice.garbage.android.utils;

import com.spinthechoice.garbage.Holiday;
import com.spinthechoice.garbage.HolidayOffset;
import com.spinthechoice.garbage.HolidayType;

import java.time.DayOfWeek;
import java.time.Month;

public final class Holidays {
    private Holidays() {
        throw new UnsupportedOperationException("cannot instantiate " + getClass());
    }

    public static Holiday electionDay() {
        return Holiday.builder()
                .setType(HolidayType.NTH_DAY_OF_WEEK)
                .setMonth(Month.NOVEMBER)
                .setDayOfWeek(DayOfWeek.MONDAY)
                .setWeekIndex(0)
                .setOffset(HolidayOffset.DAY_AFTER)
                .build();
    }

    public static Holiday thanksgiving() {
        return Holiday.builder()
                .setType(HolidayType.NTH_DAY_OF_WEEK)
                .setMonth(Month.NOVEMBER)
                .setDayOfWeek(DayOfWeek.THURSDAY)
                .setWeekIndex(-1)
                .setOffset(HolidayOffset.DAY_OF)
                .build();
    }
}
