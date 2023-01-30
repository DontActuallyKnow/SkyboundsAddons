package com.toomuchiq.sbp;

import com.toomuchiq.sbp.commands.HelloWorldCommand;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod(Main.MODID)
public class Main {

    public static final String MODID = "sbp";

    public Main() {

    }

    @SubscribeEvent
    public static void onClientCommandRegister(RegisterCommandsEvent event) {
        HelloWorldCommand.register(event.getDispatcher());
    }

}
