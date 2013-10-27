package net.ontrack.core.support;

import org.junit.Test;

import static net.ontrack.core.support.Version.of;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class VersionTest {

    @Test
    public void basics() {
        Version v = new Version(4, 15);
        assertEquals(v.getMajor(), 4);
        assertEquals(v.getMinor(), 15);
        assertEquals("4.15", v.toString());
        assertEquals(new Version(4, 15), v);
        assertEquals(new Version(4, 15).hashCode(), v.hashCode());
    }

    @Test
    public void parsing_ok() {
        Version v = of("4.15");
        assertEquals(new Version(4, 15), v);
    }

    @Test
    public void parsing_ok_with_zeros() {
        Version v = of("04.015");
        assertEquals(new Version(4, 15), v);
    }

    @Test(expected = VersionBlankException.class)
    public void parsing_null() {
        of(null);
    }

    @Test(expected = VersionBlankException.class)
    public void parsing_empty() {
        of("");
    }

    @Test(expected = VersionBlankException.class)
    public void parsing_blank() {
        of(" ");
    }

    @Test(expected = VersionFormatException.class)
    public void parsing_format_1() {
        of("1");
    }

    @Test(expected = VersionFormatException.class)
    public void parsing_format_1_m() {
        of("1.m");
    }

    @Test(expected = VersionFormatException.class)
    public void parsing_format_m_1() {
        of("m.1");
    }

    @Test(expected = VersionFormatException.class)
    public void parsing_format_m() {
        of("m");
    }

    @Test
    public void compare_equal() {
        assertEquals(0, of("4.15").compareTo(of("4.15")));
    }

    @Test
    public void compare_major_equal_gt() {
        assertTrue(of("4.16").compareTo(of("4.15")) > 0);
    }

    @Test
    public void compare_major_equal_lt() {
        assertTrue(of("4.14").compareTo(of("4.15")) < 0);
    }

    @Test
    public void compare_major_gt() {
        assertTrue(of("5.8").compareTo(of("4.15")) > 0);
    }

    @Test
    public void compare_major_lt() {
        assertTrue(of("3.16").compareTo(of("4.15")) < 0);
    }

}
