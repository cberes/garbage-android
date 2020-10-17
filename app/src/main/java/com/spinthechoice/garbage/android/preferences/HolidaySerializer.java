package com.spinthechoice.garbage.android.preferences;

import com.spinthechoice.garbage.Holiday;
import com.spinthechoice.garbage.HolidayOffset;
import com.spinthechoice.garbage.HolidayType;

import org.json.JSONException;
import org.json.JSONObject;

import java.time.DayOfWeek;
import java.time.Month;

final class HolidaySerializer {
    private HolidaySerializer() {
        throw new UnsupportedOperationException("cannot instantiate " + getClass());
    }

    static JSONObject toJson(final Holiday holiday) throws JSONException {
        return holiday == null ? null : toJsonNonNull(holiday);
    }

    private static JSONObject toJsonNonNull(final Holiday holiday) throws JSONException {
        final JSONObject json = new JSONObject();
        json.put("type", holiday.getType().name());
        json.put("offset", holiday.getOffset().name());
        json.put("month", holiday.getMonth().name());
        json.put("date", holiday.getDate());
        json.put("weekIndex", holiday.getWeekIndex());
        json.putOpt("dayOfWeek", holiday.getDayOfWeek() == null ? null : holiday.getDayOfWeek().name());
        return json;
    }

    static Holiday fromJson(final JSONObject json) {
        return json == null ? null : fromJsonNonNull(json);
    }

    private static Holiday fromJsonNonNull(final JSONObject json) {
        final Holiday.Builder builder = Holiday.builder();
        builder.setType(HolidayType.valueOf(json.optString("type", HolidayType.STATIC_DATE.name())));
        builder.setOffset(HolidayOffset.valueOf(json.optString("offset", HolidayOffset.DAY_OF.name())));
        builder.setMonth(Month.valueOf(json.optString("month", Month.JANUARY.name())));
        builder.setDate(json.optInt("date", 0));
        builder.setWeekIndex(json.optInt("weekIndex", 0));
        builder.setDayOfWeek(dayFromJson(json.optString("dayOfWeek")));
        return builder.build();
    }

    private static DayOfWeek dayFromJson(final String json) {
        return json.isEmpty() ? null : DayOfWeek.valueOf(json);
    }
}
