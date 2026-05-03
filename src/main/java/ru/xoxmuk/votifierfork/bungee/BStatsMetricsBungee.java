package ru.xoxmuk.votifierfork.bungee;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPOutputStream;

import javax.net.ssl.HttpsURLConnection;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

public class BStatsMetricsBungee {

	public static class AdvancedBarChart extends CustomChart {

		private final Callable<Map<String, int[]>> callable;

		public AdvancedBarChart(String chartId, Callable<Map<String, int[]>> callable) {
			super(chartId);
			this.callable = callable;
		}

		@Override
		protected JsonObject getChartData() throws Exception {
			JsonObject data = new JsonObject();
			JsonObject values = new JsonObject();
			Map<String, int[]> map = callable.call();
			if (map == null || map.isEmpty()) {

				return null;
			}
			boolean allSkipped = true;
			for (Map.Entry<String, int[]> entry : map.entrySet()) {
				if (entry.getValue().length == 0) {
					continue;
				}
				allSkipped = false;
				JsonArray categoryValues = new JsonArray();
				for (int categoryValue : entry.getValue()) {
					categoryValues.add(new JsonPrimitive(categoryValue));
				}
				values.add(entry.getKey(), categoryValues);
			}
			if (allSkipped) {

				return null;
			}
			data.add("values", values);
			return data;
		}

	}

	public static class AdvancedPie extends CustomChart {

		private final Callable<Map<String, Integer>> callable;

		public AdvancedPie(String chartId, Callable<Map<String, Integer>> callable) {
			super(chartId);
			this.callable = callable;
		}

		@Override
		protected JsonObject getChartData() throws Exception {
			JsonObject data = new JsonObject();
			JsonObject values = new JsonObject();
			Map<String, Integer> map = callable.call();
			if (map == null || map.isEmpty()) {

				return null;
			}
			boolean allSkipped = true;
			for (Map.Entry<String, Integer> entry : map.entrySet()) {
				if (entry.getValue() == 0) {
					continue;
				}
				allSkipped = false;
				values.addProperty(entry.getKey(), entry.getValue());
			}
			if (allSkipped) {

				return null;
			}
			data.add("values", values);
			return data;
		}
	}

	public static abstract class CustomChart {

		private final String chartId;

		CustomChart(String chartId) {
			if (chartId == null || chartId.isEmpty()) {
				throw new IllegalArgumentException("ChartId cannot be null or empty!");
			}
			this.chartId = chartId;
		}

		protected abstract JsonObject getChartData() throws Exception;

		private JsonObject getRequestJsonObject(Logger logger, boolean logFailedRequests) {
			JsonObject chart = new JsonObject();
			chart.addProperty("chartId", chartId);
			try {
				JsonObject data = getChartData();
				if (data == null) {

					return null;
				}
				chart.add("data", data);
			} catch (Throwable t) {
				if (logFailedRequests) {
					logger.log(Level.WARNING, "Failed to get data for custom chart with id " + chartId, t);
				}
				return null;
			}
			return chart;
		}

	}

	public static class DrilldownPie extends CustomChart {

		private final Callable<Map<String, Map<String, Integer>>> callable;

		public DrilldownPie(String chartId, Callable<Map<String, Map<String, Integer>>> callable) {
			super(chartId);
			this.callable = callable;
		}

		@Override
		public JsonObject getChartData() throws Exception {
			JsonObject data = new JsonObject();
			JsonObject values = new JsonObject();
			Map<String, Map<String, Integer>> map = callable.call();
			if (map == null || map.isEmpty()) {

				return null;
			}
			boolean reallyAllSkipped = true;
			for (Map.Entry<String, Map<String, Integer>> entryValues : map.entrySet()) {
				JsonObject value = new JsonObject();
				boolean allSkipped = true;
				for (Map.Entry<String, Integer> valueEntry : map.get(entryValues.getKey()).entrySet()) {
					value.addProperty(valueEntry.getKey(), valueEntry.getValue());
					allSkipped = false;
				}
				if (!allSkipped) {
					reallyAllSkipped = false;
					values.add(entryValues.getKey(), value);
				}
			}
			if (reallyAllSkipped) {

				return null;
			}
			data.add("values", values);
			return data;
		}
	}

	public static class MultiLineChart extends CustomChart {

		private final Callable<Map<String, Integer>> callable;

		public MultiLineChart(String chartId, Callable<Map<String, Integer>> callable) {
			super(chartId);
			this.callable = callable;
		}

		@Override
		protected JsonObject getChartData() throws Exception {
			JsonObject data = new JsonObject();
			JsonObject values = new JsonObject();
			Map<String, Integer> map = callable.call();
			if (map == null || map.isEmpty()) {

				return null;
			}
			boolean allSkipped = true;
			for (Map.Entry<String, Integer> entry : map.entrySet()) {
				if (entry.getValue() == 0) {
					continue;
				}
				allSkipped = false;
				values.addProperty(entry.getKey(), entry.getValue());
			}
			if (allSkipped) {

				return null;
			}
			data.add("values", values);
			return data;
		}

	}

	public static class SimpleBarChart extends CustomChart {

		private final Callable<Map<String, Integer>> callable;

		public SimpleBarChart(String chartId, Callable<Map<String, Integer>> callable) {
			super(chartId);
			this.callable = callable;
		}

		@Override
		protected JsonObject getChartData() throws Exception {
			JsonObject data = new JsonObject();
			JsonObject values = new JsonObject();
			Map<String, Integer> map = callable.call();
			if (map == null || map.isEmpty()) {

				return null;
			}
			for (Map.Entry<String, Integer> entry : map.entrySet()) {
				JsonArray categoryValues = new JsonArray();
				categoryValues.add(new JsonPrimitive(entry.getValue()));
				values.add(entry.getKey(), categoryValues);
			}
			data.add("values", values);
			return data;
		}

	}

	public static class SimplePie extends CustomChart {

		private final Callable<String> callable;

		public SimplePie(String chartId, Callable<String> callable) {
			super(chartId);
			this.callable = callable;
		}

		@Override
		protected JsonObject getChartData() throws Exception {
			JsonObject data = new JsonObject();
			String value = callable.call();
			if (value == null || value.isEmpty()) {

				return null;
			}
			data.addProperty("value", value);
			return data;
		}
	}

	public static class SingleLineChart extends CustomChart {

		private final Callable<Integer> callable;

		public SingleLineChart(String chartId, Callable<Integer> callable) {
			super(chartId);
			this.callable = callable;
		}

		@Override
		protected JsonObject getChartData() throws Exception {
			JsonObject data = new JsonObject();
			int value = callable.call();
			if (value == 0) {

				return null;
			}
			data.addProperty("value", value);
			return data;
		}

	}

	public static final int B_STATS_VERSION = 1;

	private static final List<Object> knownMetricsInstances = new ArrayList<>();

	private static boolean logResponseStatusText;

	private static boolean logSentData;

	private static final String URL = "https://bStats.org/submitData/bungeecord";

	static {

		if (System.getProperty("bstats.relocatecheck") == null
				|| !System.getProperty("bstats.relocatecheck").equals("false")) {

			final String defaultPackage = new String(new byte[] { 'o', 'r', 'g', '.', 'b', 's', 't', 'a', 't', 's', '.',
					'b', 'u', 'n', 'g', 'e', 'e', 'c', 'o', 'r', 'd' });
			final String examplePackage = new String(
					new byte[] { 'y', 'o', 'u', 'r', '.', 'p', 'a', 'c', 'k', 'a', 'g', 'e' });

			if (BStatsMetricsBungee.class.getPackage().getName().equals(defaultPackage)
					|| BStatsMetricsBungee.class.getPackage().getName().equals(examplePackage)) {
				throw new IllegalStateException("bStats Metrics class has not been relocated correctly!");
			}
		}
	}

	private static byte[] compress(final String str) throws IOException {
		if (str == null) {
			return null;
		}
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		try (GZIPOutputStream gzip = new GZIPOutputStream(outputStream)) {
			gzip.write(str.getBytes(StandardCharsets.UTF_8));
		}
		return outputStream.toByteArray();
	}

	public static void linkMetrics(Object metrics) {
		knownMetricsInstances.add(metrics);
	}

	private static void sendData(Plugin plugin, JsonObject data) throws Exception {
		if (data == null) {
			throw new IllegalArgumentException("Data cannot be null");
		}
		if (logSentData) {
			plugin.getLogger().info("Sending data to bStats: " + data);
		}

		HttpsURLConnection connection = (HttpsURLConnection) new URL(URL).openConnection();

		byte[] compressedData = compress(data.toString());

		connection.setRequestMethod("POST");
		connection.addRequestProperty("Accept", "application/json");
		connection.addRequestProperty("Connection", "close");
		connection.addRequestProperty("Content-Encoding", "gzip");
		connection.addRequestProperty("Content-Length", String.valueOf(compressedData.length));
		connection.setRequestProperty("Content-Type", "application/json");
		connection.setRequestProperty("User-Agent", "MC-Server/" + B_STATS_VERSION);

		connection.setDoOutput(true);
		try (DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream())) {
			outputStream.write(compressedData);
		}

		StringBuilder builder = new StringBuilder();

		try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				builder.append(line);
			}
		}

		if (logResponseStatusText) {
			plugin.getLogger().info("Sent data to bStats and received response: " + builder);
		}
	}

	private final List<CustomChart> charts = new ArrayList<>();

	private boolean enabled;

	private boolean logFailedRequests = false;

	private final Plugin plugin;

	private final int pluginId;

	private String serverUUID;

	public BStatsMetricsBungee(Plugin plugin, int pluginId) {
		this.plugin = plugin;
		this.pluginId = pluginId;

		try {
			loadConfig();
		} catch (IOException e) {

			plugin.getLogger().log(Level.WARNING, "Failed to load bStats config!", e);
			return;
		}

		if (!enabled) {
			return;
		}

		Class<?> usedMetricsClass = getFirstBStatsClass();
		if (usedMetricsClass == null) {

			return;
		}
		if (usedMetricsClass == getClass()) {

			linkMetrics(this);
			startSubmitting();
		} else {

			try {
				usedMetricsClass.getMethod("linkMetrics", Object.class).invoke(null, this);
			} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
				if (logFailedRequests) {
					plugin.getLogger().log(Level.WARNING,
							"Failed to link to first metrics class " + usedMetricsClass.getName() + "!", e);
				}
			}
		}
	}

	public void addCustomChart(CustomChart chart) {
		if (chart == null) {
			plugin.getLogger().log(Level.WARNING, "Chart cannot be null");
		}
		charts.add(chart);
	}

	private Class<?> getFirstBStatsClass() {
		File bStatsFolder = new File(plugin.getDataFolder().getParentFile(), "bStats");
		bStatsFolder.mkdirs();
		File tempFile = new File(bStatsFolder, "temp.txt");

		try {
			String className = readFile(tempFile);
			if (className != null) {
				try {

					return Class.forName(className);
				} catch (ClassNotFoundException ignored) {
				}
			}
			writeFile(tempFile, getClass().getName());
			return getClass();
		} catch (IOException e) {
			if (logFailedRequests) {
				plugin.getLogger().log(Level.WARNING, "Failed to get first bStats class!", e);
			}
			return null;
		}
	}

	public JsonObject getPluginData() {
		JsonObject data = new JsonObject();

		String pluginName = plugin.getDescription().getName();
		String pluginVersion = plugin.getDescription().getVersion();

		data.addProperty("pluginName", pluginName);
		data.addProperty("id", pluginId);
		data.addProperty("pluginVersion", pluginVersion);

		JsonArray customCharts = new JsonArray();
		for (CustomChart customChart : charts) {

			JsonObject chart = customChart.getRequestJsonObject(plugin.getLogger(), logFailedRequests);
			if (chart == null) {
				continue;
			}
			customCharts.add(chart);
		}
		data.add("customCharts", customCharts);

		return data;
	}

	@SuppressWarnings("deprecation")
	private JsonObject getServerData() {

		int playerAmount = Math.min(plugin.getProxy().getOnlineCount(), 500);
		int onlineMode = plugin.getProxy().getConfig().isOnlineMode() ? 1 : 0;
		String bungeecordVersion = plugin.getProxy().getVersion();
		int managedServers = plugin.getProxy().getServers().size();

		String javaVersion = System.getProperty("java.version");
		String osName = System.getProperty("os.name");
		String osArch = System.getProperty("os.arch");
		String osVersion = System.getProperty("os.version");
		int coreCount = Runtime.getRuntime().availableProcessors();

		JsonObject data = new JsonObject();

		data.addProperty("serverUUID", serverUUID);

		data.addProperty("playerAmount", playerAmount);
		data.addProperty("managedServers", managedServers);
		data.addProperty("onlineMode", onlineMode);
		data.addProperty("bungeecordVersion", bungeecordVersion);

		data.addProperty("javaVersion", javaVersion);
		data.addProperty("osName", osName);
		data.addProperty("osArch", osArch);
		data.addProperty("osVersion", osVersion);
		data.addProperty("coreCount", coreCount);

		return data;
	}

	public boolean isEnabled() {
		return enabled;
	}

	private void loadConfig() throws IOException {
		File bStatsFolder = new File(plugin.getDataFolder().getParentFile(), "bStats");
		bStatsFolder.mkdirs();
		File configFile = new File(bStatsFolder, "config.yml");
		if (!configFile.exists()) {
			writeFile(configFile,
					"#bStats collects some data for plugin authors like how many servers are using their plugins.",
					"#To honor their work, you should not disable it.",
					"#This has nearly no effect on the server performance!",
					"#Check out https://bStats.org/ to learn more :)", "enabled: true",
					"serverUuid: \"" + UUID.randomUUID() + "\"", "logFailedRequests: false", "logSentData: false",
					"logResponseStatusText: false");
		}

		Configuration configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(configFile);

		enabled = configuration.getBoolean("enabled", true);
		serverUUID = configuration.getString("serverUuid");
		logFailedRequests = configuration.getBoolean("logFailedRequests", false);
		logSentData = configuration.getBoolean("logSentData", false);
		logResponseStatusText = configuration.getBoolean("logResponseStatusText", false);
	}

	private String readFile(File file) throws IOException {
		if (!file.exists()) {
			return null;
		}
		try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
			return bufferedReader.readLine();
		}
	}

	private void startSubmitting() {

		plugin.getProxy().getScheduler().schedule(plugin, this::submitData, 2, 30, TimeUnit.MINUTES);

	}

	private void submitData() {
		final JsonObject data = getServerData();

		final JsonArray pluginData = new JsonArray();

		for (Object metrics : knownMetricsInstances) {
			try {
				Object plugin = metrics.getClass().getMethod("getPluginData").invoke(metrics);
				if (plugin instanceof JsonObject) {
					pluginData.add((JsonObject) plugin);
				}
			} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException ignored) {
			}
		}

		data.add("plugins", pluginData);

		try {

			sendData(plugin, data);
		} catch (Exception e) {

			if (logFailedRequests) {
				plugin.getLogger().log(Level.WARNING, "Could not submit plugin stats!", e);
			}
		}
	}

	private void writeFile(File file, String... lines) throws IOException {
		try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file))) {
			for (String line : lines) {
				bufferedWriter.write(line);
				bufferedWriter.newLine();
			}
		}
	}

}
