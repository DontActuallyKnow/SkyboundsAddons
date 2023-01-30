package com.toomuchiq.sbp;

import com.toomuchiq.sbp.commands.HelloWorldCommand;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod(Main.MODID)
public class Main {

    public static final String MODID = "sbp";

    public Main() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onClientCommandRegister(RegisterClientCommandsEvent event) {
        HelloWorldCommand.register(event.getDispatcher());
    }

}
