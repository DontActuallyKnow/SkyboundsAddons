package com.toomuchiq.sbp.event;

import com.toomuchiq.sbp.ui.ConfigGUI;
import net.minecraft.client.Minecraft;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ClientEventHandler {

    public static boolean openConfigGui;

    @SubscribeEvent
    public static void clientTickEvent(TickEvent.ClientTickEvent event) {
        if (TickEvent.Phase.END.equals(event.phase) && ClientEventHandler.openConfigGui) {
            ClientEventHandler.openConfigGui = false;
            Minecraft.getInstance().setScreen(null);
            Minecraft.getInstance().setScreen(new ConfigGUI());
        }
    }

}
