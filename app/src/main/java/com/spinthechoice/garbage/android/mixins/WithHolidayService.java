package com.spinthechoice.garbage.android.mixins;

import android.content.Context;

import com.spinthechoice.garbage.android.holiday.HolidayService;

public interface WithHolidayService {
    default HolidayService holidayService(final Context context) {
        return Singletons.holidayService(context);
    }
}
