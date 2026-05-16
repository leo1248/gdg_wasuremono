package com.wasuremwono.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LostItemRequest {
	@NotBlank
	private String description;
	private BigDecimal rewardAmount;
	private String preferredLanguage;
	private String name;
	@Email
	private String email;
	private String phoneNumber;
	private boolean allowPhoneContact;
	private boolean allowEmailNotification;
}
