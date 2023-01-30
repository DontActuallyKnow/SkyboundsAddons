package com.toomuchiq.sbp.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.toomuchiq.sbp.utils.Utils;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class HelloWorldCommand  {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("hw").executes(HelloWorldCommand::execute));
    }

    private static int execute(CommandContext<CommandSourceStack> command)  {
        if(command.getSource().getEntity() instanceof LocalPlayer player) {
            Utils.sendMessage(player, "&aH&be&cl&dl&eo &1W&2o&3r&4l&5d&l&4!");
        }
        return Command.SINGLE_SUCCESS;
    }

}
