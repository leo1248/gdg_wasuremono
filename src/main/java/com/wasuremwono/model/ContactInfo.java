package com.wasuremwono.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContactInfo {
	private String contactId;
	private String email;
	private String phoneMasked;
	private boolean allowPhoneContact;
	private boolean allowEmailNotification;
}
