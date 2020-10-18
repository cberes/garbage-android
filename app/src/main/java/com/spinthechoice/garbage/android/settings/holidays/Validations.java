package com.spinthechoice.garbage.android.settings.holidays;

import com.spinthechoice.garbage.HolidayType;
import com.spinthechoice.garbage.android.preferences.NamedHoliday;
import com.spinthechoice.garbage.android.text.Text;

class Validations {
    private final boolean nameValid;
    private final boolean dateValid;
    private final boolean valid;

    Validations(final NamedHoliday holiday) {
        nameValid = Text.isNotEmpty(holiday.getName());
        dateValid = holiday.getHoliday().getType() != HolidayType.STATIC_DATE ||
                (holiday.getHoliday().getDate() >= 1 && holiday.getHoliday().getDate() <= 31);
        valid = nameValid && dateValid;
    }

    boolean isNameValid() {
        return nameValid;
    }

    boolean isDateValid() {
        return dateValid;
    }

    boolean isValid() {
        return valid;
    }
}
