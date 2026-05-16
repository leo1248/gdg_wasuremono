package com.wasuremwono.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.wasuremwono.model.RiskLevel;
import com.wasuremwono.model.StructuredItemData;
import com.wasuremwono.util.TextUtil;

@Service
public class MonoStructuringService {
	private static final List<String> PUBLIC_PLACES = List.of(
		"subway station", "airport", "street", "park", "tourist attraction",
		"bus stop", "train station", "station"
	);
	private static final List<String> PRIVATE_BUSINESSES = List.of(
		"cafe", "restaurant", "hotel", "store", "shopping mall", "convenience store", "mall"
	);
	private static final List<String> FEATURE_WORDS = List.of(
		"keychain", "sticker", "logo", "brand", "inside", "card", "cards", "cash",
		"zipper", "strap", "case", "cover", "scratch", "receipt", "charm", "tag"
	);

	public StructuredItemData structureLostItem(String description, BigDecimal rewardAmount) {
		String location = inferLocation(description, null);
		String locationType = classifyLocationType(description + " " + location);
		List<String> features = extractFeatures(description);

		return StructuredItemData.builder()
			.possibleLostLocation(location)
			.locationType(locationType)
			.lostTime(inferTime(description))
			.features(features)
			.riskLevel(classifyRisk(features))
			.recommendedRoute(recommendRoute(locationType))
			.rewardAmount(defaultReward(rewardAmount))
			.build();
	}

	public StructuredItemData structureFoundItem(String description, String foundLocation, String foundTime) {
		String combined = description + " " + nullSafe(foundLocation);
		String location = inferLocation(combined, foundLocation);
		String locationType = classifyLocationType(combined + " " + location);
		List<String> features = extractFeatures(combined);

		return StructuredItemData.builder()
			.foundLocation(location)
			.locationType(locationType)
			.foundTime(firstNonBlank(foundTime, inferTime(description)))
			.features(features)
			.riskLevel(classifyRisk(features))
			.recommendedAction(recommendAction(locationType, features))
			.build();
	}

	public List<String> defaultVerificationQuestions() {
		return List.of(
			"What is the inside color of the item?",
			"What specific cards or objects were inside?",
			"Was there a keychain or sticker?",
			"What brand or logo was on the item?",
			"Where did you last use the item?"
		);
	}

	public String generateRewardPostTitle(StructuredItemData data) {
		return "Lost item near " + data.getPossibleLostLocation();
	}

	public String generateRewardPostDescription(StructuredItemData data) {
		return "Mono is helping look for an item."
			+ " Possible last known area: " + data.getPossibleLostLocation()
			+ ". Key details: " + String.join(", ", data.getFeatures())
			+ ". Please use safe official handover locations for sensitive items.";
	}

	private String inferLocation(String text, String explicitLocation) {
		if (explicitLocation != null && !explicitLocation.isBlank()) {
			return explicitLocation;
		}
		for (String place : PUBLIC_PLACES) {
			if (TextUtil.containsAny(text, place)) {
				return place;
			}
		}
		for (String place : PRIVATE_BUSINESSES) {
			if (TextUtil.containsAny(text, place)) {
				return place;
			}
		}
		return "unknown area";
	}

	private String classifyLocationType(String text) {
		boolean publicCandidate = PUBLIC_PLACES.stream().anyMatch(place -> TextUtil.containsAny(text, place));
		boolean privateCandidate = PRIVATE_BUSINESSES.stream().anyMatch(place -> TextUtil.containsAny(text, place));
		if (hasMixedLocationCandidates(publicCandidate, privateCandidate) || hasNoLocationCandidates(publicCandidate, privateCandidate)) {
			return "Uncertain: public place and private business candidates";
		}
		return publicCandidate ? "Public place" : "Private business";
	}

	private boolean hasMixedLocationCandidates(boolean publicCandidate, boolean privateCandidate) {
		return publicCandidate && privateCandidate;
	}

	private boolean hasNoLocationCandidates(boolean publicCandidate, boolean privateCandidate) {
		return !publicCandidate && !privateCandidate;
	}

	private String inferTime(String text) {
		if (TextUtil.containsAny(text, "today")) {
			return "today";
		}
		if (TextUtil.containsAny(text, "yesterday")) {
			return "yesterday";
		}
		if (TextUtil.containsAny(text, "last night")) {
			return "last night";
		}
		if (TextUtil.containsAny(text, "morning")) {
			return "morning";
		}
		if (TextUtil.containsAny(text, "afternoon")) {
			return "afternoon";
		}
		if (TextUtil.containsAny(text, "evening")) {
			return "evening";
		}
		return "unknown time";
	}

	private List<String> extractFeatures(String text) {
		Set<String> features = new LinkedHashSet<>();
		for (String feature : FEATURE_WORDS) {
			if (TextUtil.containsAny(text, feature)) {
				features.add(feature);
			}
		}
		TextUtil.tokens(text).stream()
			.filter(token -> token.length() >= 5)
			.limit(6)
			.forEach(features::add);
		if (features.isEmpty()) {
			features.add("details from user description");
		}
		return new ArrayList<>(features);
	}

	private RiskLevel classifyRisk(List<String> features) {
		String featureText = String.join(" ", features);
		if (TextUtil.containsAny(featureText, "passport", "wallet", "phone", "id", "credit", "transportation", "student")) {
			return RiskLevel.HIGH;
		}
		if (TextUtil.containsAny(featureText, "bag", "laptop", "tablet", "camera")) {
			return RiskLevel.MEDIUM;
		}
		if (TextUtil.containsAny(featureText, "umbrella", "bottle", "hat", "clothes")) {
			return RiskLevel.LOW;
		}
		return RiskLevel.UNKNOWN;
	}

	private List<String> recommendRoute(String locationType) {
		if ("Public place".equals(locationType)) {
			return List.of(
				"Check public lost and found center",
				"Check police lost and found",
				"Check transportation office if relevant"
			);
		}
		if ("Private business".equals(locationType)) {
			return List.of(
				"Contact the store or business",
				"Contact the hotel front desk or counter if relevant",
				"Use official handover if sensitive item"
			);
		}
		return List.of(
			"Ask the most recent private business visited",
			"Check transportation lost and found center",
			"Check local police lost and found",
			"Create a reward post if no match exists"
		);
	}

	private String recommendAction(String locationType, List<String> features) {
		String sensitiveNote = classifyRisk(features) == RiskLevel.HIGH
			? " Use an official handover location because this may be a sensitive item."
			: "";
		if ("Public place".equals(locationType)) {
			return "Report to the closest public lost and found center or transportation office." + sensitiveNote;
		}
		if ("Private business".equals(locationType)) {
			return "Keep the item at the business counter or front desk for safe pickup." + sensitiveNote;
		}
		return "Keep the item safe and use official handover if the owner is verified." + sensitiveNote;
	}

	private BigDecimal defaultReward(BigDecimal rewardAmount) {
		return rewardAmount == null ? BigDecimal.ZERO : rewardAmount;
	}

	private String firstNonBlank(String first, String second) {
		return first != null && !first.isBlank() ? first : second;
	}

	private String nullSafe(String value) {
		return value == null ? "" : value;
	}

}
