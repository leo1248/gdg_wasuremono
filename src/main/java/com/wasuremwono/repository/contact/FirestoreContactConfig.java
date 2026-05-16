package com.wasuremwono.repository.contact;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreOptions;

@Configuration
@Profile("gcp")
@EnableConfigurationProperties(FirestoreContactProperties.class)
public class FirestoreContactConfig {
	@Bean
	public Firestore firestore() {
		return FirestoreOptions.getDefaultInstance().getService();
	}
}
