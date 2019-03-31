package com.spinthechoice.garbage.android.util;

import org.json.JSONException;
import org.json.JSONObject;

public interface Jsonable {
    JSONObject toJson() throws JSONException;
}
