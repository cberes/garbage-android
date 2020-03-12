package com.spinthechoice.garbage.android.service;

import android.content.Context;

import com.spinthechoice.garbage.Holiday;

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
}
