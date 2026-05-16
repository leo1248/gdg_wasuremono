package com.wasuremwono.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wasuremwono.dto.MatchDetailResponse;
import com.wasuremwono.service.MatchingService;

@RestController
@RequestMapping("/api/matches")
public class MatchController {
	private final MatchingService matchingService;

	public MatchController(MatchingService matchingService) {
		this.matchingService = matchingService;
	}

	@GetMapping("/{matchId}")
	public MatchDetailResponse getMatch(@PathVariable String matchId) {
		return matchingService.getMatchDetail(matchId);
	}
}
