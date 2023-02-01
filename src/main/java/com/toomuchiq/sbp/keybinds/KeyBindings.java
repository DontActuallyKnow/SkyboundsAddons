package com.toomuchiq.sbp.keybinds;

import com.toomuchiq.sbp.ui.ConfigGUI;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import net.minecraftforge.client.ClientRegistry;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.lwjgl.glfw.GLFW;

public class KeyBindings {

    private static final String CATEGORY = "SkyboundsPlus";
    public static KeyMapping toggleGui = new KeyMapping(I18n.get("configGui.keybind"), GLFW.GLFW_KEY_G, CATEGORY);

    public static void setup() {
        ClientRegistry.registerKeyBinding(toggleGui);
    }

    @SubscribeEvent
    public static void eventInput(InputEvent event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || Minecraft.getInstance().screen != null || Minecraft.getInstance().level == null)
            return;

        if(toggleGui.consumeClick()) {
            Minecraft.getInstance().setScreen(new ConfigGUI());
        }
    }

}
