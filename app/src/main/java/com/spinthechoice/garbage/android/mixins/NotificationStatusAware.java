package com.spinthechoice.garbage.android.mixins;

import android.content.Context;

public interface NotificationStatusAware extends WithPreferencesService {
    default boolean isNotificationEnabled(final Context context) {
        return preferencesService().readNotificationPreferences(context).isNotificationEnabled();
    }
}
