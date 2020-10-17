package com.spinthechoice.garbage.android.settings.holidays;

@FunctionalInterface
interface OnChangeListener {
    void changed(String id, boolean postpone, boolean cancel);
}
