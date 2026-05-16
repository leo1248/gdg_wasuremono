package com.wasuremwono.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RewardPost {
	private String id;

	private String lostItemId;

	private String title;

	private String description;

	private BigDecimal rewardAmount;

	private String area;

	@Builder.Default
	private List<String> languages = new ArrayList<>();

	private RewardPostStatus status;

	private String safetyNote;

	private LocalDateTime createdAt;
}
