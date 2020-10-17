package com.spinthechoice.garbage.android.mixins;

import com.spinthechoice.garbage.android.preferences.PreferencesService;

public interface WithPreferencesService {
    default PreferencesService preferencesService() {
        return Singletons.preferencesService();
    }
}
