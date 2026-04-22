package net.srodix.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.srodix.client.ui.GuiMain;

import static com.mojang.blaze3d.platform.InputConstants.KEY_RSHIFT;
import static com.mojang.blaze3d.platform.InputConstants.Type.KEYSYM;

public class SkyblockFarmingUtilsClient implements ClientModInitializer {
	private static final KeyMapping OPEN_MENU_KEY = KeyBindingHelper.registerKeyBinding(new KeyMapping(
		"key.sfu.open_menu",
		KEYSYM,
		KEY_RSHIFT,
		KeyMapping.Category.MISC
	));

	@Override
	public void onInitializeClient() {
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			while (OPEN_MENU_KEY.consumeClick()) {
				toggleMenu(client);
			}
		});
	}

	private static void toggleMenu(Minecraft client) {
		if (client.screen instanceof GuiMain) {
			client.setScreen(null);
			return;
		}

		client.setScreen(new GuiMain(Component.literal("Skyblock Farming Utils")));
	}
}
