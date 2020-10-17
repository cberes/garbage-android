package com.spinthechoice.garbage.android.text;

import org.junit.Test;

import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TextTest {
    @Test
    public void testIsEmpty() {
        assertTrue(Text.isEmpty(null));
        assertTrue(Text.isEmpty(""));
        assertFalse(Text.isEmpty("a"));
    }

    @Test
    public void testIsNotEmpty() {
        assertFalse(Text.isNotEmpty(null));
        assertFalse(Text.isNotEmpty(""));
        assertTrue(Text.isNotEmpty("a"));
    }

    @Test
    public void testCapitalize() {
        assertEquals("A", Text.capitalize(Locale.US, "a"));
        assertEquals("Abc", Text.capitalize(Locale.US, "abc"));
    }

    @Test
    public void testIntToString() {
        assertEquals("-1", Text.intToString(Locale.US, -1));
        assertEquals("0", Text.intToString(Locale.US, 0));
        assertEquals("1", Text.intToString(Locale.US, 1));
    }
}
