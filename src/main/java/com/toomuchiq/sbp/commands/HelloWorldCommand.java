package com.toomuchiq.sbp.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.toomuchiq.sbp.config.Config;
import com.toomuchiq.sbp.event.ClientEventHandler;
import com.toomuchiq.sbp.utils.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.world.entity.player.Player;

public class HelloWorldCommand  {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("hw").executes(HelloWorldCommand::execute));
    }

    private static int execute(CommandContext<CommandSourceStack> command)  {
        Minecraft mc = Minecraft.getInstance();
        if(!Config.COMMON.debug.get()) return Utils.noDebug();

        if(command.getSource().getEntity() instanceof Player player) {
            Utils.sendMessage(player, "&aH&be&cl&dl&eo &1W&2o&3r&4l&5d&l&4!");
            if(mc.player == null || Minecraft.getInstance().level == null) {
                Utils.sendMessage(player, "How are you sending this command???");
                return Command.SINGLE_SUCCESS;
            }
            ClientEventHandler.openConfigGui = true;

        }
        return Command.SINGLE_SUCCESS;
    }

}
