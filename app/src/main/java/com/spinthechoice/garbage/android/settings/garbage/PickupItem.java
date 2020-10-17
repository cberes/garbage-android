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
        List<WeekOption> weekOptions(final GarbagePreferences prefs,
                                     final GarbageScheduleService scheduleService) {
            if (prefs.isGarbageEnabled()) {
                final LocalDate now = LocalDate.now();
                final int weekIndex = prefs.getGarbageWeekIndex();
                final List<WeekOption> options = range(0, prefs.getGarbageWeeks())
                        .mapToObj(week -> {
                            prefs.setGarbageWeekIndex(week);
                            Garbage garbage = scheduleService.createGarbage(prefs);
                            Optional<GarbageDay> day = scheduleService.nextPickup(garbage, now, GarbageDay::isGarbageDay);
                            return new WeekOption(week, day.map(GarbageDay::getDate).orElse(now));
                        })
                        .sorted(Comparator.comparing(WeekOption::getDate))
                        .collect(toList());
                prefs.setGarbageWeekIndex(weekIndex);
                return options;
            } else {
                return emptyList();
            }
        }
    },
    RECYCLING {
        @Override
        List<WeekOption> weekOptions(final GarbagePreferences prefs,
                                     final GarbageScheduleService scheduleService) {
            if (prefs.isRecyclingEnabled()) {
                final LocalDate now = LocalDate.now();
                final int weekIndex = prefs.getRecyclingWeekIndex();
                final List<WeekOption> options = range(0, prefs.getRecyclingWeeks())
                        .mapToObj(week -> {
                            prefs.setRecyclingWeekIndex(week);
                            Garbage garbage = scheduleService.createGarbage(prefs);
                            Optional<GarbageDay> day = scheduleService.nextPickup(garbage, now, GarbageDay::isRecyclingDay);
                            return new WeekOption(week, day.map(GarbageDay::getDate).orElse(now));
                        })
                        .sorted(Comparator.comparing(WeekOption::getDate))
                        .collect(toList());
                prefs.setRecyclingWeekIndex(weekIndex);
                return options;
            } else {
                return emptyList();
            }
        }
    };

    abstract List<WeekOption> weekOptions(GarbagePreferences prefs,
                                          GarbageScheduleService scheduleService);
}
