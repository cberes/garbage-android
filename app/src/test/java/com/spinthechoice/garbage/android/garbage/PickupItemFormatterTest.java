package com.spinthechoice.garbage.android.garbage;

import com.spinthechoice.garbage.GarbageDay;

import org.junit.Test;

import java.time.LocalDate;

import static org.junit.Assert.assertEquals;

public class PickupItemFormatterTest {
    private final PickupItemFormatter formatter =
            new PickupItemFormatter("garbage", "bulk", "recycling");

    private String format(final boolean garbage, final boolean bulk, final boolean recycling) {
        final GarbageDay day = new GarbageDay(LocalDate.now(), garbage, recycling, bulk);
        return formatter.format(day, ", ", "and ");
    }

    @Test
    public void testNothingScheduled() {
        assertEquals("", format(false, false, false));
    }

    @Test
    public void testGarbageOnly() {
        assertEquals("garbage", format(true, false, false));
    }

    @Test
    public void testBulkOnly() {
        assertEquals("bulk", format(false, true, false));
    }

    @Test
    public void testRecyclingOnly() {
        assertEquals("recycling", format(false, false, true));
    }

    @Test
    public void testGarbageAndRecycling() {
        assertEquals("garbage and recycling", format(true, false, true));
    }

    @Test
    public void testGarbageAndBulk() {
        assertEquals("garbage and bulk", format(true, true, false));
    }

    @Test
    public void testBulkAndRecycling() {
        assertEquals("bulk and recycling", format(false, true, true));
    }

    @Test
    public void testAllScheduled() {
        assertEquals("garbage, bulk, and recycling", format(true, true, true));
    }
}
