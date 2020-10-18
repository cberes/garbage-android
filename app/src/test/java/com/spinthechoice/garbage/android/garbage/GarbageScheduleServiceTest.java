package com.spinthechoice.garbage.android.garbage;

import com.spinthechoice.garbage.Garbage;
import com.spinthechoice.garbage.GarbageDay;
import com.spinthechoice.garbage.android.holidays.HolidayService;
import com.spinthechoice.garbage.android.preferences.GarbagePreferences;
import com.spinthechoice.garbage.android.preferences.HolidayRef;
import com.spinthechoice.garbage.android.preferences.NamedHoliday;
import com.spinthechoice.garbage.android.utils.Holidays;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GarbageScheduleServiceTest {
    private final HolidayService holidayService = mock(HolidayService.class);
    private final GarbageScheduleService garbageService = new GarbageScheduleService(holidayService);

    @Before
    public void setup() {
        Mockito.reset(holidayService);
    }

    @Test
    public void testGetGarbageDays() {
        mockHolidays();
        final Garbage garbage = garbageService.createGarbage(defaultPrefs());
        final List<GarbageDay> days = garbageService.getGarbageDays(garbage, LocalDate.of(2020, 10,17), 10);
        assertEquals(10, days.size());
        assertDays(days, true);
    }

    @Test
    public void testGetGarbageDaysButHolidaysAreMissing() {
        final Garbage garbage = garbageService.createGarbage(defaultPrefs());
        final List<GarbageDay> days = garbageService.getGarbageDays(garbage, LocalDate.of(2020, 10,17), 10);
        assertEquals(10, days.size());
        assertDays(days, false);
    }

    private static GarbagePreferences defaultPrefs() {
        final GarbagePreferences prefs = new GarbagePreferences();
        prefs.setDayOfWeek(DayOfWeek.THURSDAY);
        prefs.setGarbageWeeks(1);
        prefs.setRecyclingWeeks(2);
        prefs.setGarbageWeekIndex(0);
        prefs.setRecyclingWeekIndex(0);
        prefs.setSelectedHolidays(new HashSet<>(asList(
                new HolidayRef("1", true),
                new HolidayRef("2", true))));
        return prefs;
    }

    private void mockHolidays() {
        when(holidayService.findById("1")).thenReturn(Optional.of(new NamedHoliday("1", "Test1", Holidays.thanksgiving())));
        when(holidayService.findById("2")).thenReturn(Optional.of(new NamedHoliday("2", "Test2", Holidays.electionDay())));
    }

    private static void assertDays(final List<GarbageDay> days, final boolean holidays) {
        assertDay(LocalDate.of(2020, 10, 22), true, false, days.get(0));
        assertDay(LocalDate.of(2020, 10, 29), true, true, days.get(1));
        assertDay(LocalDate.of(2020, 11, holidays ? 6 : 5), true, false, days.get(2));
        assertDay(LocalDate.of(2020, 11, 12), true, true, days.get(3));
        assertDay(LocalDate.of(2020, 11, 19), true, false, days.get(4));
        assertDay(LocalDate.of(2020, 11, holidays ? 27 : 26), true, true, days.get(5));
        assertDay(LocalDate.of(2020, 12, 3), true, false, days.get(6));
        assertDay(LocalDate.of(2020, 12, 10), true, true, days.get(7));
        assertDay(LocalDate.of(2020, 12, 17), true, false, days.get(8));
        assertDay(LocalDate.of(2020, 12, 24), true, true, days.get(9));
    }

    private static void assertDay(final LocalDate date, final boolean garbage, final boolean recycling, final GarbageDay day) {
        assertEquals(date, day.getDate());
        assertEquals(garbage, day.isGarbageDay());
        assertEquals(recycling, day.isRecyclingDay());
    }

    @Test
    public void testNextPickup() {
        mockHolidays();
        final Garbage garbage = garbageService.createGarbage(defaultPrefs());
        final Optional<GarbageDay> day = garbageService.nextPickup(garbage, LocalDate.of(2020, 10,17), GarbageDay::isRecyclingDay);
        assertDay(LocalDate.of(2020, 10, 29), true, true, day.get());
    }
}
