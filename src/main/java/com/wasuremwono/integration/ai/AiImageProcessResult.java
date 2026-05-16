package com.wasuremwono.integration.ai;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AiImageProcessResult(
	@JsonProperty("item_id")
	String itemId,
	@JsonProperty("local_image_path")
	String localImagePath,
	@JsonProperty("image_gcs_uri")
	String imageGcsUri,
	@JsonProperty("summary_gcs_uri")
	String summaryGcsUri,
	@JsonProperty("metadata_gcs_uri")
	String metadataGcsUri,
	String summary
) {
}

