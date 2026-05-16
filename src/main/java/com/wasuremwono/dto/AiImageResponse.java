package com.wasuremwono.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AiImageResponse {
	@JsonProperty("item_id")
	private String itemId;
	@JsonProperty("image_path")
	private String imagePath;
	@JsonProperty("image_gcs_uri")
	private String imageGcsUri;
	@JsonProperty("summary_gcs_uri")
	private String summaryGcsUri;
	@JsonProperty("metadata_gcs_uri")
	private String metadataGcsUri;
}

