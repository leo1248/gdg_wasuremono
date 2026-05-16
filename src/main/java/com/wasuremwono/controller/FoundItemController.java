package com.wasuremwono.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wasuremwono.dto.FoundItemRequest;
import com.wasuremwono.dto.FoundItemResponse;
import com.wasuremwono.service.FoundItemService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/found-items")
public class FoundItemController {
	private final FoundItemService foundItemService;

	public FoundItemController(FoundItemService foundItemService) {
		this.foundItemService = foundItemService;
	}

	@PostMapping
	public FoundItemResponse createFoundItem(@Valid @RequestBody FoundItemRequest request) {
		return foundItemService.createFoundItem(request);
	}
}
