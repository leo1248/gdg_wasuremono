package com.wasuremwono.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.wasuremwono.model.RewardPostStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RewardPostResponse {
	private String rewardPostId;
	private String title;
	private String description;
	private BigDecimal rewardAmount;
	private String area;
	@Builder.Default
	private List<String> languages = new ArrayList<>();
	private RewardPostStatus status;
	private String safetyNote;
}
