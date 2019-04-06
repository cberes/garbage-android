package com.spinthechoice.garbage.android.service;

import android.content.Context;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

public class GarbagePresetService {
    private static final String TAG = "garbage";

    private final GarbageData data;

    public GarbagePresetService(final Context context, final int res) {
        this(GarbageData.fromJson(readJsonSafely(context, res)));
    }

    GarbagePresetService(final GarbageData data) {
        this.data = data;
    }

    private static JSONObject readJsonSafely(final Context context, final int res) {
        try {
            return readJson(context, res);
        } catch (Exception e) {
            Log.e(TAG, "Failed to read JSON data.", e);
            return new JSONObject();
        }
    }

    private static JSONObject readJson(final Context context, final int res) throws IOException, JSONException {
        final InputStream input = context.getResources().openRawResource(res);
        final StringBuilder builder = new StringBuilder();
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8))) {
            String line = bufferedReader.readLine();
            while(line != null){
                builder.append(line);
                line = bufferedReader.readLine();
            }
        }
        return (JSONObject) new JSONTokener(builder.toString()).nextValue();
    }

    public List<GarbageOption> getAllPresets() {
        return data.getPresets();
    }

    public Optional<GarbageOption> findPresetById(final String id) {
        return getAllPresets().stream()
                .filter(preset -> preset.getId().equals(id))
                .findAny();
    }

    public GarbageOption getDefaultPreset() {
        return findPresetById(data.getId()).orElse(null);
    }
}
