package com.prismatic.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

public class PrismaticClient implements ClientModInitializer {
	private static boolean shiftPrev;
	private static boolean sneakSetByUs;

	@Override
	public void onInitializeClient() {
		Config.load();

		HudElementRegistry.attachElementBefore(VanillaHudElements.CHAT,
			Identifier.of("prismatic_client", "hud"), (context, tickCounter) -> Hud.render(context));

		ClientTickEvents.END_CLIENT_TICK.register(PrismaticClient::tick);

		AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
			if (world.isClient()) Hud.onAttack(player, entity);
			return ActionResult.PASS;
		});
	}

	private static void tick(MinecraftClient mc) {
		if (mc.getWindow() == null) return;
		long win = mc.getWindow().getHandle();

		boolean shift = GLFW.glfwGetKey(win, GLFW.GLFW_KEY_RIGHT_SHIFT) == GLFW.GLFW_PRESS;
		if (shift && !shiftPrev) {
			if (mc.currentScreen == null) mc.setScreen(new PrismaticScreen());
			else if (mc.currentScreen instanceof PrismaticScreen) {
				LyricsHud.demo = false;
				mc.setScreen(null);
			}
		}
		shiftPrev = shift;

		Hud.tick(mc);
		SpotifyService.setActive(Config.isEnabled("nowplaying"));

		if (Config.isEnabled("togglesneak")) {
			mc.options.getSneakToggled().setValue(true);
			sneakSetByUs = true;
		} else if (sneakSetByUs) {
			mc.options.getSneakToggled().setValue(false);
			sneakSetByUs = false;
		}
	}
}
