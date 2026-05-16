package com.wasuremwono.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class MatchEmailRequest {
	@NotBlank
	private String matchId;
}
