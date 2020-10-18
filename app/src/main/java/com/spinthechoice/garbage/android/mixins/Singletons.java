package com.spinthechoice.garbage.android.mixins;

import android.content.Context;

import com.spinthechoice.garbage.android.R;
import com.spinthechoice.garbage.android.garbage.GarbageScheduleService;
import com.spinthechoice.garbage.android.holidays.HolidayService;
import com.spinthechoice.garbage.android.json.JsonService;
import com.spinthechoice.garbage.android.navigation.NavigationService;
import com.spinthechoice.garbage.android.preferences.PreferencesService;

final class Singletons {
    private static final Singletons instance = new Singletons();

    private GarbageScheduleService garbageScheduleService;
    private HolidayService holidayService;
    private NavigationService navigationService;
    private PreferencesService preferencesService;
    private JsonService jsonService;

    private Singletons() {
    }

    static GarbageScheduleService garbageScheduleService(final Context context) {
        if (instance.garbageScheduleService == null) {
            instance.garbageScheduleService = new GarbageScheduleService(holidayService(context));
        }
        return instance.garbageScheduleService;
    }

    static HolidayService holidayService(final Context context) {
        if (instance.holidayService == null) {
            instance.holidayService = new HolidayService(context, preferencesService());
        }
        return instance.holidayService;
    }

    static NavigationService navigationService() {
        if (instance.navigationService == null) {
            instance.navigationService = new NavigationService();
        }
        return instance.navigationService;
    }

    static PreferencesService preferencesService() {
        if (instance.preferencesService == null) {
            instance.preferencesService = new PreferencesService(jsonService(),
                    context -> jsonService().readJsonArraySafely(context, R.raw.holidays).toString());
        }
        return instance.preferencesService;
    }

    static JsonService jsonService() {
        if (instance.jsonService == null) {
            instance.jsonService = new JsonService();
        }
        return instance.jsonService;
    }
}
