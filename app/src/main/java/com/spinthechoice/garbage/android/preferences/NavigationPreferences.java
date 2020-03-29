package com.spinthechoice.garbage.android.preferences;

public class NavigationPreferences {
    private boolean navigatedToSettings;
    private boolean navigatedToHolidayPicker;

    public boolean hasNavigatedToSettings() {
        return navigatedToSettings;
    }

    public void setNavigatedToSettings(final boolean navigatedToSettings) {
        this.navigatedToSettings = navigatedToSettings;
    }

    public boolean hasNavigatedToHolidayPicker() {
        return navigatedToHolidayPicker;
    }

    public void setNavigatedToHolidayPicker(final boolean navigatedToHolidayPicker) {
        this.navigatedToHolidayPicker = navigatedToHolidayPicker;
    }
}
