package com.wasuremwono.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wasuremwono.dto.LostItemRequest;
import com.wasuremwono.dto.LostItemResponse;
import com.wasuremwono.service.LostItemService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/lost-items")
public class LostItemController {
	private final LostItemService lostItemService;

	public LostItemController(LostItemService lostItemService) {
		this.lostItemService = lostItemService;
	}

	@PostMapping
	public LostItemResponse createLostItem(@Valid @RequestBody LostItemRequest request) {
		return lostItemService.createLostItem(request);
	}
}
