package com.wasuremwono.util;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

public final class TextUtil {
	private TextUtil() {
	}

	public static String normalize(String value) {
		return value == null ? "" : value.toLowerCase(Locale.ROOT).trim();
	}

	public static boolean containsAny(String text, String... candidates) {
		String normalized = normalize(text);
		return Arrays.stream(candidates).anyMatch(candidate -> normalized.contains(normalize(candidate)));
	}

	public static Set<String> tokens(String text) {
		return Arrays.stream(normalize(text).split("[^a-z0-9]+"))
			.filter(token -> token.length() >= 3)
			.collect(Collectors.toCollection(LinkedHashSet::new));
	}
}
