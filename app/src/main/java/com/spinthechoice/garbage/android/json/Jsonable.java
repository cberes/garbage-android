package com.spinthechoice.garbage.android.json;

import org.json.JSONException;
import org.json.JSONObject;

public interface Jsonable {
    JSONObject toJson() throws JSONException;
}
