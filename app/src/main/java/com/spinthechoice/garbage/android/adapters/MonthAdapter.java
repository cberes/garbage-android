package com.spinthechoice.garbage.android.adapters;

import android.content.Context;
import android.widget.ArrayAdapter;

import com.spinthechoice.garbage.android.R;

import java.time.Month;
import java.time.format.TextStyle;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class MonthAdapter extends ArrayAdapter<String> {
    public MonthAdapter(final Context context, final List<Month> months) {
        super(context, R.layout.spinner_item,
                months.stream()
                        .map(month -> month.getDisplayName(TextStyle.FULL, context.getResources().getConfiguration().getLocales().get(0)))
                        .collect(toList()));
    }
}
