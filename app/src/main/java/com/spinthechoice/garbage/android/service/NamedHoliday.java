package com.spinthechoice.garbage.android.service;

import com.spinthechoice.garbage.Holiday;
import com.spinthechoice.garbage.android.util.Jsonable;

import org.json.JSONException;
import org.json.JSONObject;

public class NamedHoliday implements Jsonable {
    private final String id;
    private final String name;
    private final Holiday holiday;

    public NamedHoliday(final String id, final String name, final Holiday holiday) {
        this.id = id;
        this.name = name;
        this.holiday = holiday;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Holiday getHoliday() {
        return holiday;
    }

    @Override
    public JSONObject toJson() throws JSONException {
        final JSONObject json = new JSONObject();
        json.put("id", id);
        json.put("name", name);
        json.put("holiday", HolidaySerializer.toJson(holiday));
        return json;
    }

    static NamedHoliday fromJson(final JSONObject json) {
        return new NamedHoliday(
                json.optString("id"),
                json.optString("name"),
                HolidaySerializer.fromJson(json.optJSONObject("holiday")));
    }
}
