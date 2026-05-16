package com.wasuremwono.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.wasuremwono.model.FoundItem;
import com.wasuremwono.model.LostItem;
import com.wasuremwono.model.MatchResult;
import com.wasuremwono.model.StructuredItemData;
import com.wasuremwono.repository.FoundItemRepository;
import com.wasuremwono.repository.LostItemRepository;
import com.wasuremwono.repository.MatchRepository;

@SpringBootTest
class MatchingServiceTest {
	@Autowired
	private LostItemRepository lostItemRepository;
	@Autowired
	private FoundItemRepository foundItemRepository;
	@Autowired
	private MatchRepository matchRepository;
	@Autowired
	private MatchingService matchingService;

	@Test
	void doesNotCreateDuplicateMatchForSameLostAndFoundPair() {
		LostItem lostItem = lostItemRepository.save(LostItem.builder()
			.description("Lost black wallet at subway station today with a logo card")
			.structuredData(StructuredItemData.builder()
				.possibleLostLocation("subway station")
				.lostTime("today")
				.features(List.of("black", "wallet", "logo", "card"))
				.build())
			.createdAt(LocalDateTime.now())
			.build());
		FoundItem foundItem = foundItemRepository.save(FoundItem.builder()
			.description("Found black wallet at subway station today with a logo card")
			.structuredData(StructuredItemData.builder()
				.foundLocation("subway station")
				.foundTime("today")
				.features(List.of("black", "wallet", "logo", "card"))
				.build())
			.createdAt(LocalDateTime.now())
			.build());

		Optional<MatchResult> firstMatch = matchingService.findAndSaveHighMatchForLostItem(lostItem);
		Optional<MatchResult> secondMatch = matchingService.findAndSaveHighMatchForFoundItem(foundItem);

		assertThat(firstMatch).isPresent();
		assertThat(secondMatch).isPresent();
		assertThat(secondMatch.get().getId()).isEqualTo(firstMatch.get().getId());
		assertThat(firstMatch.get().getId()).isEqualTo(lostItem.getId() + "_" + foundItem.getId());
		assertThat(matchRepository.findAll()).hasSize(1);
		assertThat(matchRepository.findByLostItemIdAndFoundItemId(lostItem.getId(), foundItem.getId()))
			.contains(firstMatch.get());
	}
}
