package com.spinthechoice.garbage.android.service;

import com.spinthechoice.garbage.GlobalGarbageConfiguration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptySet;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static java.util.stream.IntStream.range;

final class GlobalGarbageConfigurationSerializer {
    private GlobalGarbageConfigurationSerializer() {
        throw new UnsupportedOperationException("cannot instantiate " + getClass());
    }

    static JSONObject toJson(final GlobalGarbageConfiguration configuration) throws JSONException {
        return configuration == null ? null : toJsonNonNull(configuration);
    }

    private static JSONObject toJsonNonNull(final GlobalGarbageConfiguration configuration) throws JSONException {
        final JSONObject json = new JSONObject();
        json.put("start", configuration.getStart().toString());
        json.put("reset", configuration.getResetDay().name());
        json.putOpt("garbageWeeks", toJson(configuration.getGarbageWeeks()));
        json.putOpt("recyclingWeeks", toJson(configuration.getRecyclingWeeks()));
        json.putOpt("leapDays", toJson(configuration.getLeapDays()));
        json.putOpt("holidays", toJson(configuration.getHolidays()));
        return json;
    }

    private static JSONArray toJson(final List<String> list) {
        return list == null ? null : list.stream()
                .reduce(new JSONArray(), JSONArray::put, (acc, cur) -> acc);
    }

    private static JSONArray toJson(final Set<LocalDate> dates) {
        return dates == null ? null : dates.stream()
                .map(LocalDate::toString)
                .reduce(new JSONArray(), JSONArray::put, (acc, cur) -> acc);
    }

    static GlobalGarbageConfiguration fromJson(final JSONObject json) {
        return json == null ? null : fromJsonNonNull(json);
    }

    private static GlobalGarbageConfiguration fromJsonNonNull(final JSONObject json) {
        final GlobalGarbageConfiguration.Builder builder = GlobalGarbageConfiguration.builder();
        builder.setStart(dateFromJson(json.optString("start")));
        builder.setResetDay(dayFromJson(json.optString("reset")));
        builder.setGarbageWeeks(listFromJson(json.optJSONArray("garbageWeeks")));
        builder.setRecyclingWeeks(listFromJson(json.optJSONArray("recyclingWeeks")));
        builder.setLeapDays(datesFromJson(json.optJSONArray("leapDays")));
        builder.setHolidays(datesFromJson(json.optJSONArray("holidays")));
        return builder.build();
    }

    private static LocalDate dateFromJson(final String json) {
        return json.isEmpty() ? null : LocalDate.parse(json);
    }

    private static DayOfWeek dayFromJson(final String json) {
        return json.isEmpty() ? null : DayOfWeek.valueOf(json);
    }

    private static List<String> listFromJson(final JSONArray json) {
        return json == null ? emptyList() : range(0, json.length())
                .mapToObj(json::optString)
                .filter(s -> !s.isEmpty())
                .collect(toList());
    }

    private static Set<LocalDate> datesFromJson(final JSONArray json) {
        return json == null ? emptySet() : range(0, json.length())
                .mapToObj(json::optString)
                .filter(s -> !s.isEmpty())
                .map(LocalDate::parse)
                .collect(toSet());
    }
}
