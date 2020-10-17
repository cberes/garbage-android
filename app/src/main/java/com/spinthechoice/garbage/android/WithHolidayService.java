package com.spinthechoice.garbage.android;

import android.content.Context;

import com.spinthechoice.garbage.android.holiday.HolidayService;

interface WithHolidayService {
    default HolidayService holidayService(final Context context) {
        return Singletons.holidayService(context);
    }
}
