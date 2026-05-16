package com.wasuremwono.controller;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.wasuremwono.dto.ErrorResponse;

class GlobalExceptionHandlerTest {

	@Test
	void handlesUnexpectedExceptionAsInternalServerError() {
		GlobalExceptionHandler handler = new GlobalExceptionHandler();

		ResponseEntity<ErrorResponse> response = handler.handleUnexpected(new RuntimeException("database password leaked"));

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
		assertThat(response.getBody()).isNotNull();
		assertThat(response.getBody().getError()).isEqualTo("INTERNAL_SERVER_ERROR");
		assertThat(response.getBody().getMessage()).isEqualTo("An unexpected error occurred.");
	}
}
