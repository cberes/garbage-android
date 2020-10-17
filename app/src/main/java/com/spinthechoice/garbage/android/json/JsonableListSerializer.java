package com.spinthechoice.garbage.android.json;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static java.util.stream.IntStream.range;

public final class JsonableListSerializer {
    private JsonableListSerializer() {
        throw new UnsupportedOperationException("cannot instantiate " + getClass());
    }

    public static JSONArray toJson(final Collection<? extends Jsonable> list) throws JSONException {
        final JSONArray json = new JSONArray();
        if (list != null) {
            for (Jsonable elem : list) {
                json.put(elem.toJson());
            }
        }
        return json;
    }

    public static <E> List<E> fromJson(final JSONArray json, final Function<JSONObject, E> mapper) {
        return json == null ? emptyList() : range(0, json.length())
                .mapToObj(json::optJSONObject)
                .map(it -> it == null ? null : mapper.apply(it))
                .collect(toList());
    }
}
