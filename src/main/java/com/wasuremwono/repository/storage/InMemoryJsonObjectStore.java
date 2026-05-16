package com.wasuremwono.repository.storage;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("!gcp")
public class InMemoryJsonObjectStore implements JsonObjectStore {
	private final Map<String, ConcurrentHashMap<String, Object>> collections = new ConcurrentHashMap<>();

	@Override
	public <T> T save(String collection, String id, T value) {
		collections.computeIfAbsent(collection, ignored -> new ConcurrentHashMap<>()).put(id, value);
		return value;
	}

	@Override
	public <T> T saveIfAbsent(String collection, String id, T value, Class<T> type) {
		Object existing = collections.computeIfAbsent(collection, ignored -> new ConcurrentHashMap<>())
			.putIfAbsent(id, value);
		return existing == null ? value : type.cast(existing);
	}

	@Override
	public <T> Optional<T> findById(String collection, String id, Class<T> type) {
		Map<String, Object> values = collections.getOrDefault(collection, new ConcurrentHashMap<>());
		Object value = values.get(id);
		return Optional.ofNullable(value).map(type::cast);
	}

	@Override
	public <T> Collection<T> findAll(String collection, Class<T> type) {
		Map<String, Object> values = collections.getOrDefault(collection, new ConcurrentHashMap<>());
		return values
			.values()
			.stream()
			.map(type::cast)
			.toList();
	}
}
