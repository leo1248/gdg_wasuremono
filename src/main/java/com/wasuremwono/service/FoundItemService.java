package com.wasuremwono.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.wasuremwono.dto.FoundItemRequest;
import com.wasuremwono.dto.FoundItemResponse;
import com.wasuremwono.dto.AiImageResponse;
import com.wasuremwono.integration.ai.AiBridgeClient;
import com.wasuremwono.integration.ai.AiImageProcessResult;
import com.wasuremwono.model.Contact;
import com.wasuremwono.model.ContactInfo;
import com.wasuremwono.model.EmailPreview;
import com.wasuremwono.model.FoundItem;
import com.wasuremwono.model.MatchResult;
import com.wasuremwono.repository.ContactRepository;
import com.wasuremwono.repository.FoundItemRepository;
import com.wasuremwono.repository.LostItemRepository;
import com.wasuremwono.util.PhoneMaskingUtil;

@Service
public class FoundItemService {
	private final FoundItemRepository foundItemRepository;
	private final LostItemRepository lostItemRepository;
	private final ContactRepository contactRepository;
	private final MonoStructuringService monoStructuringService;
	private final MatchingService matchingService;
	private final NotificationService notificationService;
	private final AiBridgeClient aiBridgeClient;

	public FoundItemService(
		FoundItemRepository foundItemRepository,
		LostItemRepository lostItemRepository,
		ContactRepository contactRepository,
		MonoStructuringService monoStructuringService,
		MatchingService matchingService,
		NotificationService notificationService,
		AiBridgeClient aiBridgeClient
	) {
		this.foundItemRepository = foundItemRepository;
		this.lostItemRepository = lostItemRepository;
		this.contactRepository = contactRepository;
		this.monoStructuringService = monoStructuringService;
		this.matchingService = matchingService;
		this.notificationService = notificationService;
		this.aiBridgeClient = aiBridgeClient;
	}

	public FoundItemResponse createFoundItem(FoundItemRequest request) {
		Contact finderContact = contactRepository.save(Contact.builder()
			.name(request.getName())
			.phoneNumber(request.getPhoneNumber())
			.allowPhoneContact(request.isAllowPhoneContact())
			.allowEmailNotification(false)
			.build());
		FoundItem foundItem = FoundItem.builder()
			.description(request.getDescription())
			.foundLocation(request.getFoundLocation())
			.foundTime(request.getFoundTime())
			.handoverStatus(request.getHandoverStatus())
			.imagePath(request.getImagePath())
			.finderContactId(finderContact.getId())
			.structuredData(monoStructuringService.structureFoundItem(
				request.getDescription(),
				request.getFoundLocation(),
				request.getFoundTime()))
			.createdAt(LocalDateTime.now())
			.build();

		foundItemRepository.save(foundItem);
		AiImageResponse aiImage = aiBridgeClient.processImage(request.getImagePath())
			.map(result -> attachAiImageResult(foundItem, result))
			.orElse(null);
		Optional<MatchResult> match = matchingService.findAndSaveHighMatchForFoundItem(foundItem);
		EmailPreview emailPreview = match
			.flatMap(matchResult -> lostItemRepository.findById(matchResult.getLostItemId())
				.flatMap(lostItem -> contactRepository.findById(lostItem.getOwnerContactId()))
				.filter(Contact::isAllowEmailNotification)
				.map(ownerContact -> notificationService.generateMatchEmailPreview(matchResult.getId())))
			.orElse(null);

		return FoundItemResponse.builder()
			.foundItemId(foundItem.getId())
			.structuredData(foundItem.getStructuredData())
			.contactInfo(ContactInfo.builder()
				.contactId(finderContact.getId())
				.phoneMasked(PhoneMaskingUtil.mask(finderContact.getPhoneNumber()))
				.allowPhoneContact(finderContact.isAllowPhoneContact())
				.allowEmailNotification(false)
				.build())
			.matchResult(match.map(matchingService::toSummary).orElse(null))
			.aiImage(aiImage)
			.emailPreview(emailPreview)
			.message(match.isPresent()
				? "Mono found a high-probability match. Email preview was generated if the lost user allowed email notifications."
				: "Found item saved. Mono did not find a strong match yet.")
			.build();
	}

	private AiImageResponse attachAiImageResult(FoundItem foundItem, AiImageProcessResult result) {
		foundItem.setAiItemId(result.itemId());
		foundItem.setImageGcsUri(result.imageGcsUri());
		foundItem.setSummaryGcsUri(result.summaryGcsUri());
		foundItem.setMetadataGcsUri(result.metadataGcsUri());
		foundItemRepository.save(foundItem);

		return AiImageResponse.builder()
			.itemId(result.itemId())
			.imagePath(result.localImagePath())
			.imageGcsUri(result.imageGcsUri())
			.summaryGcsUri(result.summaryGcsUri())
			.metadataGcsUri(result.metadataGcsUri())
			.build();
	}
}
