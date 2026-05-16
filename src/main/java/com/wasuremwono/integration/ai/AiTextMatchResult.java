package com.wasuremwono.integration.ai;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AiTextMatchResult(
	boolean matched,
	Integer number,
	@JsonProperty("item_id")
	String itemId,
	@JsonProperty("metadata_blob")
	String metadataBlob,
	@JsonProperty("image_gcs_uri")
	String imageGcsUri,
	@JsonProperty("summary_gcs_uri")
	String summaryGcsUri
) {
	public static AiTextMatchResult noMatch() {
		return new AiTextMatchResult(false, null, null, null, null, null);
	}
}

