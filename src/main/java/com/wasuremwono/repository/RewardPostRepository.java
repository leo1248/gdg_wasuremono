package com.wasuremwono.repository;

import java.util.Collection;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.wasuremwono.model.RewardPost;
import com.wasuremwono.repository.storage.JsonObjectStore;
import com.wasuremwono.util.IdGenerator;

@Repository
public class RewardPostRepository {
	private static final String COLLECTION = "reward-posts";

	private final JsonObjectStore store;

	public RewardPostRepository(JsonObjectStore store) {
		this.store = store;
	}

	public RewardPost save(RewardPost rewardPost) {
		if (rewardPost.getId() == null) {
			rewardPost.setId(IdGenerator.nextId("reward-post-"));
		}
		return store.save(COLLECTION, rewardPost.getId(), rewardPost);
	}

	public Optional<RewardPost> findById(String id) {
		return store.findById(COLLECTION, id, RewardPost.class);
	}

	public Optional<RewardPost> findByLostItemId(String lostItemId) {
		return findAll().stream()
			.filter(rewardPost -> lostItemId.equals(rewardPost.getLostItemId()))
			.findFirst();
	}

	public boolean existsByLostItemId(String lostItemId) {
		return findByLostItemId(lostItemId).isPresent();
	}

	public Collection<RewardPost> findAll() {
		return store.findAll(COLLECTION, RewardPost.class);
	}
}
