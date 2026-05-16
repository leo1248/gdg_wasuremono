package com.wasuremwono.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RewardStatusResponse {
	private BigDecimal rewardAmount;
	private BigDecimal platformFee;
	private BigDecimal finderReceives;
	private String currency;
	private String paymentStatus;
	private String note;
}
