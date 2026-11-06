package com.lony;

import com.lony.gui.LonyScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class LonyClientMod implements ClientModInitializer {

    private static KeyBinding openGuiKey;
    private static LonyScreen lonyScreenInstance;

    @Override
    public void onInitializeClient() {
        openGuiKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.lonyclient.open_gui",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_RIGHT_SHIFT,
                "category.lonyclient.general"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openGuiKey.wasPressed()) {
                if (lonyScreenInstance == null) {
                    lonyScreenInstance = new LonyScreen();
                }
                if (client.currentScreen instanceof LonyScreen) {
                    client.setScreen(null);
                } else {
                    client.setScreen(lonyScreenInstance);
                }
            }
        });
    }
}
