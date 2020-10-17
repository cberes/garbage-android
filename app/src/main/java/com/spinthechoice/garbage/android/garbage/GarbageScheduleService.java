package com.spinthechoice.garbage.android.garbage;

import com.spinthechoice.garbage.Garbage;
import com.spinthechoice.garbage.GarbageDay;
import com.spinthechoice.garbage.GlobalGarbageConfiguration;
import com.spinthechoice.garbage.UserGarbageConfiguration;
import com.spinthechoice.garbage.android.holiday.HolidayService;
import com.spinthechoice.garbage.android.preferences.GarbagePreferences;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class GarbageScheduleService {
    private final HolidayService holidayService;

    public GarbageScheduleService(final HolidayService holidayService) {
        this.holidayService = holidayService;
    }

    public Garbage createGarbage(final GarbagePreferences prefs) {
        final UserGarbageConfiguration userConfig = new UserGarbageConfiguration(
                prefs.getDayOfWeek(), prefs.getGarbageWeekIndex(), prefs.getRecyclingWeekIndex());
        final AppGlobalGarbageConfiguration appConfig = AppGlobalGarbageConfiguration.fromPreferences(prefs);
        final GlobalGarbageConfiguration configuration = appConfig.toConfig(holidayService);
        return createGarbage(configuration, userConfig);
    }

    public Garbage createGarbage(final GlobalGarbageConfiguration globalConfig, final UserGarbageConfiguration userConfig) {
        return new Garbage(globalConfig, userConfig);
    }

    public List<GarbageDay> getGarbageDays(final Garbage garbage, final LocalDate start, final int count) {
        return Stream.iterate(start, date -> date.plusDays(1))
                .map(garbage::compute)
                .filter(day -> day.isGarbageDay() || day.isRecyclingDay())
                .limit(count)
                .collect(toList());
    }

    public Optional<GarbageDay> nextPickup(final Garbage garbage, final LocalDate start, final Predicate<GarbageDay> test) {
        return Stream.iterate(start, date -> date.plusDays(1))
                .map(garbage::compute)
                .filter(test)
                .findFirst();
    }
}
