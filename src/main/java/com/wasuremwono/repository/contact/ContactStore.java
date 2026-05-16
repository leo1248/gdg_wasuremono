package com.wasuremwono.repository.contact;

import java.util.Optional;

import com.wasuremwono.model.Contact;

public interface ContactStore {
	Contact save(Contact contact);

	Optional<Contact> findById(String id);
}
