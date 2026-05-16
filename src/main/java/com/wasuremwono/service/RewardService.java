package com.wasuremwono.service;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.springframework.stereotype.Service;

import com.wasuremwono.dto.RewardStatusResponse;
import com.wasuremwono.model.LostItem;
import com.wasuremwono.model.MatchResult;
import com.wasuremwono.repository.LostItemRepository;
import com.wasuremwono.repository.MatchRepository;
import com.wasuremwono.util.ResourceNotFoundException;

@Service
public class RewardService {
	public static final String PAYMENT_STATUS = "PENDING_UNTIL_RECOVERY_CONFIRMATION";
	public static final String MOCK_ESCROW_NOTE = "This is a mock escrow simulation for the hackathon MVP. No real payment is processed.";

	private final MatchRepository matchRepository;
	private final LostItemRepository lostItemRepository;

	public RewardService(MatchRepository matchRepository, LostItemRepository lostItemRepository) {
		this.matchRepository = matchRepository;
		this.lostItemRepository = lostItemRepository;
	}

	public RewardStatusResponse getRewardStatus(String matchId) {
		MatchResult match = matchRepository.findById(matchId)
			.orElseThrow(() -> new ResourceNotFoundException("Match not found: " + matchId));
		LostItem lostItem = lostItemRepository.findById(match.getLostItemId())
			.orElseThrow(() -> new ResourceNotFoundException("Lost item not found for match: " + matchId));

		BigDecimal rewardAmount = lostItem.getRewardAmount() == null ? BigDecimal.ZERO : lostItem.getRewardAmount();
		BigDecimal platformFee = rewardAmount.multiply(new BigDecimal("0.10")).setScale(2, RoundingMode.HALF_UP);
		BigDecimal finderReceives = rewardAmount.subtract(platformFee).setScale(2, RoundingMode.HALF_UP);

		return RewardStatusResponse.builder()
			.rewardAmount(rewardAmount)
			.platformFee(platformFee)
			.finderReceives(finderReceives)
			.currency("KRW")
			.paymentStatus(PAYMENT_STATUS)
			.note(MOCK_ESCROW_NOTE)
			.build();
	}
}
