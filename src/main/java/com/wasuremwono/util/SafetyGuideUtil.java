package com.wasuremwono.util;

import java.util.List;

public final class SafetyGuideUtil {
	private SafetyGuideUtil() {
	}

	public static List<String> defaultSafetyGuide() {
		return List.of(
			"Do not share passport numbers, ID numbers, credit card numbers, full addresses, or other sensitive personal information.",
			"For sensitive items such as wallets, phones, IDs, passports, and credit cards, use official handover locations such as police stations, subway station offices, hotel front desks, airport lost and found offices, or store counters.",
			"Release the reward only after the item is safely recovered."
		);
	}

	public static String safetyNote() {
		return String.join(" ", defaultSafetyGuide());
	}
}
