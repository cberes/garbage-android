package com.spinthechoice.garbage.android.mixins;

import com.spinthechoice.garbage.android.navigation.NavigationService;

public interface WithNavigationService {
    default NavigationService navigationService() {
        return Singletons.navigationService();
    }
}
