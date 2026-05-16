package com.wasuremwono.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.wasuremwono.dto.RewardStatusResponse;
import com.wasuremwono.model.EmailPreview;
import com.wasuremwono.model.FoundItem;
import com.wasuremwono.model.LostItem;
import com.wasuremwono.model.MatchResult;
import com.wasuremwono.repository.ContactRepository;
import com.wasuremwono.repository.FoundItemRepository;
import com.wasuremwono.repository.LostItemRepository;
import com.wasuremwono.repository.MatchRepository;
import com.wasuremwono.util.PhoneMaskingUtil;
import com.wasuremwono.util.ResourceNotFoundException;
import com.wasuremwono.util.SafetyGuideUtil;

@Service
public class NotificationService {
	private final MatchRepository matchRepository;
	private final LostItemRepository lostItemRepository;
	private final FoundItemRepository foundItemRepository;
	private final ContactRepository contactRepository;
	private final RewardService rewardService;

	public NotificationService(
		MatchRepository matchRepository,
		LostItemRepository lostItemRepository,
		FoundItemRepository foundItemRepository,
		ContactRepository contactRepository,
		RewardService rewardService
	) {
		this.matchRepository = matchRepository;
		this.lostItemRepository = lostItemRepository;
		this.foundItemRepository = foundItemRepository;
		this.contactRepository = contactRepository;
		this.rewardService = rewardService;
	}

	public EmailPreview generateMatchEmailPreview(String matchId) {
		MatchResult match = matchRepository.findById(matchId)
			.orElseThrow(() -> new ResourceNotFoundException("Match not found: " + matchId));
		LostItem lostItem = lostItemRepository.findById(match.getLostItemId())
			.orElseThrow(() -> new ResourceNotFoundException("Lost item not found for match: " + matchId));
		FoundItem foundItem = foundItemRepository.findById(match.getFoundItemId())
			.orElseThrow(() -> new ResourceNotFoundException("Found item not found for match: " + matchId));
		var ownerContact = contactRepository.findById(lostItem.getOwnerContactId())
			.orElseThrow(() -> new ResourceNotFoundException("Owner contact not found for match: " + matchId));
		var finderContact = contactRepository.findById(foundItem.getFinderContactId())
			.orElseThrow(() -> new ResourceNotFoundException("Finder contact not found for match: " + matchId));
		RewardStatusResponse rewardStatus = rewardService.getRewardStatus(matchId);

		String finderContactLine = finderContact.isAllowPhoneContact()
			? "Finder contact: " + PhoneMaskingUtil.mask(finderContact.getPhoneNumber())
			: "Finder contact: The finder did not allow phone contact.";

		String body = """
			Hello, this is Mono from Wasuremono. We found a possible match for your lost item.

			Lost item summary:
			%s

			Found item summary:
			%s

			Matching possibility:
			Score %d (%s)

			Matching reasons:
			%s

			%s

			Ownership verification questions:
			%s

			Safety guide:
			%s

			Reward:
			Reward amount: %s %s
			Platform fee: %s %s
			Finder receives: %s %s

			Mock escrow status:
			%s
			%s
			""".formatted(
			summarizeLostItem(lostItem),
			summarizeFoundItem(foundItem),
			match.getMatchScore(),
			match.getMatchLevel(),
			String.join("\n", prefixLines(match.getReasons())),
			finderContactLine,
			String.join("\n", prefixLines(match.getVerificationQuestions())),
			String.join("\n", prefixLines(SafetyGuideUtil.defaultSafetyGuide())),
			rewardStatus.getRewardAmount(),
			rewardStatus.getCurrency(),
			rewardStatus.getPlatformFee(),
			rewardStatus.getCurrency(),
			rewardStatus.getFinderReceives(),
			rewardStatus.getCurrency(),
			rewardStatus.getPaymentStatus(),
			rewardStatus.getNote()
		);

		List<String> includedInfo = new ArrayList<>(List.of(
			"found item summary",
			"lost item summary",
			"match score",
			"matching reasons",
			"ownership verification questions",
			"safety guide",
			"reward amount",
			"mock escrow status"
		));
		if (finderContact.isAllowPhoneContact()) {
			includedInfo.add("finder phone number masked");
		}

		return EmailPreview.builder()
			.emailStatus("EMAIL_PREVIEW_GENERATED")
			.recipient(ownerContact.getEmail())
			.subject("[Wasuremono] Possible match found for your lost item")
			.body(body)
			.includedInfo(includedInfo)
			.build();
	}

	private String summarizeLostItem(LostItem lostItem) {
		return "Lost item near " + lostItem.getStructuredData().getPossibleLostLocation()
			+ ". Features: " + String.join(", ", lostItem.getStructuredData().getFeatures())
			+ ". Original description: " + lostItem.getDescription();
	}

	private String summarizeFoundItem(FoundItem foundItem) {
		return "Found item at " + foundItem.getStructuredData().getFoundLocation()
			+ ". Features: " + String.join(", ", foundItem.getStructuredData().getFeatures())
			+ ". Handover status: " + nullToUnknown(foundItem.getHandoverStatus())
			+ ". Finder description: " + foundItem.getDescription();
	}

	private List<String> prefixLines(List<String> lines) {
		return lines.stream().map(line -> "- " + line).toList();
	}

	private String nullToUnknown(String value) {
		return value == null || value.isBlank() ? "unknown" : value;
	}
}
