package com.spinthechoice.garbage.android.service;

import android.content.Context;

import com.spinthechoice.garbage.Holiday;
import com.spinthechoice.garbage.Holidays;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class HolidayService {
    private final HolidayData data;

    public HolidayService(final Context context, final int res) {
        this(HolidayData.fromJson(JsonService.readJsonObjectSafely(context, res)));
    }

    HolidayService(final HolidayData data) {
        this.data = data;
    }

    Optional<Holiday> findById(final String id) {
        final NamedHoliday holiday = data.getHolidaysById().get(id);
        return Optional.ofNullable(holiday).map(NamedHoliday::getHoliday);
    }

    public List<NamedHoliday> findAll() {
        return new ArrayList<>(data.getHolidays());
    }

    public Optional<LocalDate> findDateForYear(final Holiday holiday, final int year) {
        final Holidays holidayFinder = new Holidays(holiday);
        return holidayFinder.dates(year).stream().findFirst();
    }
}
