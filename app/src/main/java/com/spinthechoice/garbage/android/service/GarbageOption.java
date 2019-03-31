package com.spinthechoice.garbage.android.service;

import com.spinthechoice.garbage.GlobalGarbageConfiguration;

public class GarbageOption {
    public static class Builder {
        private String id;
        private String name;
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

        public Builder setConfiguration(final GlobalGarbageConfiguration configuration) {
            this.configuration = configuration;
            return this;
        }
    }

    private final String id;
    private final String name;
    private final GlobalGarbageConfiguration configuration;

    private GarbageOption(final Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.configuration = builder.configuration;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public GlobalGarbageConfiguration getConfiguration() {
        return configuration;
    }

    public static Builder builder() {
        return new Builder();
    }
}
