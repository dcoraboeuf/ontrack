package net.ontrack.core.model;

import lombok.Data;

import java.util.Map;

@Data
public class MessageContent {

	private final String text;
	private final Map<String,String> meta;
	
}
