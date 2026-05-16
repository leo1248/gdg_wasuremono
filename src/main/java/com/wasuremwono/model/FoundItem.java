package com.wasuremwono.model;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FoundItem {
	private String id;

	private String description;

	private String foundLocation;
	private String foundTime;
	private String handoverStatus;
	private String imagePath;
	private String aiItemId;
	private String imageGcsUri;
	private String summaryGcsUri;
	private String metadataGcsUri;
	private String finderContactId;

	private StructuredItemData structuredData;

	private LocalDateTime createdAt;
}
