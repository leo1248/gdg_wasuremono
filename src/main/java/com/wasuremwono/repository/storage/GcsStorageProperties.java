package com.wasuremwono.repository.storage;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "wasuremwono.storage")
public record GcsStorageProperties(
	String bucket,
	String prefix
) {
	public String normalizedPrefix() {
		if (prefix == null || prefix.isBlank()) {
			return "";
		}
		return prefix.replaceAll("^/+", "").replaceAll("/+$", "");
	}
}
