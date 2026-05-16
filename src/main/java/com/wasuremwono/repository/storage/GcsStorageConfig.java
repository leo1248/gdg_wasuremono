package com.wasuremwono.repository.storage;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

@Configuration
@Profile("gcp")
@EnableConfigurationProperties(GcsStorageProperties.class)
public class GcsStorageConfig {
	@Bean
	public Storage googleCloudStorage() {
		return StorageOptions.getDefaultInstance().getService();
	}
}
