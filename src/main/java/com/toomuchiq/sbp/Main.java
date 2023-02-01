package com.toomuchiq.sbp;

import com.toomuchiq.sbp.commands.DebugCommand;
import com.toomuchiq.sbp.commands.HelloWorldCommand;
import com.toomuchiq.sbp.config.Config;
import com.toomuchiq.sbp.event.ClientEventHandler;
import com.toomuchiq.sbp.keybinds.KeyBindings;
import com.toomuchiq.sbp.utils.Utils;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;

import java.awt.*;

@Mod(Main.MODID)
public class Main {

    //Colour Scheme?? Just change if you dislike
    public static class Colors {
        public static final Color mainColor = new Color(0, 246, 216);
        public static final Color secondaryColor = new Color(4, 37, 204);
        public static final Color boldColor = new Color(237, 187, 0);
        public static final Color mainColor2 = new Color(192, 4, 204);
        public static final Color secondaryColor2 = new Color(89, 4, 204);
        public static final Color contrastColor = new Color(0, 246, 149);
    }

    public static final String MODID = "sbp";
    public static final String PREFIX = Utils.chat("&4[&cSBP&4] &6");

    public static int tickTime = 0;

    public Main() {
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(KeyBindings.class);
        MinecraftForge.EVENT_BUS.addListener(ClientEventHandler::clientTickEvent);

        KeyBindings.setup();

        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, Config.COMMON_SPEC);
    }

    @SubscribeEvent
    public void onClientCommandRegister(RegisterClientCommandsEvent event) {
        HelloWorldCommand.register(event.getDispatcher());
        DebugCommand.register(event.getDispatcher());
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        tickTime++;
    }

}
