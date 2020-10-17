package com.spinthechoice.garbage.android.adapters;

import android.content.Context;
import android.widget.ArrayAdapter;

import com.spinthechoice.garbage.android.R;

import java.time.DayOfWeek;
import java.time.format.TextStyle;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class DayOfWeekAdapter extends ArrayAdapter<String> {
    public DayOfWeekAdapter(final Context context, final List<DayOfWeek> daysOfWeek) {
        super(context, R.layout.spinner_item,
                daysOfWeek.stream()
                        .map(day -> day.getDisplayName(TextStyle.FULL, context.getResources().getConfiguration().getLocales().get(0)))
                        .collect(toList()));
    }
}
