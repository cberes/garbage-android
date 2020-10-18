package com.spinthechoice.garbage.android.settings.notifications;

import com.spinthechoice.garbage.Garbage;
import com.spinthechoice.garbage.GarbageDay;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

import static com.spinthechoice.garbage.android.settings.notifications.NotificationUtils.*;
import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class NotificationUtilsTest {
    private Garbage mockGarbage = mock(Garbage.class);

    @Before
    public void setup() {
        Mockito.reset(mockGarbage);
    }

    @Test
    public void testIsWithinSendThreshold() {
        assertTrue(isWithinSendThreshold(LocalDateTime.now()));
        assertTrue(isWithinSendThreshold(LocalDateTime.now().plusMinutes(55)));
        assertFalse(isWithinSendThreshold(LocalDateTime.now().plusMinutes(65)));
        assertTrue(isWithinSendThreshold(LocalDateTime.now().minusMinutes(115)));
        assertFalse(isWithinSendThreshold(LocalDateTime.now().minusMinutes(125)));
    }

    @Test
    public void testNotificationDaysGetsFirstThreeDays() {
        int i = 0;
        mockGarbage(i++, true, false);
        mockGarbage(i++, false, true);
        mockGarbage(i++, true, false);
        mockGarbage(i++, false, true);
        mockGarbage(i, true, false);
        Stream<GarbageDay> stream = notificationDays(mockGarbage, 1);
        final List<GarbageDay> days = stream.collect(toList());
        assertEquals(3, days.size());
        assertEquals(LocalDate.now(), days.get(0).getDate());
        assertEquals(LocalDate.now().plusDays(1), days.get(1).getDate());
        assertEquals(LocalDate.now().plusDays(2), days.get(2).getDate());
    }

    @Test
    public void testNotificationDaysSkipsDayWithMatchingId() {
        int i = 0;
        mockGarbage(i++, true, false);
        mockGarbage(i++, true, false);
        mockGarbage(i, true, false);
        Stream<GarbageDay> stream = notificationDays(mockGarbage, NotificationId.fromDate(LocalDate.now()).asInt());
        final List<GarbageDay> days = stream.collect(toList());
        assertEquals(2, days.size());
        assertEquals(LocalDate.now().plusDays(1), days.get(0).getDate());
        assertEquals(LocalDate.now().plusDays(2), days.get(1).getDate());
    }

    @Test
    public void testNotificationDaysWhenNoneFound() {
        int i = 0;
        mockGarbage(i++, false, false);
        mockGarbage(i++, false, false);
        mockGarbage(i, false, false);
        Stream<GarbageDay> stream = notificationDays(mockGarbage, 1);
        assertEquals(0, stream.count());
    }

    private void mockGarbage(final int plusDays, final boolean garbage, final boolean recycling) {
        final LocalDate date = LocalDate.now().plusDays(plusDays);
        when(mockGarbage.compute(date)).thenReturn(new GarbageDay(date, garbage, recycling, false));
    }
}
