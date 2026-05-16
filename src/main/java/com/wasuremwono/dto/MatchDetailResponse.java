package com.wasuremwono.dto;

import java.util.ArrayList;
import java.util.List;

import com.wasuremwono.model.MatchLevel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MatchDetailResponse {
	private String matchId;
	private String lostItemId;
	private String foundItemId;
	private int matchScore;
	private MatchLevel matchLevel;
	@Builder.Default
	private List<String> reasons = new ArrayList<>();
	@Builder.Default
	private List<String> verificationQuestions = new ArrayList<>();
	private boolean contactAvailable;
	private String finderPhoneMasked;
}
