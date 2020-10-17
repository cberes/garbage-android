package com.spinthechoice.garbage.android;

import com.spinthechoice.garbage.android.navigation.NavigationService;

interface WithNavigationService {
    default NavigationService navigationService() {
        return Singletons.navigationService();
    }
}
