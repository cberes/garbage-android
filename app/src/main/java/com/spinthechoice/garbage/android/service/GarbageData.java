package com.spinthechoice.garbage.android.service;

import com.spinthechoice.garbage.android.util.Jsonable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Objects;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static java.util.stream.IntStream.range;

final class GarbageData implements Jsonable {
    private final String id;
    private final List<GarbageOption> presets;

    GarbageData(final String id, final List<GarbageOption> presets) {
        this.id = id;
        this.presets = presets;
    }

    String getId() {
        return id;
    }

    List<GarbageOption> getPresets() {
        return presets;
    }

    @Override
    public JSONObject toJson() throws JSONException {
        final JSONObject json = new JSONObject();
        json.putOpt("default", id);
        json.putOpt("presets", toJson(presets));
        return json;
    }

    private static JSONArray toJson(final List<? extends Jsonable> list) throws JSONException {
        final JSONArray json = new JSONArray();
        if (list != null) {
            for (Jsonable elem : list) {
                json.put(elem.toJson());
            }
        }
        return json;
    }

    static GarbageData fromJson(final JSONObject json) throws JSONException {
        return new GarbageData(
                json.optString("default", null),
                fromJson(json.optJSONArray("presets"))
        );
    }

    static List<GarbageOption> fromJson(final JSONArray json) {
        return json == null ? emptyList() : range(0, json.length())
                .mapToObj(json::optJSONObject)
                .filter(Objects::nonNull)
                .map(GarbageOption::fromJson)
                .collect(toList());
    }
}
