package com.spinthechoice.garbage.android.util;

import android.content.Context;
import android.text.format.DateFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Locale;

public final class TextUtils {
    private TextUtils() {
        throw new UnsupportedOperationException("cannot instantiate " + getClass());
    }

    public static String capitalize(final Context context, final String s) {
        final Locale locale = context.getResources().getConfiguration().getLocales().get(0);
        return s.substring(0, 1).toUpperCase(locale) + s.substring(1);
    }

    public static String formatTimeShort(final Context context, final LocalTime time) {
        final java.text.DateFormat format = DateFormat.getTimeFormat(context);
        final LocalDateTime dateTime = time.atDate(LocalDate.now());
        final Date legacyDate = Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant());
        return format.format(legacyDate);
    }

    public static String formatDate(final Context context, final LocalDate date) {
        final java.text.DateFormat format = DateFormat.getDateFormat(context);
        final Date legacyDate = Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());
        return format.format(legacyDate);
    }

    public static String formatDateMedium(final Context context, final LocalDate date) {
        final java.text.DateFormat format = DateFormat.getMediumDateFormat(context);
        final Date legacyDate = Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());
        return format.format(legacyDate);
    }
}
