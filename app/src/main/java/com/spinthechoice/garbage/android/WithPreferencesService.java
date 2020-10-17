package com.spinthechoice.garbage.android;

import com.spinthechoice.garbage.android.preferences.PreferencesService;

interface WithPreferencesService {
    default PreferencesService preferencesService() {
        return Singletons.preferencesService();
    }
}
