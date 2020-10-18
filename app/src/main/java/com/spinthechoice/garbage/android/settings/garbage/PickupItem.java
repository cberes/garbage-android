package com.spinthechoice.garbage.android.settings.garbage;

import com.spinthechoice.garbage.Garbage;
import com.spinthechoice.garbage.GarbageDay;
import com.spinthechoice.garbage.android.garbage.GarbageScheduleService;
import com.spinthechoice.garbage.android.preferences.GarbagePreferences;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static java.util.stream.IntStream.range;

enum PickupItem {
    GARBAGE {
        @Override
        protected boolean isEnabled(final GarbagePreferences prefs) {
            return prefs.isGarbageEnabled();
        }

        @Override
        protected boolean isRelevant(final GarbageDay day) {
            return day.isGarbageDay();
        }

        @Override
        protected void setWeekIndex(final GarbagePreferences prefs, final int weekIndex) {
            prefs.setGarbageWeekIndex(weekIndex);
        }

        @Override
        protected int weekIndex(final GarbagePreferences prefs) {
            return prefs.getGarbageWeekIndex();
        }

        @Override
        protected int frequency(final GarbagePreferences prefs) {
            return prefs.getGarbageWeeks();
        }

        @Override
        void setFrequency(final GarbagePreferences prefs, final int weeks) {
            prefs.setGarbageWeeks(weeks);
            if (prefs.getGarbageWeekIndex() >= weeks) {
                prefs.setGarbageWeekIndex(0);
            }
        }
    },
    RECYCLING {
        @Override
        protected boolean isEnabled(final GarbagePreferences prefs) {
            return prefs.isRecyclingEnabled();
        }

        @Override
        protected boolean isRelevant(final GarbageDay day) {
            return day.isRecyclingDay();
        }

        @Override
        protected void setWeekIndex(final GarbagePreferences prefs, final int weekIndex) {
            prefs.setRecyclingWeekIndex(weekIndex);
        }

        @Override
        protected int weekIndex(final GarbagePreferences prefs) {
            return prefs.getRecyclingWeekIndex();
        }

        @Override
        protected int frequency(final GarbagePreferences prefs) {
            return prefs.getRecyclingWeeks();
        }

        @Override
        void setFrequency(final GarbagePreferences prefs, final int weeks) {
            prefs.setRecyclingWeeks(weeks);
            if (prefs.getRecyclingWeekIndex() >= weeks) {
                prefs.setRecyclingWeekIndex(0);
            }
        }
    };

    List<WeekOption> weekOptions(final GarbagePreferences prefs,
                                 final GarbageScheduleService scheduleService) {
        return weekOptions(prefs, scheduleService, LocalDate.now());
    }

    List<WeekOption> weekOptions(final GarbagePreferences prefs,
                                 final GarbageScheduleService scheduleService,
                                 final LocalDate now) {
        if (isEnabled(prefs)) {
            final int weekIndex = weekIndex(prefs);
            final List<WeekOption> options = range(0, frequency(prefs))
                    .mapToObj(week -> {
                        setWeekIndex(prefs, week);
                        Garbage garbage = scheduleService.createGarbage(prefs);
                        Optional<GarbageDay> day = scheduleService.nextPickup(garbage, now, this::isRelevant);
                        return new WeekOption(week, day.map(GarbageDay::getDate).orElse(now));
                    })
                    .sorted(Comparator.comparing(WeekOption::getDate))
                    .collect(toList());
            setWeekIndex(prefs, weekIndex);
            return options;
        } else {
            return emptyList();
        }
    }

    protected abstract boolean isEnabled(GarbagePreferences prefs);

    protected abstract int weekIndex(GarbagePreferences prefs);

    protected abstract int frequency(GarbagePreferences prefs);

    protected abstract boolean isRelevant(GarbageDay day);

    protected abstract void setWeekIndex(GarbagePreferences prefs, int weekIndex);

    abstract void setFrequency(GarbagePreferences prefs, int weeks);
}
