package com.spinthechoice.garbage.android.settings.garbage;

import androidx.annotation.NonNull;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

class WeekOption {
    private static final DateTimeFormatter FORMAT = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM);

    private final int id;
    private final LocalDate date;

    WeekOption(final int id, final LocalDate date) {
        this.id = id;
        this.date = date;
    }

    int getId() {
        return id;
    }

    LocalDate getDate() {
        return date;
    }

    @NonNull
    @Override
    public String toString() {
        return date.format(FORMAT);
    }
}
