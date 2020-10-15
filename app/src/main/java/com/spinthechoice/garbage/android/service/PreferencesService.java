package com.spinthechoice.garbage.android.service;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.spinthechoice.garbage.android.preferences.GarbagePreferences;
import com.spinthechoice.garbage.android.preferences.NotificationPreferences;
import com.spinthechoice.garbage.android.util.Jsonable;

import org.json.JSONArray;

import java.time.DayOfWeek;
import java.util.Collection;
import java.util.HashSet;
import java.util.concurrent.TimeUnit;

public class PreferencesService {
    private static final String TAG = "garbage";

    public GarbagePreferences readGarbagePreferences(final Context context, final int holidaysRes) {
        final GarbagePreferences prefs = new GarbagePreferences();
        final SharedPreferences sharedPref = getGarbagePreferences(context);

        final String defaultHolidaysJson = JsonService.readJsonArraySafely(context, holidaysRes).toString();
        final String holidaysJson = sharedPref.getString("allHolidays", defaultHolidaysJson);
        final JSONArray holidaysArray = JsonService.readJsonArraySafely(holidaysJson);
        prefs.setHolidays(JsonableListSerializer.fromJson(holidaysArray, NamedHoliday::fromJson));

        final String selectedHolidaysJson = sharedPref.getString("holidays", "[]");
        final JSONArray selectedHolidaysArray = JsonService.readJsonArraySafely(selectedHolidaysJson);
        prefs.setSelectedHolidays(new HashSet<>(JsonableListSerializer.fromJson(selectedHolidaysArray, HolidayRef::fromJson)));

        prefs.setDayOfWeek(DayOfWeek.valueOf(sharedPref.getString("dayOfWeek", DayOfWeek.MONDAY.name())));
        prefs.setGarbageWeekIndex(sharedPref.getInt("garbageWeekIndex", 0));
        prefs.setGarbageWeeks(sharedPref.getInt("garbageWeeks", 1));
        prefs.setRecyclingWeekIndex(sharedPref.getInt("recyclingWeekIndex", 0));
        prefs.setRecyclingWeeks(sharedPref.getInt("recyclingWeeks", 1));
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
        editor.putString("allHolidays", serializeHolidaysSafely(prefs.getHolidays()).toString());
        editor.putString("holidays", serializeHolidaysSafely(prefs.getSelectedHolidays()).toString());
        editor.putString("dayOfWeek", prefs.getDayOfWeek().name());
        editor.putInt("garbageWeekIndex", prefs.getGarbageWeekIndex());
        editor.putInt("garbageWeeks", prefs.getGarbageWeeks());
        editor.putInt("recyclingWeekIndex", prefs.getRecyclingWeekIndex());
        editor.putInt("recyclingWeeks", prefs.getRecyclingWeeks());
        editor.apply();
    }

    private JSONArray serializeHolidaysSafely(final Collection<? extends Jsonable> holidays) {
        try {
            return JsonableListSerializer.toJson(holidays);
        } catch (Exception e) {
            Log.e(TAG, "Failed to serialize holiday list.", e);
            return new JSONArray();
        }
    }

    public NotificationPreferences readNotificationPreferences(final Context context) {
        final NotificationPreferences prefs = new NotificationPreferences();
        final SharedPreferences sharedPref = getNotificationPreferences(context);
        prefs.setNotificationEnabled(sharedPref.getBoolean("enabled", false));
        prefs.setOffset(sharedPref.getInt("offset", (int) TimeUnit.HOURS.toSeconds(-5)));
        prefs.setLastNotificationId(sharedPref.getInt("notificationId", 0));
        return prefs;
    }

    private SharedPreferences getNotificationPreferences(final Context context) {
        return getPreferences(context, "com.spinthechoice.garbage.android.NOTIFICATION");
    }

    public void writeNotificationPreferences(final Context context, final NotificationPreferences prefs) {
        final SharedPreferences sharedPref = getNotificationPreferences(context);
        final SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean("enabled", prefs.isNotificationEnabled());
        editor.putInt("offset", prefs.getOffset());
        editor.putInt("notificationId", prefs.getLastNotificationId());
        editor.apply();
    }
}
