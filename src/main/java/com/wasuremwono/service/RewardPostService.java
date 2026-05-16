package com.wasuremwono.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.wasuremwono.dto.RewardPostRequest;
import com.wasuremwono.dto.RewardPostResponse;
import com.wasuremwono.model.LostItem;
import com.wasuremwono.model.RewardPost;
import com.wasuremwono.model.RewardPostStatus;
import com.wasuremwono.repository.LostItemRepository;
import com.wasuremwono.repository.RewardPostRepository;
import com.wasuremwono.util.DuplicateResourceException;
import com.wasuremwono.util.ResourceNotFoundException;
import com.wasuremwono.util.SafetyGuideUtil;

@Service
public class RewardPostService {
	private final RewardPostRepository rewardPostRepository;
	private final LostItemRepository lostItemRepository;
	private final MonoStructuringService monoStructuringService;

	public RewardPostService(
		RewardPostRepository rewardPostRepository,
		LostItemRepository lostItemRepository,
		MonoStructuringService monoStructuringService
	) {
		this.rewardPostRepository = rewardPostRepository;
		this.lostItemRepository = lostItemRepository;
		this.monoStructuringService = monoStructuringService;
	}

	public RewardPostResponse createRewardPost(RewardPostRequest request) {
		LostItem lostItem = lostItemRepository.findById(request.getLostItemId())
			.orElseThrow(() -> new ResourceNotFoundException("Lost item not found: " + request.getLostItemId()));
		if (rewardPostRepository.existsByLostItemId(lostItem.getId())) {
			throw new DuplicateResourceException("Reward post already exists for lost item: " + lostItem.getId());
		}
		BigDecimal rewardAmount = request.getRewardAmount() != null ? request.getRewardAmount() : lostItem.getRewardAmount();
		String area = request.getArea() != null && !request.getArea().isBlank()
			? request.getArea()
			: lostItem.getStructuredData().getPossibleLostLocation();

		RewardPost rewardPost = RewardPost.builder()
			.lostItemId(lostItem.getId())
			.title(monoStructuringService.generateRewardPostTitle(lostItem.getStructuredData()))
			.description(monoStructuringService.generateRewardPostDescription(lostItem.getStructuredData()))
			.rewardAmount(rewardAmount == null ? BigDecimal.ZERO : rewardAmount)
			.area(area)
			.languages(resolveLanguages(lostItem.getPreferredLanguage()))
			.status(RewardPostStatus.ACTIVE)
			.safetyNote(SafetyGuideUtil.safetyNote())
			.createdAt(LocalDateTime.now())
			.build();

		return toResponse(rewardPostRepository.save(rewardPost));
	}

	public List<RewardPostResponse> getAllRewardPosts() {
		return rewardPostRepository.findAll().stream()
			.map(this::toResponse)
			.toList();
	}

	private RewardPostResponse toResponse(RewardPost rewardPost) {
		return RewardPostResponse.builder()
			.rewardPostId(rewardPost.getId())
			.title(rewardPost.getTitle())
			.description(rewardPost.getDescription())
			.rewardAmount(rewardPost.getRewardAmount())
			.area(rewardPost.getArea())
			.languages(rewardPost.getLanguages())
			.status(rewardPost.getStatus())
			.safetyNote(rewardPost.getSafetyNote())
			.build();
	}

	private List<String> resolveLanguages(String preferredLanguage) {
		Set<String> languages = new LinkedHashSet<>();
		languages.add(preferredLanguage == null || preferredLanguage.isBlank() ? "English" : preferredLanguage);
		languages.add("English");
		return new ArrayList<>(languages);
	}
}
