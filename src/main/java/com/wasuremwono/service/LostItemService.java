package com.wasuremwono.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.wasuremwono.dto.AiMatchResponse;
import com.wasuremwono.dto.LostItemRequest;
import com.wasuremwono.dto.LostItemResponse;
import com.wasuremwono.integration.ai.AiBridgeClient;
import com.wasuremwono.integration.ai.AiTextMatchResult;
import com.wasuremwono.model.Contact;
import com.wasuremwono.model.ContactInfo;
import com.wasuremwono.model.LostItem;
import com.wasuremwono.model.MatchResult;
import com.wasuremwono.model.MatchStatus;
import com.wasuremwono.repository.ContactRepository;
import com.wasuremwono.repository.LostItemRepository;
import com.wasuremwono.util.PhoneMaskingUtil;

@Service
public class LostItemService {
	private final LostItemRepository lostItemRepository;
	private final ContactRepository contactRepository;
	private final MonoStructuringService monoStructuringService;
	private final MatchingService matchingService;
	private final AiBridgeClient aiBridgeClient;

	public LostItemService(
		LostItemRepository lostItemRepository,
		ContactRepository contactRepository,
		MonoStructuringService monoStructuringService,
		MatchingService matchingService,
		AiBridgeClient aiBridgeClient
	) {
		this.lostItemRepository = lostItemRepository;
		this.contactRepository = contactRepository;
		this.monoStructuringService = monoStructuringService;
		this.matchingService = matchingService;
		this.aiBridgeClient = aiBridgeClient;
	}

	public LostItemResponse createLostItem(LostItemRequest request) {
		Contact ownerContact = contactRepository.save(Contact.builder()
			.name(request.getName())
			.email(request.getEmail())
			.phoneNumber(request.getPhoneNumber())
			.allowPhoneContact(request.isAllowPhoneContact())
			.allowEmailNotification(request.isAllowEmailNotification())
			.build());
		LostItem lostItem = LostItem.builder()
			.description(request.getDescription())
			.rewardAmount(request.getRewardAmount())
			.preferredLanguage(defaultLanguage(request.getPreferredLanguage()))
			.ownerContactId(ownerContact.getId())
			.structuredData(monoStructuringService.structureLostItem(request.getDescription(), request.getRewardAmount()))
			.createdAt(LocalDateTime.now())
			.build();

		lostItemRepository.save(lostItem);
		AiMatchResponse aiMatch = aiBridgeClient.matchText(request.getDescription())
			.map(this::toAiMatchResponse)
			.orElse(null);
		Optional<MatchResult> match = matchingService.findAndSaveHighMatchForLostItem(lostItem);

		return LostItemResponse.builder()
			.lostItemId(lostItem.getId())
			.structuredData(lostItem.getStructuredData())
			.contactInfo(ContactInfo.builder()
				.contactId(ownerContact.getId())
				.email(ownerContact.getEmail())
				.phoneMasked(PhoneMaskingUtil.mask(ownerContact.getPhoneNumber()))
				.allowPhoneContact(ownerContact.isAllowPhoneContact())
				.allowEmailNotification(ownerContact.isAllowEmailNotification())
				.build())
			.matchStatus(match.isPresent() ? MatchStatus.HIGH_MATCH : MatchStatus.NO_STRONG_MATCH)
			.matchResult(match.map(matchingService::toSummary).orElse(null))
			.aiMatch(aiMatch)
			.message(match.isPresent()
				? "Mono found a high-probability match. Generate an email preview from the notification API before contacting anyone."
				: "No strong match yet. You can create a reward post to widen the search.")
			.build();
	}

	private String defaultLanguage(String preferredLanguage) {
		return preferredLanguage == null || preferredLanguage.isBlank() ? "English" : preferredLanguage;
	}

	private AiMatchResponse toAiMatchResponse(AiTextMatchResult result) {
		return AiMatchResponse.builder()
			.matched(result.matched())
			.number(result.number())
			.itemId(result.itemId())
			.metadataBlob(result.metadataBlob())
			.imageGcsUri(result.imageGcsUri())
			.summaryGcsUri(result.summaryGcsUri())
			.build();
	}
}
