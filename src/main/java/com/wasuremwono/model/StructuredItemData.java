package com.wasuremwono.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StructuredItemData {
	private String possibleLostLocation;
	private String foundLocation;
	private String locationType;
	private String lostTime;
	private String foundTime;
	@Builder.Default
	private List<String> features = new ArrayList<>();
	private RiskLevel riskLevel;
	@Builder.Default
	private List<String> recommendedRoute = new ArrayList<>();
	private String recommendedAction;
	private BigDecimal rewardAmount;
}
