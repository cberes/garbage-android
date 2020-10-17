package com.spinthechoice.garbage.android;

import android.content.Context;

import com.spinthechoice.garbage.GarbageDay;

import java.util.LinkedList;
import java.util.List;

class PickupItemFormatter {
    private final String garbage;
    private final String bulk;
    private final String recycling;

    PickupItemFormatter(final Context context, final int garbage, final int bulk, final int recycling) {
        this(context.getString(garbage), context.getString(bulk), context.getString(recycling));
    }

    PickupItemFormatter(final String garbage, final String bulk, final String recycling) {
        this.garbage = garbage;
        this.bulk = bulk;
        this.recycling = recycling;
    }

    String format(final GarbageDay day, final String separator) {
        return format(day, separator, "");
    }

    String format(final GarbageDay day, final String separator, final String prefixLast) {
        final List<String> items = getItems(day);
        if (items.isEmpty()) {
            return "";
        } else if (items.size() == 1) {
            return items.get(0);
        } else if (items.size() == 2) {
            return items.get(0) + (TextUtils.isNotEmpty(prefixLast) ? " " + prefixLast : separator) + items.get(1);
        } else {
            items.set(items.size() - 1, prefixLast + items.get(items.size() - 1));
            return String.join(separator, items);
        }
    }

    private List<String> getItems(final GarbageDay day) {
        final List<String> items = new LinkedList<>();
        if (day.isGarbageDay()) {
            items.add(garbage);
        }
        if (day.isBulkDay()) {
            items.add(bulk);
        }
        if (day.isRecyclingDay()) {
            items.add(recycling);
        }
        return items;
    }
}
