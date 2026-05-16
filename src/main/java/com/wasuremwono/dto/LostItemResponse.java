package com.wasuremwono.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.wasuremwono.model.ContactInfo;
import com.wasuremwono.model.MatchStatus;
import com.wasuremwono.model.StructuredItemData;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LostItemResponse {
	private String lostItemId;
	private StructuredItemData structuredData;
	private ContactInfo contactInfo;
	private MatchStatus matchStatus;
	private MatchSummaryResponse matchResult;
	private AiMatchResponse aiMatch;
	private String message;
}
