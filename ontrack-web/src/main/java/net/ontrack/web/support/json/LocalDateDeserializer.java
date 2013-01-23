package net.ontrack.web.support.json;

import java.io.IOException;

import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.deser.std.StdDeserializer;
import org.joda.time.LocalDate;

public class LocalDateDeserializer extends StdDeserializer<LocalDate> {

	public LocalDateDeserializer() {
		super(LocalDate.class);
	}
	
	@Override
	public LocalDate deserialize(JsonParser jp, DeserializationContext ctxt)
			throws IOException, JsonProcessingException {
		return LocalDate.parse(jp.getText());
	}

}
