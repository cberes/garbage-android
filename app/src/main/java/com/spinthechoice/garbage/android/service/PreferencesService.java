package com.spinthechoice.garbage.android.service;

import android.content.Context;
import android.content.SharedPreferences;

import com.spinthechoice.garbage.GlobalGarbageConfiguration;
import com.spinthechoice.garbage.android.preferences.GarbagePreferences;
import com.spinthechoice.garbage.android.preferences.NotificationPreferences;

import java.time.DayOfWeek;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

public class PreferencesService {
    public GarbagePreferences createDefaultPreferences(final GarbageOption option) {
        final GarbagePreferences prefs = new GarbagePreferences();
        prefs.setOptionId(option.getId());
        prefs.setDayOfWeek(DayOfWeek.MONDAY);
        final GlobalGarbageConfiguration config = option.getConfiguration();
        prefs.setGarbageWeek(isEmpty(config.getGarbageWeeks()) ?
                null : config.getGarbageWeeks().get(0));
        prefs.setRecyclingWeek(isEmpty(config.getRecyclingWeeks()) ?
                null : config.getRecyclingWeeks().get(0));
        return prefs;
    }

    private static boolean isEmpty(final Collection<?> col) {
        return col == null || col.isEmpty();
    }

    public GarbagePreferences readGarbagePreferences(final Context context) {
        final GarbagePreferences prefs = new GarbagePreferences();
        final SharedPreferences sharedPref = getGarbagePreferences(context);
        prefs.setOptionId(sharedPref.getString("optionId", null));
        prefs.setDayOfWeek(DayOfWeek.valueOf(sharedPref.getString("dayOfWeek", DayOfWeek.MONDAY.name())));
        prefs.setGarbageWeek(sharedPref.getString("garbageWeek", null));
        prefs.setRecyclingWeek(sharedPref.getString("recyclingWeek", null));
        return prefs;
    }

    private SharedPreferences getGarbagePreferences(final Context context) {
        return getPreferences(context, "com.spinthechoice.garbage.android.GARBAGE");
    }

    private SharedPreferences getPreferences(final Context context, final String name) {
        return context.getSharedPreferences(name, Context.MODE_PRIVATE);
    }

    public void writeGarbagePreferences(final Context context, final GarbagePreferences prefs) {
        final SharedPreferences sharedPref = getGarbagePreferences(context);
        final SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("optionId", prefs.getOptionId());
        editor.putString("dayOfWeek", prefs.getDayOfWeek().name());
        editor.putString("garbageWeek", prefs.getGarbageWeek());
        editor.putString("recyclingWeek", prefs.getRecyclingWeek());
        editor.apply();
    }

    public NotificationPreferences readNotificationPreferences(final Context context) {
        final NotificationPreferences prefs = new NotificationPreferences();
        final SharedPreferences sharedPref = getNotificationPreferences(context);
        prefs.setNotificationEnabled(sharedPref.getBoolean("enabled", false));
        prefs.setOffset(sharedPref.getInt("offset", 0));
        return prefs;
    }

    private SharedPreferences getNotificationPreferences(final Context context) {
        return getPreferences(context, "com.spinthechoice.garbage.android.NOTIFICATION");
    }

    public void writeNotificationPreferences(final Context context, final NotificationPreferences prefs) {
        final SharedPreferences sharedPref = getNotificationPreferences(context);
        final SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean("enabled", prefs.isNotificationEnabled());
        editor.putInt("offset", (int) TimeUnit.HOURS.toSeconds(5));
        editor.apply();
    }
}
