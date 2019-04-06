package com.spinthechoice.garbage.android.service;

import com.spinthechoice.garbage.GlobalGarbageConfiguration;
import com.spinthechoice.garbage.android.util.Jsonable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

import static java.util.Collections.emptyMap;
import static java.util.Collections.emptySet;
import static java.util.stream.Collectors.toMap;
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
        json.put("items", toJson(toPickupItems(configuration)));
        json.putOpt("bulkDays", datesToJson(configuration.getBulkDays()));
        json.putOpt("leapDays", datesToJson(configuration.getLeapDays()));
        json.putOpt("holidays", datesToJson(configuration.getHolidays()));
        return json;
    }

    private static List<PickupItemConfiguration> toPickupItems(final GlobalGarbageConfiguration configuration) {
        final List<PickupItemConfiguration> items = new LinkedList<>();
        items.add(new PickupItemConfiguration(PickupItem.GARBAGE,
                configuration.isGarbageEnabled(), configuration.getGarbageWeeks()));
        items.add(new PickupItemConfiguration(PickupItem.RECYCLING,
                configuration.isRecyclingEnabled(), configuration.getRecyclingWeeks()));
        return items;
    }

    static JSONArray toJson(final Collection<? extends Jsonable> list) throws JSONException {
        final JSONArray json = new JSONArray();
        if (list != null) {
            for (Jsonable elem : list) {
                json.put(elem.toJson());
            }
        }
        return json;
    }

    private static JSONArray datesToJson(final Collection<LocalDate> dates) {
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
        final Map<PickupItem, PickupItemConfiguration> pickupItems =
                pickupItemsFromJson(json.optJSONArray("items"));
        if (pickupItems.containsKey(PickupItem.GARBAGE)) {
            final PickupItemConfiguration item = pickupItems.get(PickupItem.GARBAGE);
            builder.setGarbageEnabled(item.isEnabled());
            builder.setGarbageWeeks(item.getWeeks());
        }
        if (pickupItems.containsKey(PickupItem.RECYCLING)) {
            final PickupItemConfiguration item = pickupItems.get(PickupItem.RECYCLING);
            builder.setRecyclingEnabled(item.isEnabled());
            builder.setRecyclingWeeks(item.getWeeks());
        }
        builder.setBulkDays(datesFromJson(json.optJSONArray("bulkDays")));
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

    private static Map<PickupItem, PickupItemConfiguration> pickupItemsFromJson(final JSONArray json) {
        return json == null ? emptyMap() : range(0, json.length())
                .mapToObj(json::optJSONObject)
                .filter(Objects::nonNull)
                .map(PickupItemConfiguration::fromJson)
                .collect(toMap(PickupItemConfiguration::getItem, Function.identity()));
    }

    private static Set<LocalDate> datesFromJson(final JSONArray json) {
        return json == null ? emptySet() : range(0, json.length())
                .mapToObj(json::optString)
                .filter(s -> !s.isEmpty())
                .map(LocalDate::parse)
                .collect(toSet());
    }
}
