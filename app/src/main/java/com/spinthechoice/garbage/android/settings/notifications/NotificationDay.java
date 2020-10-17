package com.spinthechoice.garbage.android.settings.notifications;

import android.content.Context;

import com.spinthechoice.garbage.android.R;

import java.time.LocalTime;
import java.util.concurrent.TimeUnit;

enum NotificationDay {
    DAY_OF(R.string.notify_option_day_of, 1) {
        @Override
        int getOffset(final LocalTime time) {
            return time.toSecondOfDay();
        }
    },
    DAY_PRIOR(R.string.notify_option_day_prior, -1) {
        @Override
        int getOffset(final LocalTime time) {
            return time.toSecondOfDay() - (int) TimeUnit.DAYS.toSeconds(1);
        }
    };

    private final int resId;
    private final int multiplier;

    NotificationDay(final int resId, final int multiplier) {
        this.resId = resId;
        this.multiplier = multiplier;
    }

    String getText(final Context context) {
        return context.getString(resId);
    }

    int getOffset(final int offset) {
        return multiplier * Math.abs(offset);
    }

    abstract int getOffset(final LocalTime time);

    static NotificationDay fromNotificationOffset(final int offset) {
        return offset >= 0 ? DAY_OF : DAY_PRIOR;
    }

    static NotificationDay fromIndex(final int index) {
        return values()[index];
    }
}
