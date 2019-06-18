package com.spinthechoice.garbage.android.service;

import com.spinthechoice.garbage.GlobalGarbageConfiguration;
import com.spinthechoice.garbage.android.util.Jsonable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static java.util.stream.IntStream.range;

final class GarbageData implements Jsonable {
    private final String id;
    private final Map<String, GlobalGarbageConfiguration> configurationsById;
    private final List<GarbageOption> presets;

    GarbageData(final String id,
                final Map<String, GlobalGarbageConfiguration> configurationsById,
                final List<GarbageOption> presets) {
        this.id = id;
        this.configurationsById = configurationsById;
        this.presets = presets;
    }

    String getId() {
        return id;
    }

    Map<String, GlobalGarbageConfiguration> getConfigurationsById() {
        return configurationsById;
    }

    List<GarbageOption> getPresets() {
        return presets;
    }

    @Override
    public JSONObject toJson() throws JSONException {
        final JSONObject json = new JSONObject();
        json.putOpt("default", id);
        json.putOpt("configurations", GlobalGarbageConfigurationSerializer.toJson(configurationsById));
        json.putOpt("presets", GlobalGarbageConfigurationSerializer.toJson(presets));
        return json;
    }

    static GarbageData fromJson(final JSONObject json) {
        final Map<String, GlobalGarbageConfiguration> configurationsById =
                GlobalGarbageConfigurationSerializer.fromJson(json.optJSONArray("configurations"));
        return new GarbageData(
                json.optString("default", null),
                configurationsById,
                fromJson(json.optJSONArray("presets"), configurationsById)
        );
    }

    private static List<GarbageOption> fromJson(final JSONArray json,
                                                final Map<String, GlobalGarbageConfiguration> configurationsById) {
        return json == null ? emptyList() : range(0, json.length())
                .mapToObj(json::optJSONObject)
                .filter(Objects::nonNull)
                .map(elem -> GarbageOption.fromJson(elem, configurationsById))
                .collect(toList());
    }
}
