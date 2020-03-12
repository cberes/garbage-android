package com.spinthechoice.garbage.android.service;

import com.spinthechoice.garbage.android.util.Jsonable;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static java.util.stream.Collectors.toMap;

final class HolidayData implements Jsonable {
    private final List<NamedHoliday> holidays;
    private final Map<String, NamedHoliday> holidaysById;

    HolidayData(final List<NamedHoliday> holidays) {
        this.holidays = holidays;
        this.holidaysById = holidays.stream().collect(toMap(NamedHoliday::getId, Function.identity()));
    }

    List<NamedHoliday> getHolidays() {
        return holidays;
    }

    Map<String, NamedHoliday> getHolidaysById() {
        return holidaysById;
    }

    @Override
    public JSONObject toJson() throws JSONException {
        final JSONObject json = new JSONObject();
        json.putOpt("holidays", JsonableListSerializer.toJson(holidays));
        return json;
    }

    static HolidayData fromJson(final JSONObject json) {
        return new HolidayData(JsonableListSerializer.fromJson(
                json.optJSONArray("holidays"), NamedHoliday::fromJson));
    }
}
