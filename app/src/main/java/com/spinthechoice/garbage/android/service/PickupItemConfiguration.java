package com.spinthechoice.garbage.android.service;

import com.spinthechoice.garbage.android.util.Jsonable;

import org.json.JSONException;
import org.json.JSONObject;

final class PickupItemConfiguration implements Jsonable {
    private final PickupItem item;
    private final boolean enabled;
    private final int weeks;

    public PickupItemConfiguration(final PickupItem item, final boolean enabled, final int weeks) {
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

    int getWeeks() {
        return weeks;
    }

    @Override
    public JSONObject toJson() throws JSONException {
        final JSONObject json = new JSONObject();
        json.putOpt("item", item == null ? null : item.name());
        json.putOpt("enabled", enabled);
        json.putOpt("weeks", weeks);
        return json;
    }

    static PickupItemConfiguration fromJson(final JSONObject json) {
        return new PickupItemConfiguration(
                fromJson(json.optString("item")),
                json.optBoolean("enabled", false),
                json.optInt("weeks", 0)
        );
    }

    private static PickupItem fromJson(final String json) {
        return json.isEmpty() ? null : PickupItem.valueOf(json);
    }
}
