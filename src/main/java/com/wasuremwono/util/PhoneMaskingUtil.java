package com.wasuremwono.util;

public final class PhoneMaskingUtil {
	private PhoneMaskingUtil() {
	}

	public static String mask(String phoneNumber) {
		if (phoneNumber == null || phoneNumber.isBlank()) {
			return null;
		}

		String[] parts = phoneNumber.split("-");
		if (parts.length >= 3) {
			parts[parts.length - 2] = "****";
			return String.join("-", parts);
		}

		String digits = phoneNumber.replaceAll("\\D", "");
		if (digits.length() <= 4) {
			return "****";
		}
		String lastFour = digits.substring(digits.length() - 4);
		return "****" + lastFour;
	}
}
