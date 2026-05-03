package ru.xoxmuk.votifierfork;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class CheckUpdate {

	private static final String GITHUB_API_URL =
			"https://api.github.com/repos/xoxmuk/Votifier-Fork/releases/tags/latest";

	static VotifierFork plugin;

	public CheckUpdate(VotifierFork plugin) {
		CheckUpdate.plugin = plugin;
	}

	public void checkUpdate() {
		if (plugin.configFile.isDisableUpdateChecking()) {
			return;
		}

		try {
			HttpURLConnection connection = (HttpURLConnection) new URL(GITHUB_API_URL).openConnection();
			connection.setRequestMethod("GET");
			connection.setRequestProperty("Accept", "application/vnd.github+json");
			connection.setRequestProperty("User-Agent", "VotifierFork-UpdateChecker");
			connection.setConnectTimeout(5000);
			connection.setReadTimeout(5000);

			int status = connection.getResponseCode();
			if (status != 200) {
				plugin.getLogger().info("Failed to check for update for " + plugin.getName() + "! (HTTP " + status + ")");
				return;
			}

			StringBuilder response = new StringBuilder();
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
				String line;
				while ((line = reader.readLine()) != null) {
					response.append(line);
				}
			}

			String latestVersion = extractField(response.toString(), "name");

			if (latestVersion == null || latestVersion.isEmpty()) {
				plugin.getLogger().info("Failed to parse latest version from GitHub for " + plugin.getName() + "!");
				return;
			}

			String currentVersion = plugin.getDescription().getVersion();

			if (normalize(currentVersion).equals(normalize(latestVersion))) {
				plugin.getLogger().info(plugin.getName() + " is up to date! Version: " + currentVersion);
			} else {
				plugin.getLogger().info(plugin.getName() + " has an update available! Your Version: "
						+ currentVersion + " New Version: " + latestVersion
						+ " - https://github.com/xoxmuk/Votifier-Fork/releases/tag/latest");
			}

		} catch (Exception e) {
			plugin.getLogger().info("Failed to check for update for " + plugin.getName() + "! (" + e.getMessage() + ")");
		}
	}

	private String extractField(String json, String field) {
		String key = "\"" + field + "\"";
		int idx = json.indexOf(key);
		if (idx == -1) return null;
		int start = json.indexOf('"', idx + key.length() + 1);
		if (start == -1) return null;
		int end = json.indexOf('"', start + 1);
		if (end == -1) return null;
		return json.substring(start + 1, end);
	}

	private String normalize(String version) {
		return version.trim().replaceAll("^[vV]", "");
	}

}
