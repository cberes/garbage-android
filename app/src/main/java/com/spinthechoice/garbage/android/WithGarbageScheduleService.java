package com.spinthechoice.garbage.android;

import android.content.Context;

import com.spinthechoice.garbage.android.garbage.GarbageScheduleService;

interface WithGarbageScheduleService {
    default GarbageScheduleService garbageScheduleService(final Context context) {
        return Singletons.garbageScheduleService(context);
    }
}
