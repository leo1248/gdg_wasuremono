package com.wasuremwono.repository.storage;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.StreamSupport;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageException;

@Component
@Profile("gcp")
public class GcsJsonObjectStore implements JsonObjectStore {
	private static final String JSON_CONTENT_TYPE = "application/json";

	private final Storage storage;
	private final ObjectMapper objectMapper;
	private final GcsStorageProperties properties;

	public GcsJsonObjectStore(Storage storage, ObjectMapper objectMapper, GcsStorageProperties properties) {
		if (properties.bucket() == null || properties.bucket().isBlank()) {
			throw new IllegalStateException("wasuremwono.storage.bucket must be set for the gcp profile");
		}
		this.storage = storage;
		this.objectMapper = objectMapper;
		this.properties = properties;
	}

	@Override
	public <T> T save(String collection, String id, T value) {
		try {
			storage.create(blobInfo(collection, id), objectMapper.writeValueAsBytes(value));
			return value;
		} catch (Exception ex) {
			throw new IllegalStateException("Failed to save " + collection + "/" + id + " to Cloud Storage", ex);
		}
	}

	@Override
	public <T> T saveIfAbsent(String collection, String id, T value, Class<T> type) {
		try {
			storage.create(
				blobInfo(collection, id),
				objectMapper.writeValueAsBytes(value),
				Storage.BlobTargetOption.doesNotExist());
			return value;
		} catch (StorageException ex) {
			if (ex.getCode() == 412) {
				return findById(collection, id, type)
					.orElseThrow(() -> new IllegalStateException("Object already existed but could not be read: " + collection + "/" + id, ex));
			}
			throw new IllegalStateException("Failed to create " + collection + "/" + id + " in Cloud Storage", ex);
		} catch (Exception ex) {
			throw new IllegalStateException("Failed to create " + collection + "/" + id + " in Cloud Storage", ex);
		}
	}

	@Override
	public <T> Optional<T> findById(String collection, String id, Class<T> type) {
		Blob blob = storage.get(BlobId.of(properties.bucket(), objectName(collection, id)));
		if (blob == null || !blob.exists()) {
			return Optional.empty();
		}
		return Optional.of(read(blob, type));
	}

	@Override
	public <T> Collection<T> findAll(String collection, Class<T> type) {
		String prefix = collectionPrefix(collection);
		return StreamSupport.stream(
				storage.list(properties.bucket(), Storage.BlobListOption.prefix(prefix)).iterateAll().spliterator(),
				false)
			.filter(blob -> !blob.isDirectory())
			.filter(blob -> blob.getName().endsWith(".json"))
			.map(blob -> read(blob, type))
			.toList();
	}

	private <T> T read(Blob blob, Class<T> type) {
		try {
			return objectMapper.readValue(blob.getContent(), type);
		} catch (Exception ex) {
			throw new IllegalStateException("Failed to read " + blob.getName() + " from Cloud Storage", ex);
		}
	}

	private BlobInfo blobInfo(String collection, String id) {
		return BlobInfo.newBuilder(properties.bucket(), objectName(collection, id))
			.setContentType(JSON_CONTENT_TYPE)
			.build();
	}

	private String objectName(String collection, String id) {
		return collectionPrefix(collection) + id + ".json";
	}

	private String collectionPrefix(String collection) {
		String prefix = properties.normalizedPrefix();
		if (prefix.isEmpty()) {
			return collection + "/";
		}
		return prefix + "/" + collection + "/";
	}
}
