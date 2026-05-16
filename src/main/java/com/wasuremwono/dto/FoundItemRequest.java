package com.wasuremwono.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class FoundItemRequest {
	@NotBlank
	private String description;
	private String foundLocation;
	private String foundTime;
	private String handoverStatus;
	@JsonProperty("image_path")
	private String imagePath;
	private String name;
	private String phoneNumber;
	private boolean allowPhoneContact;
}
