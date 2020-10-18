package com.spinthechoice.garbage.android.preferences;

import org.junit.Test;

import java.time.LocalTime;

import static org.junit.Assert.assertEquals;

public class NotificationDayTest {
    @Test
    public void testFromNotificationOffset() {
        assertEquals(NotificationDay.DAY_OF, NotificationDay.fromNotificationOffset(1));
        assertEquals(NotificationDay.DAY_OF, NotificationDay.fromNotificationOffset(0));
        assertEquals(NotificationDay.DAY_PRIOR, NotificationDay.fromNotificationOffset(-1));
    }

    @Test
    public void testGetOffsetDayOf() {
        assertEquals(29425, NotificationDay.DAY_OF.getOffset(LocalTime.of(8, 10, 25)));
    }

    @Test
    public void testGetOffsetFromIntDayOf() {
        assertEquals(29425, NotificationDay.DAY_OF.getOffset(29425));
        assertEquals(29425, NotificationDay.DAY_OF.getOffset(-29425));
    }

    @Test
    public void testGetOffsetDayPrior() {
        assertEquals(-29425, NotificationDay.DAY_PRIOR.getOffset(LocalTime.of(15, 49, 35)));
    }

    @Test
    public void testGetOffsetFromIntDayPrior() {
        assertEquals(-29425, NotificationDay.DAY_PRIOR.getOffset(29425));
        assertEquals(-29425, NotificationDay.DAY_PRIOR.getOffset(-29425));
    }
}
