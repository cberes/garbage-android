package com.spinthechoice.garbage.android.settings.garbage;

import com.spinthechoice.garbage.android.garbage.GarbageScheduleService;
import com.spinthechoice.garbage.android.preferences.GarbagePreferences;

import org.junit.Test;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

import static java.util.Collections.emptySet;
import static org.junit.Assert.assertEquals;

public class PickupItemTest {
    @Test
    public void testSetGarbageFrequencyWhenResetRequired() {
        final GarbagePreferences prefs = new GarbagePreferences();
        prefs.setGarbageWeekIndex(3);
        PickupItem.GARBAGE.setFrequency(prefs, 3);
        assertEquals(3, prefs.getGarbageWeeks());
        assertEquals(0, prefs.getGarbageWeekIndex());
    }

    @Test
    public void testSetGarbageFrequency() {
        final GarbagePreferences prefs = new GarbagePreferences();
        prefs.setGarbageWeekIndex(2);
        PickupItem.GARBAGE.setFrequency(prefs, 3);
        assertEquals(3, prefs.getGarbageWeeks());
        assertEquals(2, prefs.getGarbageWeekIndex());
    }

    @Test
    public void testSetRecyclingFrequencyWhenResetRequired() {
        final GarbagePreferences prefs = new GarbagePreferences();
        prefs.setRecyclingWeekIndex(3);
        PickupItem.RECYCLING.setFrequency(prefs, 3);
        assertEquals(3, prefs.getRecyclingWeeks());
        assertEquals(0, prefs.getRecyclingWeekIndex());
    }

    @Test
    public void testSetRecyclingFrequency() {
        final GarbagePreferences prefs = new GarbagePreferences();
        prefs.setRecyclingWeekIndex(2);
        PickupItem.RECYCLING.setFrequency(prefs, 3);
        assertEquals(3, prefs.getRecyclingWeeks());
        assertEquals(2, prefs.getRecyclingWeekIndex());
    }

    @Test
    public void testGarbageWeeksDisabled() {
        final GarbagePreferences prefs = new GarbagePreferences();
        prefs.setGarbageWeeks(0);
        final List<WeekOption> options = PickupItem.GARBAGE.weekOptions(prefs,
                new GarbageScheduleService(null), LocalDate.of(2020, 10, 17));
        assertEquals(0, options.size());
    }

    @Test
    public void testRecyclingWeeksDisabled() {
        final GarbagePreferences prefs = new GarbagePreferences();
        prefs.setRecyclingWeeks(0);
        final List<WeekOption> options = PickupItem.RECYCLING.weekOptions(prefs,
                new GarbageScheduleService(null), LocalDate.of(2020, 10, 17));
        assertEquals(0, options.size());
    }

    @Test
    public void testGarbageWeeksSingleOption() {
        final GarbagePreferences prefs = new GarbagePreferences();
        prefs.setDayOfWeek(DayOfWeek.THURSDAY);
        prefs.setGarbageWeeks(1);
        prefs.setGarbageWeekIndex(0);
        prefs.setSelectedHolidays(emptySet());
        final List<WeekOption> options = PickupItem.GARBAGE.weekOptions(prefs,
                new GarbageScheduleService(null), LocalDate.of(2020, 10, 17));
        assertEquals(1, options.size());
        assertEquals(LocalDate.of(2020, 10, 22), options.get(0).getDate());
    }

    @Test
    public void testGarbageWeekOptions() {
        final GarbagePreferences prefs = new GarbagePreferences();
        prefs.setDayOfWeek(DayOfWeek.THURSDAY);
        prefs.setGarbageWeeks(2);
        prefs.setGarbageWeekIndex(0);
        prefs.setSelectedHolidays(emptySet());
        final List<WeekOption> options = PickupItem.GARBAGE.weekOptions(prefs,
                new GarbageScheduleService(null), LocalDate.of(2020, 10, 17));
        assertEquals(2, options.size());
        assertEquals(LocalDate.of(2020, 10, 22), options.get(0).getDate());
        assertEquals(LocalDate.of(2020, 10, 29), options.get(1).getDate());
    }

    @Test
    public void testRecyclingWeekOptions() {
        final GarbagePreferences prefs = new GarbagePreferences();
        prefs.setDayOfWeek(DayOfWeek.THURSDAY);
        prefs.setRecyclingWeeks(2);
        prefs.setRecyclingWeekIndex(0);
        prefs.setSelectedHolidays(emptySet());
        final List<WeekOption> options = PickupItem.RECYCLING.weekOptions(prefs,
                new GarbageScheduleService(null), LocalDate.of(2020, 10, 17));
        assertEquals(2, options.size());
        assertEquals(LocalDate.of(2020, 10, 22), options.get(0).getDate());
        assertEquals(LocalDate.of(2020, 10, 29), options.get(1).getDate());
    }
}
