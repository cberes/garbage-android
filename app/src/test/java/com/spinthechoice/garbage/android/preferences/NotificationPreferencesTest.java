package com.spinthechoice.garbage.android.preferences;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class NotificationPreferencesTest {
    @Test
    public void testUpdateOffsetWhenNoChange() {
        final NotificationPreferences prefs = new NotificationPreferences();
        prefs.setOffset(28800);
        prefs.updateOffset(NotificationDay.DAY_OF);
        assertEquals(28800, prefs.getOffset());
    }

    @Test
    public void testUpdateOffset() {
        final NotificationPreferences prefs = new NotificationPreferences();
        prefs.setOffset(28800);
        prefs.updateOffset(NotificationDay.DAY_PRIOR);
        assertEquals(-57600, prefs.getOffset());
    }
}
