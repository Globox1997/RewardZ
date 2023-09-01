package net.rewardz.init;

import org.lwjgl.glfw.GLFW;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.rewardz.packet.RewardsClientPacket;

@Environment(EnvType.CLIENT)
public class KeyInit {

    public static KeyBinding rewardKeyBinding;

    public static void init() {
        // Keybinds
        rewardKeyBinding = new KeyBinding("key.rewardz.reward", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, "category.rewardz.keybind");
        // Registering
        KeyBindingHelper.registerKeyBinding(rewardKeyBinding);
        // Callback
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (rewardKeyBinding.isPressed()) {
                RewardsClientPacket.writeC2SRewardsScreenPacket(client);
                return;
            }
        });
    }

}
