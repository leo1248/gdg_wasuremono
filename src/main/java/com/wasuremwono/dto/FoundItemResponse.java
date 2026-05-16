package com.wasuremwono.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.wasuremwono.model.ContactInfo;
import com.wasuremwono.model.EmailPreview;
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
public class FoundItemResponse {
	private String foundItemId;
	private StructuredItemData structuredData;
	private ContactInfo contactInfo;
	private MatchSummaryResponse matchResult;
	private AiImageResponse aiImage;
	private EmailPreview emailPreview;
	private String message;
}
