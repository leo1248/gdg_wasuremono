package com.wasuremwono.repository;

import java.util.Collection;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.wasuremwono.model.FoundItem;
import com.wasuremwono.repository.storage.JsonObjectStore;
import com.wasuremwono.util.IdGenerator;

@Repository
public class FoundItemRepository {
	private static final String COLLECTION = "found-items";

	private final JsonObjectStore store;

	public FoundItemRepository(JsonObjectStore store) {
		this.store = store;
	}

	public FoundItem save(FoundItem foundItem) {
		if (foundItem.getId() == null) {
			foundItem.setId(IdGenerator.nextId("found-"));
		}
		return store.save(COLLECTION, foundItem.getId(), foundItem);
	}

	public Optional<FoundItem> findById(String id) {
		return store.findById(COLLECTION, id, FoundItem.class);
	}

	public Collection<FoundItem> findAll() {
		return store.findAll(COLLECTION, FoundItem.class);
	}
}
