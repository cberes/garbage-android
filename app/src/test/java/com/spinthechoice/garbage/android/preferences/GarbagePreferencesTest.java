package com.spinthechoice.garbage.android.preferences;

import org.junit.Test;

import java.util.HashSet;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class GarbagePreferencesTest {
    @Test
    public void testRemoveHolidayWhenEmpty() {
        final GarbagePreferences prefs = new GarbagePreferences();
        prefs.setSelectedHolidays(new HashSet<>());
        prefs.updateHoliday("1", false, false);
        assertEquals(0, prefs.getSelectedHolidays().size());
    }

    @Test
    public void testRemoveHoliday() {
        final GarbagePreferences prefs = new GarbagePreferences();
        prefs.setSelectedHolidays(new HashSet<>(asList(
                new HolidayRef("1", false),
                new HolidayRef("2", true))));
        prefs.updateHoliday("1", false, false);
        assertEquals(1, prefs.getSelectedHolidays().size());
        assertTrue(prefs.getSelectedHolidays().contains(new HolidayRef("2", true)));
    }

    @Test
    public void testAddPostponeHoliday() {
        testAddHoliday("3", true, 3);
    }

    @Test
    public void testAddCancelHoliday() {
        testAddHoliday("3", false, 3);
    }

    @Test
    public void testUpdatePostponeHoliday() {
        testAddHoliday("2", true, 2);
    }

    @Test
    public void testUpdateCancelHoliday() {
        testAddHoliday("2", false, 2);
    }

    private void testAddHoliday(final String newId,
                                final boolean postpone,
                                final int expectedSize) {
        final GarbagePreferences prefs = new GarbagePreferences();
        prefs.setSelectedHolidays(new HashSet<>(asList(
                new HolidayRef("1", false),
                new HolidayRef("2", true))));
        prefs.updateHoliday(newId, postpone, !postpone);
        assertEquals(expectedSize, prefs.getSelectedHolidays().size());
        assertTrue(prefs.getSelectedHolidays().contains(new HolidayRef(newId, true)));
        assertEquals(postpone, prefs.getSelectedHolidays().stream()
                .filter(it -> it.getId().equals(newId))
                .findFirst().get().isLeap());
    }
}
