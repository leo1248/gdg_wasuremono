package com.wasuremwono.repository.contact;

import java.util.Optional;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.wasuremwono.model.Contact;

@Component
@Profile("gcp")
public class FirestoreContactStore implements ContactStore {
	private final Firestore firestore;
	private final FirestoreContactProperties properties;

	public FirestoreContactStore(Firestore firestore, FirestoreContactProperties properties) {
		this.firestore = firestore;
		this.properties = properties;
	}

	@Override
	public Contact save(Contact contact) {
		try {
			firestore.collection(properties.collectionName())
				.document(contact.getId())
				.set(contact)
				.get();
			return contact;
		} catch (Exception ex) {
			throw new IllegalStateException("Failed to save contact to Firestore: " + contact.getId(), ex);
		}
	}

	@Override
	public Optional<Contact> findById(String id) {
		try {
			DocumentSnapshot snapshot = firestore.collection(properties.collectionName())
				.document(id)
				.get()
				.get();
			if (!snapshot.exists()) {
				return Optional.empty();
			}
			return Optional.ofNullable(snapshot.toObject(Contact.class));
		} catch (Exception ex) {
			throw new IllegalStateException("Failed to read contact from Firestore: " + id, ex);
		}
	}
}
