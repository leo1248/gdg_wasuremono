package com.wasuremwono.integration.ai;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "wasuremwono.ai")
public class AiBridgeProperties {
	private boolean enabled;
	private String pythonExecutable = "ai/.venv/Scripts/python.exe";
	private String projectDir = "ai";
	private String analysesPrefix = "photo-features/analyses/";
	private String uploadPrefix = "photo-features";

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public String getPythonExecutable() {
		return pythonExecutable;
	}

	public void setPythonExecutable(String pythonExecutable) {
		this.pythonExecutable = pythonExecutable;
	}

	public String getProjectDir() {
		return projectDir;
	}

	public void setProjectDir(String projectDir) {
		this.projectDir = projectDir;
	}

	public String getAnalysesPrefix() {
		return analysesPrefix;
	}

	public void setAnalysesPrefix(String analysesPrefix) {
		this.analysesPrefix = analysesPrefix;
	}

	public String getUploadPrefix() {
		return uploadPrefix;
	}

	public void setUploadPrefix(String uploadPrefix) {
		this.uploadPrefix = uploadPrefix;
	}
}
