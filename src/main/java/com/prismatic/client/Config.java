package com.prismatic.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/** Saved to .minecraft/config/prismatic_client.json */
public final class Config {
	public static class Spotify {
		public float scale = 1.0f;          // font size multiplier
		public String font = "smooth";      // smooth | minecraft | uniform
		public String highlight = "prismatic"; // prismatic | pink | cyan | violet | white
		public String position = "top";     // top | middle | bottom
		public boolean shadow = true;
		public boolean bold = true;
		public boolean nextLine = true;
		public int delayMs = 0;             // shift lyrics earlier/later
	}

	private static class Data {
		Map<String, Boolean> enabled = new HashMap<>();
		Spotify spotify = new Spotify();
	}

	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private static Data data = new Data();

	private static Path file() {
		return FabricLoader.getInstance().getConfigDir().resolve("prismatic_client.json");
	}

	public static void load() {
		try {
			Path f = file();
			if (Files.exists(f)) {
				Data d = GSON.fromJson(Files.readString(f), Data.class);
				if (d != null) {
					if (d.enabled == null) d.enabled = new HashMap<>();
					if (d.spotify == null) d.spotify = new Spotify();
					data = d;
				}
			} else {
				data.enabled.put("fps", true);
				save();
			}
		} catch (Exception e) {
			data = new Data();
		}
	}

	public static void save() {
		try {
			Files.writeString(file(), GSON.toJson(data));
		} catch (Exception ignored) {
		}
	}

	public static boolean isEnabled(String id) {
		return data.enabled.getOrDefault(id, false);
	}

	public static void setEnabled(String id, boolean on) {
		data.enabled.put(id, on);
		save();
	}

	public static Spotify spotify() {
		return data.spotify;
	}
}
