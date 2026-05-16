package com.wasuremwono.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.wasuremwono.dto.MatchDetailResponse;
import com.wasuremwono.dto.MatchSummaryResponse;
import com.wasuremwono.model.FoundItem;
import com.wasuremwono.model.LostItem;
import com.wasuremwono.model.MatchLevel;
import com.wasuremwono.model.MatchResult;
import com.wasuremwono.repository.ContactRepository;
import com.wasuremwono.repository.FoundItemRepository;
import com.wasuremwono.repository.LostItemRepository;
import com.wasuremwono.repository.MatchRepository;
import com.wasuremwono.util.PhoneMaskingUtil;
import com.wasuremwono.util.ResourceNotFoundException;
import com.wasuremwono.util.TextUtil;

@Service
public class MatchingService {
	private final LostItemRepository lostItemRepository;
	private final FoundItemRepository foundItemRepository;
	private final MatchRepository matchRepository;
	private final ContactRepository contactRepository;
	private final MonoStructuringService monoStructuringService;

	public MatchingService(
		LostItemRepository lostItemRepository,
		FoundItemRepository foundItemRepository,
		MatchRepository matchRepository,
		ContactRepository contactRepository,
		MonoStructuringService monoStructuringService
	) {
		this.lostItemRepository = lostItemRepository;
		this.foundItemRepository = foundItemRepository;
		this.matchRepository = matchRepository;
		this.contactRepository = contactRepository;
		this.monoStructuringService = monoStructuringService;
	}

	public Optional<MatchResult> findAndSaveHighMatchForLostItem(LostItem lostItem) {
		return foundItemRepository.findAll().stream()
			.map(foundItem -> calculate(lostItem, foundItem))
			.filter(match -> match.getMatchScore() >= 80)
			.max(Comparator.comparingInt(MatchResult::getMatchScore))
			.map(matchRepository::saveIfAbsentByItemIds);
	}

	public Optional<MatchResult> findAndSaveHighMatchForFoundItem(FoundItem foundItem) {
		return lostItemRepository.findAll().stream()
			.map(lostItem -> calculate(lostItem, foundItem))
			.filter(match -> match.getMatchScore() >= 80)
			.max(Comparator.comparingInt(MatchResult::getMatchScore))
			.map(matchRepository::saveIfAbsentByItemIds);
	}

	public MatchDetailResponse getMatchDetail(String matchId) {
		MatchResult match = matchRepository.findById(matchId)
			.orElseThrow(() -> new ResourceNotFoundException("Match not found: " + matchId));
		FoundItem foundItem = foundItemRepository.findById(match.getFoundItemId())
			.orElseThrow(() -> new ResourceNotFoundException("Found item not found for match: " + matchId));
		var finderContact = contactRepository.findById(foundItem.getFinderContactId())
			.orElseThrow(() -> new ResourceNotFoundException("Finder contact not found for match: " + matchId));

		return MatchDetailResponse.builder()
			.matchId(match.getId())
			.lostItemId(match.getLostItemId())
			.foundItemId(match.getFoundItemId())
			.matchScore(match.getMatchScore())
			.matchLevel(match.getMatchLevel())
			.reasons(match.getReasons())
			.verificationQuestions(match.getVerificationQuestions())
			.contactAvailable(finderContact.isAllowPhoneContact())
			.finderPhoneMasked(finderContact.isAllowPhoneContact() ? PhoneMaskingUtil.mask(finderContact.getPhoneNumber()) : null)
			.build();
	}

	public MatchSummaryResponse toSummary(MatchResult match) {
		return MatchSummaryResponse.builder()
			.matchId(match.getId())
			.lostItemId(match.getLostItemId())
			.foundItemId(match.getFoundItemId())
			.matchScore(match.getMatchScore())
			.matchLevel(match.getMatchLevel())
			.reasons(match.getReasons())
			.verificationQuestions(match.getVerificationQuestions())
			.build();
	}

	private MatchResult calculate(LostItem lostItem, FoundItem foundItem) {
		int score = 0;
		List<String> reasons = new ArrayList<>();

		int featureScore = featureMatchScore(lostItem.getStructuredData().getFeatures(), foundItem.getStructuredData().getFeatures());
		if (featureScore > 0) {
			score += featureScore;
			reasons.add("Item features overlap.");
		}

		if (hasOverlap(lostItem.getStructuredData().getPossibleLostLocation(), foundItem.getStructuredData().getFoundLocation())) {
			score += 25;
			reasons.add("Location text overlaps between lost and found reports.");
		}

		if (isCloseTime(lostItem.getStructuredData().getLostTime(), foundItem.getStructuredData().getFoundTime())) {
			score += 15;
			reasons.add("Lost time and found time appear to be the same day or close.");
		}

		return MatchResult.builder()
			.lostItemId(lostItem.getId())
			.foundItemId(foundItem.getId())
			.matchScore(score)
			.matchLevel(toLevel(score))
			.reasons(reasons.isEmpty() ? List.of("No strong matching signals were found.") : reasons)
			.verificationQuestions(monoStructuringService.defaultVerificationQuestions())
			.createdAt(LocalDateTime.now())
			.build();
	}

	private MatchLevel toLevel(int score) {
		if (score >= 80) {
			return MatchLevel.HIGH_MATCH;
		}
		if (score >= 60) {
			return MatchLevel.POSSIBLE_MATCH;
		}
		return MatchLevel.NO_STRONG_MATCH;
	}

	private boolean hasOverlap(String first, String second) {
		Set<String> firstTokens = TextUtil.tokens(first);
		Set<String> secondTokens = TextUtil.tokens(second);
		firstTokens.retainAll(secondTokens);
		return !firstTokens.isEmpty();
	}

	private boolean isCloseTime(String lostTime, String foundTime) {
		if (lostTime == null || foundTime == null) {
			return false;
		}
		String lost = TextUtil.normalize(lostTime);
		String found = TextUtil.normalize(foundTime);
		if (lost.isBlank() || found.isBlank() || lost.contains("unknown") || found.contains("unknown")) {
			return false;
		}
		if (lost.equals(found)) {
			return true;
		}
		Set<String> lostTokens = TextUtil.tokens(lost);
		Set<String> foundTokens = TextUtil.tokens(found);
		lostTokens.retainAll(foundTokens);
		return !lostTokens.isEmpty();
	}

	private int featureMatchScore(List<String> first, List<String> second) {
		Set<String> firstSet = new HashSet<>(first == null ? List.of() : first);
		Set<String> secondSet = new HashSet<>(second == null ? List.of() : second);
		firstSet.retainAll(secondSet);
		if (firstSet.isEmpty()) {
			return 0;
		}
		return firstSet.size() >= 3 ? 60 : 45;
	}
}
