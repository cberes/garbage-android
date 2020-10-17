package com.spinthechoice.garbage.android.mixins;

import android.content.Context;

import com.spinthechoice.garbage.android.garbage.GarbageScheduleService;

public interface WithGarbageScheduleService {
    default GarbageScheduleService garbageScheduleService(final Context context) {
        return Singletons.garbageScheduleService(context);
    }
}
