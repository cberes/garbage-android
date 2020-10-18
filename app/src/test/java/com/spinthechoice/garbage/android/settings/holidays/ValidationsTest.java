package com.spinthechoice.garbage.android.settings.holidays;

import com.spinthechoice.garbage.Holiday;
import com.spinthechoice.garbage.HolidayType;
import com.spinthechoice.garbage.android.preferences.NamedHoliday;
import com.spinthechoice.garbage.android.utils.Holidays;

import org.junit.Test;

import java.time.Month;

import static org.junit.Assert.assertEquals;

public class ValidationsTest {
    private static void assertValid(final NamedHoliday holiday, final boolean nameValid, final boolean dateValid) {
        final Validations valid = new Validations(holiday);
        assertEquals(nameValid, valid.isNameValid());
        assertEquals(dateValid, valid.isDateValid());
        assertEquals(nameValid && dateValid, valid.isValid());
    }

    private static Holiday holiday(final int date) {
        return Holiday.builder()
                .setType(HolidayType.STATIC_DATE)
                .setMonth(Month.JANUARY)
                .setDate(date)
                .build();
    }

    @Test
    public void testValidateNameWhenNull() {
        final NamedHoliday holiday = new NamedHoliday(null, null, Holidays.thanksgiving());
        assertValid(holiday, false, true);
    }

    @Test
    public void testValidateNameWhenEmpty() {
        final NamedHoliday holiday = new NamedHoliday(null, "", Holidays.thanksgiving());
        assertValid(holiday, false, true);
    }

    @Test
    public void testValidateNameWhenValid() {
        final NamedHoliday holiday = new NamedHoliday(null, "A", Holidays.thanksgiving());
        assertValid(holiday, true, true);
    }

    @Test
    public void testValidateDateWhenNotStaticDate() {
        final NamedHoliday holiday = new NamedHoliday(null, "Test", Holidays.electionDay());
        assertValid(holiday, true, true);
    }

    @Test
    public void testValidateDateWhenWhenMinDate() {
        final NamedHoliday holiday = new NamedHoliday(null, "Test", holiday(1));
        assertValid(holiday, true, true);
    }

    @Test
    public void testValidateDateWhenWhenMaxDate() {
        final NamedHoliday holiday = new NamedHoliday(null, "Test", holiday(31));
        assertValid(holiday, true, true);
    }

    @Test
    public void testValidateDateWhenWhenDateTooLow() {
        final NamedHoliday holiday = new NamedHoliday(null, "Test", holiday(0));
        assertValid(holiday, true, false);
    }

    @Test
    public void testValidateDateWhenWhenDateTooHigh() {
        final NamedHoliday holiday = new NamedHoliday(null, "Test", holiday(32));
        assertValid(holiday, true, false);
    }

    @Test
    public void testValidateWhenAllInvalid() {
        final NamedHoliday holiday = new NamedHoliday(null, "", holiday(0));
        assertValid(holiday, false, false);
    }
}
