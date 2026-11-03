package com.prismatic.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.glfw.GLFW;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Locale;

/** Draws all working HUD modules (no mixins needed). */
public final class Hud {
	private static final Deque<Long> LEFT = new ArrayDeque<>(), RIGHT = new ArrayDeque<>();
	private static boolean lPrev, rPrev;
	private static int combo;
	private static long lastHit;
	private static double reach, speed, prevX, prevZ;
	private static boolean hasPrev;

	// ---- logic -------------------------------------------------------
	public static void tick(MinecraftClient mc) {
		PlayerEntity p = mc.player;
		if (p == null) {
			hasPrev = false;
			return;
		}
		if (hasPrev) {
			speed = Math.hypot(p.getX() - prevX, p.getZ() - prevZ) * 20.0;
		}
		prevX = p.getX();
		prevZ = p.getZ();
		hasPrev = true;
		if (p.hurtTime > 0 || System.currentTimeMillis() - lastHit > 3000) {
			combo = 0;
		}
	}

	public static void onAttack(PlayerEntity p, Entity target) {
		combo++;
		lastHit = System.currentTimeMillis();
		reach = p.distanceTo(target);
	}

	private static void pollMouse(MinecraftClient mc) {
		long w = mc.getWindow().getHandle();
		boolean l = GLFW.glfwGetMouseButton(w, GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS;
		boolean r = GLFW.glfwGetMouseButton(w, GLFW.GLFW_MOUSE_BUTTON_RIGHT) == GLFW.GLFW_PRESS;
		long now = System.currentTimeMillis();
		if (mc.currentScreen == null) {
			if (l && !lPrev) LEFT.addLast(now);
			if (r && !rPrev) RIGHT.addLast(now);
		}
		lPrev = l;
		rPrev = r;
		while (!LEFT.isEmpty() && now - LEFT.peekFirst() > 1000) LEFT.pollFirst();
		while (!RIGHT.isEmpty() && now - RIGHT.peekFirst() > 1000) RIGHT.pollFirst();
	}

	private static String pretty(String s) {
		StringBuilder b = new StringBuilder();
		for (String part : s.split("_")) {
			if (part.isEmpty()) continue;
			b.append(Character.toUpperCase(part.charAt(0))).append(part.substring(1)).append(' ');
		}
		return b.toString().trim();
	}

	private static String line(String id, MinecraftClient mc) {
		PlayerEntity p = mc.player;
		switch (id) {
			case "fps":
				return mc.getCurrentFps() + " FPS";
			case "coords":
				return String.format(Locale.ROOT, "XYZ %.0f / %.0f / %.0f", p.getX(), p.getY(), p.getZ());
			case "ping": {
				var net = mc.getNetworkHandler();
				var entry = net == null ? null : net.getPlayerListEntry(p.getUuid());
				return entry == null ? "Ping --" : "Ping " + entry.getLatency() + " ms";
			}
			case "clock":
				return LocalTime.now().format(DateTimeFormatter.ofPattern("hh:mm a"));
			case "direction": {
				float yaw = MathHelper.wrapDegrees(p.getYaw());
				float compass = (yaw + 180f + 360f) % 360f;
				String[] names = {"N", "NE", "E", "SE", "S", "SW", "W", "NW"};
				return names[(int) ((compass + 22.5f) / 45f) % 8] + " " + Math.round(compass) + "\u00B0";
			}
			case "speed":
				return String.format(Locale.ROOT, "%.1f b/s", speed);
			case "memory": {
				Runtime rt = Runtime.getRuntime();
				long used = (rt.totalMemory() - rt.freeMemory()) >> 20;
				return "Mem " + used + " / " + (rt.maxMemory() >> 20) + " MB";
			}
			case "cps":
				return "CPS " + LEFT.size() + " | " + RIGHT.size();
			case "combo":
				return "Combo " + combo;
			case "potcounter": {
				int n = 0;
				PlayerInventory inv = p.getInventory();
				for (int i = 0; i < inv.size(); i++) {
					ItemStack s = inv.getStack(i);
					if (s.isOf(Items.SPLASH_POTION)) n += s.getCount();
				}
				return "Pots " + n;
			}
			case "serverinfo": {
				var info = mc.getCurrentServerEntry();
				return info == null ? "Singleplayer" : info.address;
			}
			case "biome": {
				if (mc.world == null) return null;
				String name = mc.world.getBiome(p.getBlockPos()).getKey().map(k -> k.getValue().getPath()).orElse("unknown");
				return pretty(name);
			}
			case "worldclock": {
				if (mc.world == null) return null;
				long t = mc.world.getTime();
				long hour = (t / 1000 + 6) % 24;
				long min = (t % 1000) * 60 / 1000;
				return String.format(Locale.ROOT, "Day %d  %02d:%02d", t / 24000 + 1, hour, min);
			}
			case "reach":
				return String.format(Locale.ROOT, "Reach %.2f", reach);
			default:
				return null;
		}
	}

	// ---- rendering ---------------------------------------------------
	public static void render(DrawContext ctx) {
		MinecraftClient mc = MinecraftClient.getInstance();
		if (mc.player == null || mc.options.hudHidden) return;
		pollMouse(mc);
		TextRenderer tr = mc.textRenderer;

		List<String> lines = new ArrayList<>();
		for (Modules.Mod m : Modules.ALL) {
			if (!m.ready || !Config.isEnabled(m.id)) continue;
			String s = line(m.id, mc);
			if (s != null) lines.add(s);
		}
		int y = 4, i = 0;
		for (String s : lines) {
			int w = tr.getWidth(s) + 10;
			Draw.box(ctx, 4, y, w, 13, 0x99000000);
			ctx.fill(4, y + 1, 6, y + 12, Draw.gradient(i / 6f));
			ctx.drawText(tr, s, 9, y + 3, 0xFFFFFFFF, true);
			y += 15;
			i++;
		}

		int sw = ctx.getScaledWindowWidth(), sh = ctx.getScaledWindowHeight();
		if (Config.isEnabled("keystrokes")) drawKeys(ctx, mc, tr, 6, sh - 66);
		if (Config.isEnabled("armor")) drawArmor(ctx, mc, tr, sw - 70, sh - 96);
		if (Config.isEnabled("potions")) drawPotions(ctx, mc, tr, sw - 4, 4);
		if (Config.isEnabled("nowplaying")) LyricsHud.render(ctx, mc);
	}

	private static void key(DrawContext c, TextRenderer tr, int x, int y, int w, int h, String label, boolean down) {
		Draw.box(c, x, y, w, h, down ? 0xDDD946EF : 0x99000000);
		c.drawText(tr, label, x + (w - tr.getWidth(label)) / 2, y + (h - 8) / 2 + 1, 0xFFFFFFFF, false);
	}

	private static void drawKeys(DrawContext c, MinecraftClient mc, TextRenderer tr, int x, int y) {
		var o = mc.options;
		key(c, tr, x + 20, y, 18, 18, "W", o.forwardKey.isPressed());
		key(c, tr, x, y + 20, 18, 18, "A", o.leftKey.isPressed());
		key(c, tr, x + 20, y + 20, 18, 18, "S", o.backKey.isPressed());
		key(c, tr, x + 40, y + 20, 18, 18, "D", o.rightKey.isPressed());
		key(c, tr, x, y + 40, 28, 18, "LMB", lPrev);
		key(c, tr, x + 30, y + 40, 28, 18, "RMB", rPrev);
	}

	private static void drawArmor(DrawContext c, MinecraftClient mc, TextRenderer tr, int x, int y) {
		EquipmentSlot[] slots = {EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};
		for (int i = 0; i < slots.length; i++) {
			ItemStack s = mc.player.getEquippedStack(slots[i]);
			int yy = y + i * 20;
			if (s.isEmpty()) continue;
			Draw.box(c, x - 2, yy - 2, 66, 20, 0x99000000);
			c.drawItem(s, x, yy);
			if (s.isDamageable()) {
				int left = s.getMaxDamage() - s.getDamage();
				c.drawText(tr, left + "/" + s.getMaxDamage(), x + 20, yy + 4, 0xFFFFFFFF, true);
			}
		}
	}

	private static void drawPotions(DrawContext c, MinecraftClient mc, TextRenderer tr, int right, int y) {
		for (StatusEffectInstance e : mc.player.getStatusEffects()) {
			String name = e.getEffectType().value().getName().getString() + " " + (e.getAmplifier() + 1);
			int d = e.getDuration();
			String time = (d < 0 || d > 99999) ? "inf" : String.format(Locale.ROOT, "%d:%02d", d / 1200, (d / 20) % 60);
			String s = name + "  " + time;
			int w = tr.getWidth(s) + 10;
			Draw.box(c, right - w, y, w, 13, 0x99000000);
			c.drawText(tr, s, right - w + 5, y + 3, 0xFFFFFFFF, true);
			y += 15;
		}
	}

	private Hud() {
	}
}
