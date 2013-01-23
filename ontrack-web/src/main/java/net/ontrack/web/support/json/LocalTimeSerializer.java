package net.ontrack.web.support.json;

import java.io.IOException;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.SerializerProvider;
import org.codehaus.jackson.map.ser.std.SerializerBase;
import org.joda.time.LocalTime;

public class LocalTimeSerializer extends SerializerBase<LocalTime> {

	public LocalTimeSerializer() {
		super(LocalTime.class);
	}

	@Override
	public void serialize(LocalTime value, JsonGenerator jgen,
			SerializerProvider provider) throws IOException,
			JsonGenerationException {
		jgen.writeString(value.toString("HH:mm"));
	}

}
