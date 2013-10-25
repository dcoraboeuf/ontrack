package net.ontrack.core.model;

import com.netbeetle.jackson.ObjectMapperFactory;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class ValidationRunCommentCreationFormTest {

    private final ObjectMapper objectMapper = ObjectMapperFactory.createObjectMapper();

    @Test
    public void comment_only() throws IOException {
        String json = "{\"status\":\"\",\"description\":\"Proxy error\",\"properties\":[{\"extension\":\"explanation\",\"name\":\"explanation\",\"value\":\"5. Server or application down\"}]}";
        ValidationRunCommentCreationForm form = objectMapper.readValue(json, ValidationRunCommentCreationForm.class);
        assertNotNull(form);
        assertNull(form.getStatus());
        assertEquals("Proxy error", form.getDescription());
        List<PropertyCreationForm> properties = form.getProperties();
        assertEquals(
                Arrays.asList(
                        new PropertyCreationForm("explanation", "explanation", "5. Server or application down")
                ),
                properties
        );
    }

    @Test
    public void comment_with_status() throws IOException {
        String json = "{\"status\":\"EXPLAINED\",\"description\":\"Proxy error\",\"properties\":[{\"extension\":\"explanation\",\"name\":\"explanation\",\"value\":\"5. Server or application down\"}]}";
        ValidationRunCommentCreationForm form = objectMapper.readValue(json, ValidationRunCommentCreationForm.class);
        assertNotNull(form);
        assertEquals(Status.EXPLAINED, form.getStatus());
        assertEquals("Proxy error", form.getDescription());
        List<PropertyCreationForm> properties = form.getProperties();
        assertEquals(
                Arrays.asList(
                        new PropertyCreationForm("explanation", "explanation", "5. Server or application down")
                ),
                properties
        );
    }

}
