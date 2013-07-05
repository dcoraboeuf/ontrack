package net.ontrack.core.model;

import com.netbeetle.jackson.ObjectMapperFactory;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

public class FlagTest {

    @Test
    public void const_set() {
        assertTrue(Flag.SET.isSet());
    }

    @Test
    public void const_unset() {
        assertFalse(Flag.UNSET.isSet());
    }

    @Test
    public void to_json_set() throws IOException {
        ObjectMapper mapper = ObjectMapperFactory.createObjectMapper();
        String value = mapper.writeValueAsString(Flag.SET);
        assertEquals("{\"set\":true}", value);
    }

    @Test
    public void to_json_unset() throws IOException {
        ObjectMapper mapper = ObjectMapperFactory.createObjectMapper();
        String value = mapper.writeValueAsString(Flag.UNSET);
        assertEquals("{\"set\":false}", value);
    }

    @Test
    public void from_json_set() throws IOException {
        ObjectMapper mapper = ObjectMapperFactory.createObjectMapper();
        Flag flag = mapper.readValue("{\"set\":true}", Flag.class);
        assertTrue(flag.isSet());
    }

    @Test
    public void from_json_unset() throws IOException {
        ObjectMapper mapper = ObjectMapperFactory.createObjectMapper();
        Flag flag = mapper.readValue("{\"set\":false}", Flag.class);
        assertFalse(flag.isSet());
    }

}
