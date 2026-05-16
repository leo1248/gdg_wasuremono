package com.wasuremwono.repository.storage;

import java.util.Collection;
import java.util.Optional;

public interface JsonObjectStore {
	<T> T save(String collection, String id, T value);

	<T> T saveIfAbsent(String collection, String id, T value, Class<T> type);

	<T> Optional<T> findById(String collection, String id, Class<T> type);

	<T> Collection<T> findAll(String collection, Class<T> type);
}
