package com.wasuremwono.integration.ai;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class AiBridgeClient {
	private static final Duration PROCESS_TIMEOUT = Duration.ofSeconds(120);

	private final AiBridgeProperties properties;
	private final ObjectMapper objectMapper;

	public AiBridgeClient(AiBridgeProperties properties, ObjectMapper objectMapper) {
		this.properties = properties;
		this.objectMapper = objectMapper;
	}

	public boolean isEnabled() {
		return properties.isEnabled();
	}

	public Optional<AiTextMatchResult> matchText(String description) {
		if (!properties.isEnabled() || description == null || description.isBlank()) {
			return Optional.empty();
		}

		try {
			String output = runPython(List.of(
				"scripts/match_text.py",
				description,
				"--prefix",
				properties.getAnalysesPrefix(),
				"--json"
			));
			return Optional.of(objectMapper.readValue(output, AiTextMatchResult.class));
		} catch (InterruptedException ex) {
			Thread.currentThread().interrupt();
			return Optional.empty();
		} catch (IOException ex) {
			return Optional.empty();
		}
	}

	public Optional<AiImageProcessResult> processImage(String imagePath) {
		if (!properties.isEnabled() || imagePath == null || imagePath.isBlank()) {
			return Optional.empty();
		}

		try {
			String output = runPython(List.of(
				"scripts/process_image.py",
				imagePath,
				"--prefix",
				properties.getUploadPrefix()
			));
			List<AiImageProcessResult> results = objectMapper.readValue(
				output,
				new TypeReference<List<AiImageProcessResult>>() {
				}
			);
			return results.stream().findFirst();
		} catch (InterruptedException ex) {
			Thread.currentThread().interrupt();
			return Optional.empty();
		} catch (IOException ex) {
			return Optional.empty();
		}
	}

	private String runPython(List<String> arguments) throws IOException, InterruptedException {
		List<String> command = new ArrayList<>();
		command.add(properties.getPythonExecutable());
		command.addAll(arguments);

		Process process = new ProcessBuilder(command)
			.directory(new File(properties.getProjectDir()))
			.redirectErrorStream(true)
			.start();

		boolean completed = process.waitFor(PROCESS_TIMEOUT.toMillis(), java.util.concurrent.TimeUnit.MILLISECONDS);
		String output = new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8).trim();

		if (!completed) {
			process.destroyForcibly();
			throw new IOException("AI bridge process timed out");
		}
		if (process.exitValue() != 0) {
			throw new IOException("AI bridge process failed: " + output);
		}
		return output;
	}
}
