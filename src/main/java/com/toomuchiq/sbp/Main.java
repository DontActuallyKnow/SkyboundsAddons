package com.toomuchiq.sbp;

import com.toomuchiq.sbp.commands.DebugCommand;
import com.toomuchiq.sbp.commands.HelloWorldCommand;
import com.toomuchiq.sbp.config.Config;
import com.toomuchiq.sbp.utils.Utils;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;

@Mod(Main.MODID)
public class Main {

    public static final String MODID = "sbp";
    public static final String PREFIX = Utils.chat("&4[&cSBP&4] &6");

    public Main() {
        MinecraftForge.EVENT_BUS.register(this);
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, Config.COMMON_SPEC);
    }

    @SubscribeEvent
    public void onClientCommandRegister(RegisterClientCommandsEvent event) {
        HelloWorldCommand.register(event.getDispatcher());
        DebugCommand.register(event.getDispatcher());
    }

}
