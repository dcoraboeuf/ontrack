package net.ontrack.core.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;

import com.netbeetle.jackson.ObjectMapperFactory;

public class AckTest {

	@Test
	public void ok() {
		assertTrue(Ack.OK.isSuccess());
	}

	@Test
	public void nok() {
		assertFalse(Ack.NOK.isSuccess());
	}

	@Test
	public void validate_true() {
		assertTrue(Ack.validate(true).isSuccess());
	}

	@Test
	public void validate_false() {
		assertFalse(Ack.validate(false).isSuccess());
	}

	@Test
	public void one_0() {
		assertFalse(Ack.one(0).isSuccess());
	}

	@Test
	public void one_1() {
		assertTrue(Ack.one(1).isSuccess());
	}

	@Test
	public void one_more() {
		assertFalse(Ack.one(2).isSuccess());
	}

	@Test
	public void to_json() throws JsonGenerationException, JsonMappingException, IOException {
		ObjectMapper mapper = ObjectMapperFactory.createObjectMapper();
		String value = mapper.writeValueAsString(Ack.OK);
		assertEquals("{\"success\":true}", value);
	}

	@Test
	public void from_json() throws JsonParseException, JsonMappingException, IOException {
		ObjectMapper mapper = ObjectMapperFactory.createObjectMapper();
		Ack ack = mapper.readValue("{\"success\":true}", Ack.class);
		assertTrue(ack.isSuccess());
	}
	
	@Test
	public void and () {
		assertFalse(Ack.NOK.and(Ack.NOK).isSuccess());
		assertFalse(Ack.NOK.and(Ack.OK).isSuccess());
		assertFalse(Ack.OK.and(Ack.NOK).isSuccess());
		assertTrue(Ack.OK.and(Ack.OK).isSuccess());
	}
	
	@Test
	public void or () {
		assertFalse(Ack.NOK.or(Ack.NOK).isSuccess());
		assertTrue(Ack.NOK.or(Ack.OK).isSuccess());
		assertTrue(Ack.OK.or(Ack.NOK).isSuccess());
		assertTrue(Ack.OK.or(Ack.OK).isSuccess());
	}

}
