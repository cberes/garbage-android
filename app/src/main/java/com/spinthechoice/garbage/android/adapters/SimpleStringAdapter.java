package com.spinthechoice.garbage.android.adapters;

import android.content.Context;
import android.widget.ArrayAdapter;

import com.spinthechoice.garbage.android.R;

import java.util.List;

public class SimpleStringAdapter extends ArrayAdapter<String> {
    public SimpleStringAdapter(final Context context, final List<String> strings) {
        super(context, R.layout.spinner_item, strings);
    }
}
