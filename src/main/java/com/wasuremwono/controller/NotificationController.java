package com.wasuremwono.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wasuremwono.dto.MatchEmailRequest;
import com.wasuremwono.model.EmailPreview;
import com.wasuremwono.service.NotificationService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {
	private final NotificationService notificationService;

	public NotificationController(NotificationService notificationService) {
		this.notificationService = notificationService;
	}

	@PostMapping("/match-email")
	public EmailPreview generateMatchEmailPreview(@Valid @RequestBody MatchEmailRequest request) {
		return notificationService.generateMatchEmailPreview(request.getMatchId());
	}
}
