package com.wasuremwono.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import com.wasuremwono.model.StructuredItemData;

class MonoStructuringServiceTest {
	private final MonoStructuringService monoStructuringService = new MonoStructuringService();

	@Test
	void classifiesMixedPublicAndPrivateLocationCandidatesAsUncertain() {
		StructuredItemData data = monoStructuringService.structureLostItem(
			"Lost a black wallet at a cafe near the subway station today",
			BigDecimal.ZERO
		);

		assertThat(data.getLocationType()).isEqualTo("Uncertain: public place and private business candidates");
	}
}
