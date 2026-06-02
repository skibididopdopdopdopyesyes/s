package com.swill.killaura;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class KillAuraMod implements ClientModInitializer {
    public static KillAuraModule killAura = new KillAuraModule();
    public static KeyBinding toggleKey;
    
    @Override
    public void onInitializeClient() {
        toggleKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.killaura.toggle",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_R,
            "category.killaura"
        ));
        
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (toggleKey.wasPressed()) {
                killAura.toggle();
            }
            if (killAura.isEnabled()) {
                killAura.onTick();
            }
        });
        
        System.out.println("[SWILL] KillAura loaded for 1.16.5 Fabric");
    }
}
