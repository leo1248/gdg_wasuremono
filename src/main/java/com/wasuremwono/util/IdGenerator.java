package com.wasuremwono.util;

import java.util.UUID;

public final class IdGenerator {
	private IdGenerator() {
	}

	public static String nextId(String prefix) {
		return prefix + UUID.randomUUID();
	}
}
