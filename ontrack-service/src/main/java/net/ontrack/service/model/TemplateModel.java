package net.ontrack.service.model;

import com.google.common.collect.ImmutableMap;

import java.util.HashMap;
import java.util.Map;

public class TemplateModel {

	private final Map<String, Object> map = new HashMap<>();

	public TemplateModel add(String name, Object value) {
		map.put(name, value);
		return this;
	}

	public Map<String, Object> toMap() {
		return ImmutableMap.copyOf(map);
	}

}
