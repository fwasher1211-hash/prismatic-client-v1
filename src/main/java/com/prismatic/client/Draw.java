package com.prismatic.client;

import net.minecraft.client.gui.DrawContext;

/** Small drawing helpers (ARGB colors). */
public final class Draw {
	public static final int[] STOPS = {0xFF22D3EE, 0xFF3B82F6, 0xFF8B5CF6, 0xFFD946EF, 0xFFF472B6};

	public static int gradient(float t) {
		t = Math.max(0f, Math.min(1f, t));
		float seg = t * (STOPS.length - 1);
		int i = Math.min((int) seg, STOPS.length - 2);
		float f = seg - i;
		int a = STOPS[i], b = STOPS[i + 1];
		int r = (int) (((a >> 16) & 255) + (((b >> 16) & 255) - ((a >> 16) & 255)) * f);
		int g = (int) (((a >> 8) & 255) + (((b >> 8) & 255) - ((a >> 8) & 255)) * f);
		int bl = (int) ((a & 255) + ((b & 255) - (a & 255)) * f);
		return 0xFF000000 | (r << 16) | (g << 8) | bl;
	}

	/** Rounded-looking box (corners trimmed by 1px). */
	public static void box(DrawContext c, int x, int y, int w, int h, int argb) {
		c.fill(x + 1, y, x + w - 1, y + h, argb);
		c.fill(x, y + 1, x + 1, y + h - 1, argb);
		c.fill(x + w - 1, y + 1, x + w, y + h - 1, argb);
	}

	/** Horizontal gradient strip. */
	public static void gradientStrip(DrawContext c, int x, int y, int w, int h) {
		for (int i = 0; i < w; i++) {
			c.fill(x + i, y, x + i + 1, y + h, gradient(w <= 1 ? 0 : i / (float) (w - 1)));
		}
	}

	public static boolean inside(int mx, int my, int x, int y, int w, int h) {
		return mx >= x && mx < x + w && my >= y && my < y + h;
	}

	private Draw() {
	}
}
