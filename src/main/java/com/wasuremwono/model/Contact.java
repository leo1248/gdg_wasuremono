package com.wasuremwono.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Contact {
	private String id;
	private String name;
	private String email;
	private String phoneNumber;
	private boolean allowPhoneContact;
	private boolean allowEmailNotification;
	private String createdAt;
}
