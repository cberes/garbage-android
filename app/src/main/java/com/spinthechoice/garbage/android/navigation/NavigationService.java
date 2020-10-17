package com.spinthechoice.garbage.android.navigation;

import android.content.Context;
import android.content.SharedPreferences;

public class NavigationService {
    public NavigationPreferences readNavigationPreferences(final Context context) {
        final NavigationPreferences prefs = new NavigationPreferences();
        final SharedPreferences sharedPref = getNavigationPreferences(context);
        prefs.setNavigatedToSettings(sharedPref.getBoolean("settings", false));
        prefs.setNavigatedToHolidayPicker(sharedPref.getBoolean("holidayPicker", false));
        return prefs;
    }

    private SharedPreferences getNavigationPreferences(final Context context) {
        return context.getSharedPreferences(
                "com.spinthechoice.garbage.android.NAVIGATION", Context.MODE_PRIVATE);
    }

    public void writeNavigationPreferences(final Context context, final NavigationPreferences prefs) {
        final SharedPreferences sharedPref = getNavigationPreferences(context);
        final SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean("settings", prefs.hasNavigatedToSettings());
        editor.putBoolean("holidayPicker", prefs.hasNavigatedToHolidayPicker());
        editor.apply();
    }
}
