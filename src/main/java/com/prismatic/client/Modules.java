package com.prismatic.client;

import java.util.ArrayList;
import java.util.List;

/** Every module shown in the Prismatic menu. "ready" ones really work; the rest show SOON. */
public final class Modules {
	public static final class Mod {
		public final String id, name, desc;
		public final boolean ready;

		Mod(String id, String name, String desc, boolean ready) {
			this.id = id;
			this.name = name;
			this.desc = desc;
			this.ready = ready;
		}
	}

	public static final List<Mod> ALL = new ArrayList<>();

	private static void add(String id, String name, String desc, boolean ready) {
		ALL.add(new Mod(id, name, desc, ready));
	}

	static {
		add("fps", "FPS counter", "Keep your frame rate in view", true);
		add("coords", "Coordinates", "Your position in the world", true);
		add("ping", "Ping", "Your connection latency", true);
		add("clock", "Clock", "Local time, visible while playing", true);
		add("direction", "Direction", "Which way you are facing", true);
		add("speed", "Speed", "Your horizontal movement speed", true);
		add("memory", "Memory", "Watch Java heap usage", true);
		add("keystrokes", "Keystrokes", "Show movement keys and clicks", true);
		add("cps", "Clicks per second", "Track left and right clicks", true);
		add("combo", "Combo counter", "Consecutive melee hits", true);
		add("waypoints", "Waypoints", "Named world markers", false);
		add("armor", "Armor status", "Equipment icons and durability", true);
		add("potions", "Potion status", "Active effects and their timers", true);
		add("potcounter", "Pot counter", "Count usable splash potions", true);
		add("serverinfo", "Server info", "Server address you are on", true);
		add("biome", "Biome", "The biome at your position", true);
		add("worldclock", "World clock", "World day count and time", true);
		add("cooldowns", "Cooldowns", "Remaining time for actions", false);
		add("foodpreview", "Food preview", "Hunger and saturation preview", false);
		add("reach", "Reach display", "Distance to your last hit", true);
		add("itemcounter", "Item counter", "Track up to ten chosen items", false);
		add("playerprofile", "Player profile", "Look up players", false);
		add("teamview", "Team view", "Mark your teammates", false);
		add("tntcountdown", "TNT countdown", "Time left on lit TNT", false);

		add("saturation", "Color saturation", "Adjust world color intensity", false);
		add("hittint", "Hit tint", "Choose the color of the hit flash", false);
		add("blockoutline", "Block outline", "Change the color and width", false);
		add("itemphysics", "Item physics", "Dropped items tumble", false);
		add("chunkborders", "Chunk borders", "Toggle chunk borders", false);
		add("fog", "Fog customizer", "Adjust distance fog", false);
		add("freelook", "Free look", "Hold a key to look around", false);
		add("quickaction", "Quick action", "Send your saved chat", false);
		add("viewmodel", "Viewmodel", "Set each hand's item position", false);
		add("handsway", "Hand sway", "Reduce or remove hand sway", false);
		add("crosshair", "Crosshair", "Customize your crosshair", false);
		add("togglesneak", "Toggle sneak", "Press sneak once to stay sneaking", true);
		add("scoreboard", "Scoreboard", "Move, scale or hide it", false);
		add("bossbars", "Boss bars", "Move, scale or hide them", false);
		add("menublur", "Menu blur", "Set the strength of menu blur", false);
		add("titles", "Titles", "Move, scale or hide titles", false);
		add("soundfilters", "Sound filters", "Adjust portal and thunder sounds", false);
		add("timechanger", "Time changer", "Choose the time of day", false);
		add("gamemenu", "Prismatic game menu", "A polished pause menu", false);
		add("snaplook", "Snap look", "Snap your view to a preset", false);
		add("shinypots", "Shiny pots", "Add an enchantment glint", false);
		add("mctiers", "MCTiers", "Show each player's tier", false);
		add("clearwater", "Clear water", "See through water", false);
		add("hitboxes", "Hitboxes", "Show entity hitboxes", false);

		add("motionblur", "Motion blur", "Blend recent world frames", false);
		add("stretched", "Stretched resolution", "Use a competitive aspect ratio", false);
		add("betterf3", "Better F3", "Replace the debug view", false);
		add("titlescreen", "Prismatic title screen", "Replace Minecraft's menu", false);
		add("particles", "Particles", "Turn off any particle", false);
		add("nowplaying", "Now playing", "Synced Spotify lyrics HUD", true);
		add("swingspeed", "Swing speed", "Slow down or speed up swings", false);
		add("yourname", "Your name", "See your own nametag", false);
		add("smoothhotbar", "Smooth hotbar", "Slide the hotbar selection", false);
		add("smoothswap", "Smooth swap", "Items you move between slots", false);
		add("compacttab", "Compact tab", "Shrink the player list", false);
		add("blockanimation", "Block animation", "Blocks you place grow in", false);
		add("disableplants", "Disable plants", "Stop drawing grass and flowers", false);
		add("legacyvisuals", "Legacy visuals", "1.7 animations", false);
		add("shulkerpreview", "Shulker preview", "Hover a shulker box to peek", false);
		add("flightspeed", "Flight speed", "Fly faster in creative", false);
		add("bedrockflight", "Bedrock flight", "Creative and spectator flight", false);
		add("legitchams", "Legit chams", "Draw players in a match", false);
	}

	private Modules() {
	}
}
