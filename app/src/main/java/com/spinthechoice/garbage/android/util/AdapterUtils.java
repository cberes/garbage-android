package com.spinthechoice.garbage.android.util;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;

import java.time.DayOfWeek;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.List;

import static java.util.stream.Collectors.toList;

public final class AdapterUtils {
    private AdapterUtils() {
        throw new UnsupportedOperationException("cannot instantiate " + getClass());
    }

    public static SpinnerAdapter dayOfWeekAdapter(final Context context, final List<DayOfWeek> daysOfWeek) {
        return new ArrayAdapter<>(context, android.R.layout.simple_spinner_item,
                daysOfWeek.stream()
                        .map(day -> day.getDisplayName(TextStyle.FULL, context.getResources().getConfiguration().getLocales().get(0)))
                        .collect(toList()));
    }

    public static SpinnerAdapter monthAdapter(final Context context, final List<Month> months) {
        return new ArrayAdapter<>(context, android.R.layout.simple_spinner_item,
                months.stream()
                        .map(month -> month.getDisplayName(TextStyle.FULL, context.getResources().getConfiguration().getLocales().get(0)))
                        .collect(toList()));
    }
}
