package com.spinthechoice.garbage.android.service;

import com.spinthechoice.garbage.android.util.Jsonable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static java.util.stream.IntStream.range;

final class PickupItemConfiguration implements Jsonable {
    private final PickupItem item;
    private final boolean enabled;
    private final List<String> weeks;

    public PickupItemConfiguration(final PickupItem item, final boolean enabled, final List<String> weeks) {
        this.item = item;
        this.enabled = enabled;
        this.weeks = weeks;
    }

    PickupItem getItem() {
        return item;
    }

    boolean isEnabled() {
        return enabled;
    }

    List<String> getWeeks() {
        return weeks;
    }

    @Override
    public JSONObject toJson() throws JSONException {
        final JSONObject json = new JSONObject();
        json.putOpt("item", item == null ? null : item.name());
        json.putOpt("enabled", enabled);
        json.putOpt("weeks", toJson(weeks));
        return json;
    }

    private static JSONArray toJson(final List<String> list) {
        return list == null ? null : list.stream()
                .reduce(new JSONArray(), JSONArray::put, (acc, cur) -> acc);
    }

    static PickupItemConfiguration fromJson(final JSONObject json) {
        return new PickupItemConfiguration(
                fromJson(json.optString("item")),
                json.optBoolean("enabled", false),
                fromJson(json.optJSONArray("weeks"))
        );
    }

    private static PickupItem fromJson(final String json) {
        return json.isEmpty() ? null : PickupItem.valueOf(json);
    }

    private static List<String> fromJson(final JSONArray json) {
        return json == null ? emptyList() : range(0, json.length())
                .mapToObj(json::optString)
                .filter(s -> !s.isEmpty())
                .collect(toList());
    }
}
