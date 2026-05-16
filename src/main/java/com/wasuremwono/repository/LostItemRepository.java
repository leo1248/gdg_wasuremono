package com.wasuremwono.repository;

import java.util.Collection;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.wasuremwono.model.LostItem;
import com.wasuremwono.repository.storage.JsonObjectStore;
import com.wasuremwono.util.IdGenerator;

@Repository
public class LostItemRepository {
	private static final String COLLECTION = "lost-items";

	private final JsonObjectStore store;

	public LostItemRepository(JsonObjectStore store) {
		this.store = store;
	}

	public LostItem save(LostItem lostItem) {
		if (lostItem.getId() == null) {
			lostItem.setId(IdGenerator.nextId("lost-"));
		}
		return store.save(COLLECTION, lostItem.getId(), lostItem);
	}

	public Optional<LostItem> findById(String id) {
		return store.findById(COLLECTION, id, LostItem.class);
	}

	public Collection<LostItem> findAll() {
		return store.findAll(COLLECTION, LostItem.class);
	}
}
