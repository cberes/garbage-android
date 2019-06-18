package com.spinthechoice.garbage.android.service;

import com.spinthechoice.garbage.GlobalGarbageConfiguration;
import com.spinthechoice.garbage.android.util.Jsonable;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class GarbageOption implements Jsonable {
    public static class Builder {
        private String id;
        private String name;
        private String configurationId;
        private GlobalGarbageConfiguration configuration;

        public GarbageOption build() {
            return new GarbageOption(this);
        }

        public Builder setId(final String id) {
            this.id = id;
            return this;
        }

        public Builder setName(final String name) {
            this.name = name;
            return this;
        }

        public Builder setConfigurationId(final String configurationId) {
            this.configurationId = configurationId;
            return this;
        }

        public Builder setConfiguration(final GlobalGarbageConfiguration configuration) {
            this.configuration = configuration;
            return this;
        }
    }

    private final String id;
    private final String name;
    private final String configurationId;
    private final GlobalGarbageConfiguration configuration;

    private GarbageOption(final Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.configurationId = builder.configurationId;
        this.configuration = builder.configuration;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getConfigurationId() {
        return configurationId;
    }

    public GlobalGarbageConfiguration getConfiguration() {
        return configuration;
    }

    @Override
    public JSONObject toJson() throws JSONException {
        final JSONObject json = new JSONObject();
        json.putOpt("id", id);
        json.putOpt("name", name);
        json.putOpt("configurationId", configurationId);
        json.putOpt("configuration", GlobalGarbageConfigurationSerializer.toJson(configuration));
        return json;
    }

    static GarbageOption fromJson(final JSONObject json,
                                  final Map<String, GlobalGarbageConfiguration> configurationsById) {
        final Builder builder = builder();
        builder.setId(json.optString("id", null));
        builder.setName(json.optString("name", null));
        final String configurationId = json.optString("configurationId");
        builder.setConfiguration(configurationsById.get(configurationId));
        return builder.build();
    }

    public static Builder builder() {
        return new Builder();
    }
}
