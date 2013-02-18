package net.ontrack.core.support;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;

public class MapBuilder<K, V> {

	public static <K, V> Map<K, V> build(@SuppressWarnings("unchecked") Pair<K, V>... elements) {
		Map<K, V> map = new LinkedHashMap<>();
		for (Pair<K, V> element : elements) {
			map.put(element.getLeft(), element.getValue());
		}
		return map;
	}

	public static <K, V> MapBuilder<K, V> create() {
		return new MapBuilder<K, V>();
	}

    public static MapBuilder<String,Object> params() {
        return create();
    }

    public static MapBuilder<String,Object> params(String key, Object value) {
        return of(key, value);
    }

	public static <K, V> MapBuilder<K, V> of(K key, V value) {
		return MapBuilder.<K, V> create().with(key, value);
	}

	private final Map<K, V> map = new LinkedHashMap<>();

	public MapBuilder<K, V> with(K key, V value) {
		map.put(key, value);
		return this;
	}

	public Map<K, V> get() {
		return map;
	}

}
