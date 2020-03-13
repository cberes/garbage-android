package com.spinthechoice.garbage.android;

import com.spinthechoice.garbage.Holiday;

import java.time.LocalDate;

class HolidayPickerItem {
    private final String id;
    private final String name;
    private final LocalDate date;
    private final String dateText;
    private final Holiday holiday;
    private boolean postpone;
    private boolean cancel;

    HolidayPickerItem(final String id, final String name, final LocalDate date, final String dateText, final Holiday holiday) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.dateText = dateText;
        this.holiday = holiday;
    }

    String getId() {
        return id;
    }

    String getName() {
        return name;
    }

    public LocalDate getDate() {
        return date;
    }

    String getDateText() {
        return dateText;
    }

    Holiday getHoliday() {
        return holiday;
    }

    boolean isPostpone() {
        return postpone;
    }

    void setPostpone(final boolean postpone) {
        this.postpone = postpone;
    }

    boolean isCancel() {
        return cancel;
    }

    void setCancel(final boolean cancel) {
        this.cancel = cancel;
    }
}
