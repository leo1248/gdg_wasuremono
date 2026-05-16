package com.wasuremwono.model;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailPreview {
	private String emailStatus;
	private String recipient;
	private String subject;
	private String body;
	@Builder.Default
	private List<String> includedInfo = new ArrayList<>();
}
