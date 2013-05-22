package net.ontrack.core.model;

import com.netbeetle.jackson.ObjectMapperFactory;
import org.codehaus.jackson.map.ObjectMapper;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class PromotedRunCreationFormTest {

    @Test
    public void to_json() throws IOException {
        PromotedRunCreationForm form = new PromotedRunCreationForm(
                new DateTime(2013, 5, 22, 15, 17, DateTimeZone.UTC),
                "My comment"
        );
        ObjectMapper mapper = ObjectMapperFactory.createObjectMapper();
        String json = mapper.writeValueAsString(form);
        assertEquals("{\"creation\":1369235820000,\"description\":\"My comment\"}", json);
    }

    @Test
    public void from_json_no_date() throws IOException {
        ObjectMapper mapper = ObjectMapperFactory.createObjectMapper();
        PromotedRunCreationForm form = mapper.readValue("{\"description\":\"My comment\"}", PromotedRunCreationForm.class);
        assertNull(form.getCreation());
        assertEquals("My comment", form.getDescription());
    }

    @Test
    public void from_json_with_date() throws IOException {
        ObjectMapper mapper = ObjectMapperFactory.createObjectMapper();
        PromotedRunCreationForm form = mapper.readValue("{\"creation\":1369235820000,\"description\":\"My comment\"}", PromotedRunCreationForm.class);
        assertEquals(new DateTime(2013, 5, 22, 15, 17, DateTimeZone.UTC), form.getCreation());
        assertEquals("My comment", form.getDescription());
    }

}
