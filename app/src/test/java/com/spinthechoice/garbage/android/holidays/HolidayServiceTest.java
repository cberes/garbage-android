package com.spinthechoice.garbage.android.holidays;

import android.content.Context;

import com.spinthechoice.garbage.Holiday;
import com.spinthechoice.garbage.HolidayOffset;
import com.spinthechoice.garbage.HolidayType;
import com.spinthechoice.garbage.android.preferences.GarbagePreferences;
import com.spinthechoice.garbage.android.preferences.HolidayRef;
import com.spinthechoice.garbage.android.preferences.NamedHoliday;
import com.spinthechoice.garbage.android.preferences.PreferencesService;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.util.Arrays.asList;
import static java.util.Collections.emptySet;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SuppressWarnings("OptionalGetWithoutIsPresent")
public class HolidayServiceTest {
    private Context context = mock(Context.class);
    private PreferencesService prefsService = mock(PreferencesService.class);

    @Before
    public void setup() {
        Mockito.reset(context, prefsService);
    }

    private GarbagePreferences mockPrefsWithBasicHolidays(final HolidayRef... selected) {
        return mockPrefs(new HashSet<>(asList(selected)),
                holiday("one", "Easter"),
                holiday("two", "Thanksgiving"),
                holiday("three", "Christmas"));
    }

    private GarbagePreferences mockPrefs(final NamedHoliday... holidays) {
        return mockPrefs(emptySet(), holidays);
    }

    private GarbagePreferences mockPrefs(final Set<HolidayRef> selected, final NamedHoliday... holidays) {
        final GarbagePreferences prefs = new GarbagePreferences();
        prefs.setHolidays(asList(holidays));
        prefs.setSelectedHolidays(selected);
        when(prefsService.readGarbagePreferences(context)).thenReturn(prefs);
        return prefs;
    }

    private static NamedHoliday holiday(final String id, final String name) {
        return new NamedHoliday(id, name, Holiday.builder()
                .setType(HolidayType.STATIC_DATE)
                .setMonth(Month.JANUARY)
                .setDate(1)
                .build());
    }

    @Test
    public void testFindAllWhenEmpty() {
        mockPrefs();
        final HolidayService holidayService = new HolidayService(context, prefsService);
        assertEquals(0, holidayService.findAll().size());
    }

    @Test
    public void testFindAll() {
        mockPrefsWithBasicHolidays();
        final HolidayService holidayService = new HolidayService(context, prefsService);
        final List<NamedHoliday> holidays = holidayService.findAll();
        assertEquals(3, holidays.size());
        assertEquals("Easter", holidays.get(0).getName());
        assertEquals("Thanksgiving", holidays.get(1).getName());
        assertEquals("Christmas", holidays.get(2).getName());
    }

    @Test
    public void testHolidayCountWhenEmpty() {
        mockPrefs();
        final HolidayService holidayService = new HolidayService(context, prefsService);
        assertEquals(0, holidayService.holidayCount());
    }

    @Test
    public void testHolidayCount() {
        mockPrefsWithBasicHolidays();
        final HolidayService holidayService = new HolidayService(context, prefsService);
        assertEquals(3, holidayService.holidayCount());
    }

    @Test
    public void testIndexOfWhenEmpty() {
        mockPrefs();
        final HolidayService holidayService = new HolidayService(context, prefsService);
        assertEquals(-1, holidayService.indexOf("one"));
    }

    @Test
    public void testIndexOf() {
        mockPrefsWithBasicHolidays();
        final HolidayService holidayService = new HolidayService(context, prefsService);
        assertEquals(-1, holidayService.indexOf("zero"));
        assertEquals(0, holidayService.indexOf("one"));
        assertEquals(1, holidayService.indexOf("two"));
        assertEquals(2, holidayService.indexOf("three"));
    }

    @Test
    public void testFindByIdWhenEmpty() {
        mockPrefs();
        final HolidayService holidayService = new HolidayService(context, prefsService);
        assertFalse(holidayService.findById("one").isPresent());
    }

    @Test
    public void testFindById() {
        mockPrefsWithBasicHolidays();
        final HolidayService holidayService = new HolidayService(context, prefsService);
        assertFalse(holidayService.findById("zero").isPresent());
        assertEquals("Easter", holidayService.findById("one").get().getName());
        assertEquals("Thanksgiving", holidayService.findById("two").get().getName());
        assertEquals("Christmas", holidayService.findById("three").get().getName());
    }

    @Test
    public void testDeleteByIdWhenEmpty() {
        mockPrefs();
        final HolidayService holidayService = new HolidayService(context, prefsService);
        assertEquals(-1, holidayService.deleteById(context, "one"));
    }

    @Test
    public void testDeleteById() {
        mockPrefsWithBasicHolidays();
        final HolidayService holidayService = new HolidayService(context, prefsService);
        assertEquals(-1, holidayService.deleteById(context, "zero"));
        assertEquals(0, holidayService.deleteById(context, "one"));
        assertEquals(1, holidayService.deleteById(context, "three"));
        assertEquals(0, holidayService.deleteById(context, "two"));
        assertEquals(0, holidayService.holidayCount());
    }

    @Test
    public void testDeleteByIdRemovesSelectedHoliday() {
        final GarbagePreferences prefs = mockPrefsWithBasicHolidays(
                new HolidayRef("one", false),
                new HolidayRef("two", false));
        final HolidayService holidayService = new HolidayService(context, prefsService);

        holidayService.deleteById(context, "three");
        assertEquals(2, prefs.getSelectedHolidays().size());

        holidayService.deleteById(context, "two");
        assertEquals(1, prefs.getSelectedHolidays().size());
        assertTrue(prefs.getSelectedHolidays().contains(new HolidayRef("one", false)));
    }

    @Test
    public void testSaveWhenEmpty() {
        mockPrefs();
        final HolidayService holidayService = new HolidayService(context, prefsService);
        String id = holidayService.save(context, holiday(null, "Test"));
        assertNotNull(id);
        assertEquals(1, holidayService.holidayCount());
        assertEquals(0, holidayService.indexOf(id));
    }

    @Test
    public void testSave() {
        mockPrefsWithBasicHolidays();
        final HolidayService holidayService = new HolidayService(context, prefsService);
        String id = holidayService.save(context, holiday(null, "Test"));
        assertNotNull(id);
        assertEquals(4, holidayService.holidayCount());
        assertEquals("Easter", holidayService.findById("one").get().getName());
        assertEquals("Thanksgiving", holidayService.findById("two").get().getName());
        assertEquals("Christmas", holidayService.findById("three").get().getName());
        assertEquals("Test", holidayService.findById(id).get().getName());
    }

    @Test
    public void testUpdate() {
        mockPrefsWithBasicHolidays();
        final HolidayService holidayService = new HolidayService(context, prefsService);
        String id = holidayService.save(context, holiday("two", "Test"));
        assertEquals("two", id);
        assertEquals("Easter", holidayService.findById("one").get().getName());
        assertEquals("Test", holidayService.findById("two").get().getName());
        assertEquals("Christmas", holidayService.findById("three").get().getName());
    }

    @Test
    public void testFindDateForYear() {
        // most of this work is done in the garbage library, so there's not much to test here
        mockPrefs();
        final HolidayService holidayService = new HolidayService(context, prefsService);
        assertEquals(LocalDate.of(2020, 11, 3), holidayService.findDateForYear(Holiday.builder()
                .setType(HolidayType.NTH_DAY_OF_WEEK)
                .setMonth(Month.NOVEMBER)
                .setDayOfWeek(DayOfWeek.MONDAY)
                .setWeekIndex(0)
                .setOffset(HolidayOffset.DAY_AFTER)
                .build(), 2020).get());
    }
}