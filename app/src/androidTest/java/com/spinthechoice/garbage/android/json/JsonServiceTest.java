package com.spinthechoice.garbage.android.json;

import android.content.Context;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.spinthechoice.garbage.HolidayType;
import com.spinthechoice.garbage.android.R;
import com.spinthechoice.garbage.android.preferences.NamedHoliday;

import org.json.JSONArray;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.time.DayOfWeek;
import java.time.Month;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class JsonServiceTest {
    @Test
    public void testReadDefaultHolidays() {
        final Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        final JSONArray holidaysArray = new JsonService().readJsonArraySafely(context, R.raw.holidays);
        final List<NamedHoliday> holidays = JsonableListSerializer.fromJson(holidaysArray, NamedHoliday::fromJson);
        assertEquals(12, holidays.size());
        assertNewYearsDay(holidays.get(0));
        assertMlkDay(holidays.get(1));
    }

    private static void assertNewYearsDay(final NamedHoliday holiday) {
        assertEquals("07b41e3d-5850-4b1e-8024-c9aaa25cc052", holiday.getId());
        assertEquals("New Year's Day", holiday.getName());
        assertEquals(HolidayType.STATIC_DATE, holiday.getHoliday().getType());
        assertEquals(Month.JANUARY, holiday.getHoliday().getMonth());
        assertEquals(1, holiday.getHoliday().getDate());
    }

    private static void assertMlkDay(final NamedHoliday holiday) {
        assertEquals("c623929e-0f38-4727-b104-5cbcb70a8751", holiday.getId());
        assertEquals("Martin Luther King Jr. Day", holiday.getName());
        assertEquals(HolidayType.NTH_DAY_OF_WEEK, holiday.getHoliday().getType());
        assertEquals(Month.JANUARY, holiday.getHoliday().getMonth());
        assertEquals(DayOfWeek.MONDAY, holiday.getHoliday().getDayOfWeek());
        assertEquals(-1, holiday.getHoliday().getWeekIndex());
    }
}
