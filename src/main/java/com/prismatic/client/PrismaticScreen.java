package com.prismatic.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

/** The Right Shift menu. Mouse is read straight from GLFW so it doesn't depend on Screen input signatures. */
public class PrismaticScreen extends Screen {
	private enum Mode { LIST, SPOTIFY }

	private static final int BG = 0xF00B0B12, CARD = 0xFF12121B, CARD_HOVER = 0xFF181824, MUTED = 0xFF8A8A9C;

	private Mode mode = Mode.LIST;
	private int scroll;
	private boolean wasDown, dragging;
	private int mx, my;
	private boolean clicked;

	public PrismaticScreen() {
		super(Text.literal("Prismatic Client"));
	}

	public boolean shouldPause() {
		return false;
	}

	/** Not annotated on purpose: if the mouse wheel signature ever changes, the menu still compiles. */
	public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
		scroll -= (int) (verticalAmount * 24);
		return true;
	}

	@Override
	public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
		MinecraftClient mc = MinecraftClient.getInstance();
		TextRenderer tr = mc.textRenderer;
		long win = mc.getWindow().getHandle();
		boolean down = GLFW.glfwGetMouseButton(win, GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS;
		clicked = down && !wasDown;
		if (!down && dragging) {
			dragging = false;
			Config.save();
		}
		wasDown = down;
		mx = mouseX;
		my = mouseY;
		LyricsHud.demo = mode == Mode.SPOTIFY;

		ctx.fill(0, 0, width, height, 0x66000000);
		int pw = Math.min(width - 24, 560), ph = Math.min(height - 24, 330);
		int px = (width - pw) / 2, py = (height - ph) / 2;
		Draw.box(ctx, px - 1, py - 1, pw + 2, ph + 2, 0xFF24242F);
		Draw.box(ctx, px, py, pw, ph, BG);

		// header
		String title = "Prismatic Client";
		for (int i = 0; i < title.length(); i++) {
			int x = px + 12 + tr.getWidth(title.substring(0, i));
			ctx.drawText(tr, title.substring(i, i + 1), x, py + 11, Draw.gradient(i / (float) (title.length() - 1)), true);
		}
		String hint = "Right Shift to close";
		ctx.drawText(tr, hint, px + pw - 12 - tr.getWidth(hint), py + 11, MUTED, false);
		Draw.gradientStrip(ctx, px + 8, py + 26, pw - 16, 1);

		if (mode == Mode.LIST) list(ctx, tr, px, py, pw, ph);
		else spotify(ctx, tr, px, py, pw, ph);
	}

	// ---- module grid -------------------------------------------------
	private void list(DrawContext ctx, TextRenderer tr, int px, int py, int pw, int ph) {
		int gx = px + 10, gy = py + 34, gw = pw - 20, gh = ph - 44;
		int gap = 6, cols = 3, cw = (gw - gap * (cols - 1)) / cols, ch = 34;
		int rows = (Modules.ALL.size() + cols - 1) / cols;
		int content = rows * (ch + gap);
		int max = Math.max(0, content - gh);
		scroll = Math.max(0, Math.min(max, scroll));

		ctx.enableScissor(gx, gy, gx + gw, gy + gh);
		for (int i = 0; i < Modules.ALL.size(); i++) {
			Modules.Mod m = Modules.ALL.get(i);
			int x = gx + (i % cols) * (cw + gap);
			int y = gy + (i / cols) * (ch + gap) - scroll;
			if (y + ch < gy || y > gy + gh) continue;
			boolean hover = Draw.inside(mx, my, gx, gy, gw, gh) && Draw.inside(mx, my, x, y, cw, ch);
			Draw.box(ctx, x, y, cw, ch, hover && m.ready ? CARD_HOVER : CARD);
			int nameCol = m.ready ? 0xFFFFFFFF : 0xFF6E6E80;
			ctx.drawText(tr, m.name, x + 8, y + 7, nameCol, false);
			ctx.drawText(tr, tr.trimToWidth(m.desc, cw - 50), x + 8, y + 19, m.ready ? MUTED : 0xFF55556A, false);
			int tx = x + cw - 30, ty = y + (ch - 11) / 2;
			if (!m.ready) {
				ctx.drawText(tr, "SOON", x + cw - 8 - tr.getWidth("SOON"), y + 12, 0xFF55556A, false);
				continue;
			}
			boolean on = Config.isEnabled(m.id);
			toggle(ctx, tx, ty, on);
			if (m.id.equals("nowplaying")) {
				int ex = tx - 8 - tr.getWidth("Edit");
				boolean hEdit = Draw.inside(mx, my, ex - 2, y + 8, tr.getWidth("Edit") + 4, 18);
				ctx.drawText(tr, "Edit", ex, y + 13, hEdit ? 0xFFD946EF : 0xFF8B5CF6, false);
				if (clicked && hEdit && Draw.inside(mx, my, gx, gy, gw, gh)) {
					mode = Mode.SPOTIFY;
					clicked = false;
					continue;
				}
			}
			if (clicked && hover) {
				Config.setEnabled(m.id, !on);
				clicked = false;
			}
		}
		ctx.disableScissor();

		if (max > 0) {
			int barH = Math.max(18, gh * gh / content);
			int barY = gy + (gh - barH) * scroll / max;
			ctx.fill(gx + gw + 3, gy, gx + gw + 5, gy + gh, 0xFF1A1A24);
			ctx.fill(gx + gw + 3, barY, gx + gw + 5, barY + barH, 0xFF8B5CF6);
		}
	}

	private void toggle(DrawContext ctx, int x, int y, boolean on) {
		int w = 22, h = 11;
		if (on) {
			for (int i = 0; i < w; i++) ctx.fill(x + i, y + 1, x + i + 1, y + h - 1, Draw.gradient(i / (float) (w - 1)));
			ctx.fill(x + 1, y, x + w - 1, y + 1, Draw.gradient(0.5f));
			ctx.fill(x + 1, y + h - 1, x + w - 1, y + h, Draw.gradient(0.5f));
			ctx.fill(x + w - 9, y + 2, x + w - 2, y + h - 2, 0xFFFFFFFF);
		} else {
			Draw.box(ctx, x, y, w, h, 0xFF2C2C3A);
			ctx.fill(x + 2, y + 2, x + 9, y + h - 2, 0xFFB8B8C8);
		}
	}

	// ---- Now playing settings ---------------------------------------
	private static final String[] FONTS = {"smooth", "minecraft", "uniform"};
	private static final String[] FONT_NAMES = {"Smooth", "Minecraft", "Uniform"};
	private static final String[] HILITE = {"prismatic", "pink", "cyan", "violet", "white"};
	private static final String[] HILITE_NAMES = {"Prismatic", "Pink", "Cyan", "Violet", "White"};
	private static final String[] POS = {"top", "middle", "bottom"};
	private static final String[] POS_NAMES = {"Top", "Middle", "Bottom"};

	private static int idx(String[] arr, String v) {
		for (int i = 0; i < arr.length; i++) if (arr[i].equals(v)) return i;
		return 0;
	}

	private boolean button(DrawContext ctx, TextRenderer tr, int x, int y, int w, int h, String label) {
		boolean hover = Draw.inside(mx, my, x, y, w, h);
		Draw.box(ctx, x, y, w, h, hover ? 0xFF24243A : 0xFF1A1A28);
		ctx.drawText(tr, label, x + (w - tr.getWidth(label)) / 2, y + (h - 8) / 2 + 1, 0xFFFFFFFF, false);
		if (clicked && hover) {
			clicked = false;
			return true;
		}
		return false;
	}

	private void spotify(DrawContext ctx, TextRenderer tr, int px, int py, int pw, int ph) {
		Config.Spotify c = Config.spotify();
		int x = px + 16, y = py + 36, rh = 24, ctl = px + pw - 16 - 150;

		if (button(ctx, tr, px + pw - 70, py + 7, 58, 16, "< Back")) {
			mode = Mode.LIST;
			Config.save();
			return;
		}
		ctx.drawText(tr, "Now playing", x, y, 0xFFFFFFFF, true);
		if (!SpotifyService.supported()) {
			ctx.drawText(tr, "Spotify detection works on Windows only", x + 80, y, 0xFFF472B6, false);
		}
		y += rh;

		// font size slider
		ctx.drawText(tr, "Font size", x, y + 4, MUTED, false);
		int sx = ctl, sw = 110, sy = y + 8;
		ctx.fill(sx, sy, sx + sw, sy + 3, 0xFF2C2C3A);
		float t = (c.scale - 0.5f) / 2.5f;
		Draw.gradientStrip(ctx, sx, sy, Math.max(1, (int) (sw * t)), 3);
		int kx = sx + (int) (sw * t);
		ctx.fill(kx - 2, sy - 3, kx + 3, sy + 6, 0xFFFFFFFF);
		ctx.drawText(tr, Math.round(c.scale * 100) + "%", sx + sw + 8, y + 4, 0xFFFFFFFF, false);
		if (clicked && Draw.inside(mx, my, sx - 4, sy - 6, sw + 8, 16)) dragging = true;
		if (dragging) {
			float nt = Math.max(0f, Math.min(1f, (mx - sx) / (float) sw));
			c.scale = Math.round((0.5f + nt * 2.5f) * 20f) / 20f;
		}
		y += rh;

		if (button(ctx, tr, ctl, y, 150, 18, "Font: " + FONT_NAMES[idx(FONTS, c.font)])) {
			c.font = FONTS[(idx(FONTS, c.font) + 1) % FONTS.length];
			Config.save();
		}
		ctx.drawText(tr, "Font style", x, y + 5, MUTED, false);
		y += rh;
		if (button(ctx, tr, ctl, y, 150, 18, "Highlight: " + HILITE_NAMES[idx(HILITE, c.highlight)])) {
			c.highlight = HILITE[(idx(HILITE, c.highlight) + 1) % HILITE.length];
			Config.save();
		}
		ctx.drawText(tr, "Highlight color", x, y + 5, MUTED, false);
		y += rh;
		if (button(ctx, tr, ctl, y, 150, 18, "Position: " + POS_NAMES[idx(POS, c.position)])) {
			c.position = POS[(idx(POS, c.position) + 1) % POS.length];
			Config.save();
		}
		ctx.drawText(tr, "Screen position", x, y + 5, MUTED, false);
		y += rh;
		if (button(ctx, tr, ctl, y, 150, 18, "Bold: " + (c.bold ? "On" : "Off"))) {
			c.bold = !c.bold;
			Config.save();
		}
		ctx.drawText(tr, "Bold text", x, y + 5, MUTED, false);
		y += rh;
		if (button(ctx, tr, ctl, y, 150, 18, "Shadow: " + (c.shadow ? "On" : "Off"))) {
			c.shadow = !c.shadow;
			Config.save();
		}
		ctx.drawText(tr, "Text shadow", x, y + 5, MUTED, false);
		y += rh;
		if (button(ctx, tr, ctl, y, 150, 18, "Next line: " + (c.nextLine ? "On" : "Off"))) {
			c.nextLine = !c.nextLine;
			Config.save();
		}
		ctx.drawText(tr, "Show next line", x, y + 5, MUTED, false);
		y += rh;
		ctx.drawText(tr, "Lyrics delay", x, y + 5, MUTED, false);
		if (button(ctx, tr, ctl, y, 30, 18, "-100")) {
			c.delayMs = Math.max(-5000, c.delayMs - 100);
			Config.save();
		}
		ctx.drawText(tr, c.delayMs + " ms", ctl + 40, y + 5, 0xFFFFFFFF, false);
		if (button(ctx, tr, ctl + 120, y, 30, 18, "+100")) {
			c.delayMs = Math.min(5000, c.delayMs + 100);
			Config.save();
		}
	}
}
