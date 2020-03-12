package com.spinthechoice.garbage.android.service;

import com.spinthechoice.garbage.android.util.Jsonable;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

public class HolidayRef implements Jsonable {
    private final String id;
    private final boolean leap;

    public HolidayRef(final String id, final boolean leap) {
        this.id = id;
        this.leap = leap;
    }

    public String getId() {
        return id;
    }

    public boolean isLeap() {
        return leap;
    }

    @Override
    public JSONObject toJson() throws JSONException {
        final JSONObject json = new JSONObject();
        json.put("id", id);
        json.put("leap", leap);
        return json;
    }

    static HolidayRef fromJson(final JSONObject json) {
        return new HolidayRef(
                json.optString("id"),
                json.optBoolean("leap", false));
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final HolidayRef that = (HolidayRef) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
