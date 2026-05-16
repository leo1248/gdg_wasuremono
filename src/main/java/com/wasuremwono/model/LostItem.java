package com.wasuremwono.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LostItem {
	private String id;

	private String description;

	private BigDecimal rewardAmount;
	private String preferredLanguage;
	private String ownerContactId;

	private StructuredItemData structuredData;

	private LocalDateTime createdAt;
}
