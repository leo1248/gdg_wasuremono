package com.wasuremwono.repository.contact;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "wasuremwono.firebase")
public record FirestoreContactProperties(
	String contactsCollection
) {
	public String collectionName() {
		return contactsCollection == null || contactsCollection.isBlank() ? "contacts" : contactsCollection;
	}
}
