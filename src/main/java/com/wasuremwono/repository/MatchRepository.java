package com.wasuremwono.repository;

import java.util.Collection;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.wasuremwono.model.MatchResult;
import com.wasuremwono.repository.storage.JsonObjectStore;

@Repository
public class MatchRepository {
	private static final String COLLECTION = "matches";

	private final JsonObjectStore store;

	public MatchRepository(JsonObjectStore store) {
		this.store = store;
	}

	public MatchResult save(MatchResult matchResult) {
		if (matchResult.getId() == null) {
			matchResult.setId(matchId(matchResult.getLostItemId(), matchResult.getFoundItemId()));
		}
		return store.save(COLLECTION, matchResult.getId(), matchResult);
	}

	public MatchResult saveIfAbsentByItemIds(MatchResult matchResult) {
		String id = matchId(matchResult.getLostItemId(), matchResult.getFoundItemId());
		matchResult.setId(id);
		return store.saveIfAbsent(COLLECTION, id, matchResult, MatchResult.class);
	}

	public Optional<MatchResult> findById(String id) {
		return store.findById(COLLECTION, id, MatchResult.class);
	}

	public Optional<MatchResult> findByLostItemIdAndFoundItemId(String lostItemId, String foundItemId) {
		return findById(matchId(lostItemId, foundItemId));
	}

	public Collection<MatchResult> findAll() {
		return store.findAll(COLLECTION, MatchResult.class);
	}

	private String matchId(String lostItemId, String foundItemId) {
		if (lostItemId == null || foundItemId == null) {
			throw new IllegalArgumentException("lostItemId and foundItemId are required for match identity");
		}
		return lostItemId + "_" + foundItemId;
	}
}
