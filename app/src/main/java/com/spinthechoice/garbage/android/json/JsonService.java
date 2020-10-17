package com.spinthechoice.garbage.android.json;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class JsonService {
    private static final String TAG = "garbage";

    public JsonService() {
    }

    public JSONObject readJsonObjectSafely(final String json) {
        try {
            return (JSONObject) new JSONTokener(json).nextValue();
        } catch (Exception e) {
            Log.e(TAG, "Failed to read JSON object.", e);
            return new JSONObject();
        }
    }

    public JSONObject readJsonObjectSafely(final Context context, final int res) {
        try {
            return (JSONObject) new JSONTokener(resource(context, res)).nextValue();
        } catch (Exception e) {
            Log.e(TAG, "Failed to read JSON object.", e);
            return new JSONObject();
        }
    }

    public JSONArray readJsonArraySafely(final String json) {
        try {
            return (JSONArray) new JSONTokener(json).nextValue();
        } catch (Exception e) {
            Log.e(TAG, "Failed to read JSON array.", e);
            return new JSONArray();
        }
    }

    public JSONArray readJsonArraySafely(final Context context, final int res) {
        try {
            return (JSONArray) new JSONTokener(resource(context, res)).nextValue();
        } catch (Exception e) {
            Log.e(TAG, "Failed to read JSON array.", e);
            return new JSONArray();
        }
    }

    private static String resource(final Context context, final int res) throws IOException {
        final InputStream input = context.getResources().openRawResource(res);
        final StringBuilder builder = new StringBuilder();
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8))) {
            String line = bufferedReader.readLine();
            while (line != null){
                builder.append(line);
                line = bufferedReader.readLine();
            }
        }
        return builder.toString();
    }
}
