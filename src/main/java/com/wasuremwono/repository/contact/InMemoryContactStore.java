package com.wasuremwono.repository.contact;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.wasuremwono.model.Contact;

@Component
@Profile("!gcp")
public class InMemoryContactStore implements ContactStore {
	private final Map<String, Contact> contacts = new ConcurrentHashMap<>();

	@Override
	public Contact save(Contact contact) {
		contacts.put(contact.getId(), contact);
		return contact;
	}

	@Override
	public Optional<Contact> findById(String id) {
		return Optional.ofNullable(contacts.get(id));
	}
}
