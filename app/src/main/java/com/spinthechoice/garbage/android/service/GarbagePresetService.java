package com.spinthechoice.garbage.android.service;

import java.util.List;
import java.util.Optional;

import static java.util.Arrays.asList;

public class GarbagePresetService {
    public List<GarbageOption> getAllPresets() {
        return asList(
                GarbagePresets.amherst(),
                GarbagePresets.buffalo(),
                GarbagePresets.cheektowaga(),
                GarbagePresets.orchardPark(),
                GarbagePresets.westSeneca());
    }

    public Optional<GarbageOption> findPresetById(final String id) {
        return getAllPresets().stream()
                .filter(preset -> preset.getId().equals(id))
                .findAny();
    }

    public GarbageOption getDefaultPreset() {
        return GarbagePresets.buffalo();
    }
}
