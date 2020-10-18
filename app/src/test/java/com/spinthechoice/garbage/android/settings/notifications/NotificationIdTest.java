package com.spinthechoice.garbage.android.settings.notifications;

import org.junit.Test;

import java.time.LocalDate;

import static org.junit.Assert.assertEquals;

public class NotificationIdTest {
    @Test
    public void testAsInt() {
        assertEquals(20000101,
                NotificationId.fromDate(LocalDate.of(2000, 1, 1)).asInt());
        assertEquals(20201231,
                NotificationId.fromDate(LocalDate.of(2020, 12, 31)).asInt());
    }
}
