package com.toomuchiq.sbp.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.toomuchiq.sbp.Main;
import com.toomuchiq.sbp.config.Config;
import com.toomuchiq.sbp.utils.Utils;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.world.entity.player.Player;

public class DebugCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("debug").executes(DebugCommand::execute));
    }

    private static int execute(CommandContext<CommandSourceStack> command)  {
        Config.COMMON.debug.set(!Config.COMMON.debug.get());

        if(command.getSource().getEntity() instanceof Player player) {
            Utils.sendMessage(player, "Set debug mode to &a&l" + Config.COMMON.debug.get().toString().toUpperCase());
        }
        return Command.SINGLE_SUCCESS;
    }

}
