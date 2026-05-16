package com.wasuremwono.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
public class RewardPostRequest {
	@NotBlank
	private String lostItemId;
	@PositiveOrZero
	@DecimalMax(value = "1000000000.00")
	private BigDecimal rewardAmount;
	private String area;
}
