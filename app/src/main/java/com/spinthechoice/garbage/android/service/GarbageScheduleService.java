package com.spinthechoice.garbage.android.service;

import com.spinthechoice.garbage.Garbage;
import com.spinthechoice.garbage.GarbageDay;
import com.spinthechoice.garbage.GlobalGarbageConfiguration;
import com.spinthechoice.garbage.UserGarbageConfiguration;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class GarbageScheduleService {
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
}
