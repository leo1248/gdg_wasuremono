package com.wasuremwono.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wasuremwono.dto.RewardStatusResponse;
import com.wasuremwono.service.RewardService;

@RestController
@RequestMapping("/api/rewards")
public class RewardController {
	private final RewardService rewardService;

	public RewardController(RewardService rewardService) {
		this.rewardService = rewardService;
	}

	@GetMapping("/{matchId}")
	public RewardStatusResponse getRewardStatus(@PathVariable String matchId) {
		return rewardService.getRewardStatus(matchId);
	}
}
