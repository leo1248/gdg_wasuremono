package com.wasuremwono.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AiMatchResponse {
	private boolean matched;
	private Integer number;
	@JsonProperty("item_id")
	private String itemId;
	@JsonProperty("metadata_blob")
	private String metadataBlob;
	@JsonProperty("image_gcs_uri")
	private String imageGcsUri;
	@JsonProperty("summary_gcs_uri")
	private String summaryGcsUri;
}

