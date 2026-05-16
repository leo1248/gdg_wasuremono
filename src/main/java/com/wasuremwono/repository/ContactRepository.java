package com.wasuremwono.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.wasuremwono.model.Contact;
import com.wasuremwono.repository.contact.ContactStore;
import com.wasuremwono.util.IdGenerator;

@Repository
public class ContactRepository {
	private final ContactStore contactStore;

	public ContactRepository(ContactStore contactStore) {
		this.contactStore = contactStore;
	}

	public Contact save(Contact contact) {
		if (contact.getId() == null) {
			contact.setId(IdGenerator.nextId("contact-"));
		}
		if (contact.getCreatedAt() == null) {
			contact.setCreatedAt(LocalDateTime.now().toString());
		}
		return contactStore.save(contact);
	}

	public Optional<Contact> findById(String id) {
		return contactStore.findById(id);
	}
}
