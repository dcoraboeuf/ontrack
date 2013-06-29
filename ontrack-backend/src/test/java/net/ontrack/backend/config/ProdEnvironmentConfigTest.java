package net.ontrack.backend.config;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ProdEnvironmentConfigTest {

    @Test
    public void expandVariable_sys() {
        System.setProperty("test.dir", "target/work");
        assertEquals("target/work", ProdEnvironmentConfig.expandVariable("test.dir"));
    }

    @Test(expected = IllegalStateException.class)
    public void expandVariable_none() {
        ProdEnvironmentConfig.expandVariable("xxx");
    }

    @Test
    public void expandPath_sys() {
        System.setProperty("test.dir", "target/work");
        assertEquals("target/work/test", ProdEnvironmentConfig.expandPath("%{test.dir}/test"));
    }

    @Test
    public void expandPath_no_var() {
        assertEquals("work/test", ProdEnvironmentConfig.expandPath("work/test"));
    }

    @Test(expected = IllegalStateException.class)
    public void expandPath_none() {
        ProdEnvironmentConfig.expandPath("%{xxx}/test");
    }

}
