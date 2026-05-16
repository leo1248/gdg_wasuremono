package com.wasuremwono.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wasuremwono.dto.RewardPostRequest;
import com.wasuremwono.dto.RewardPostResponse;
import com.wasuremwono.service.RewardPostService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/reward-posts")
public class RewardPostController {
	private final RewardPostService rewardPostService;

	public RewardPostController(RewardPostService rewardPostService) {
		this.rewardPostService = rewardPostService;
	}

	@PostMapping
	public RewardPostResponse createRewardPost(@Valid @RequestBody RewardPostRequest request) {
		return rewardPostService.createRewardPost(request);
	}

	@GetMapping
	public List<RewardPostResponse> getRewardPosts() {
		return rewardPostService.getAllRewardPosts();
	}
}
