package com.wasuremwono.model;

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
public class MatchResult {
	private String id;

	private String lostItemId;

	private String foundItemId;

	private int matchScore;

	private MatchLevel matchLevel;

	@Builder.Default
	private List<String> reasons = new ArrayList<>();

	@Builder.Default
	private List<String> verificationQuestions = new ArrayList<>();

	private LocalDateTime createdAt;
}
