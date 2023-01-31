package com.toomuchiq.sbp.utils;

import com.ibm.icu.impl.locale.LocaleSyntaxException;
import com.toomuchiq.sbp.Main;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Minecart;

public class Utils {

    public static String chat(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    public static void sendMessage(Player player, String text) {
        player.sendMessage(new TextComponent(Utils.chat(Main.PREFIX + text)), Util.NIL_UUID);
    }

    public static int noDebug() {
        Minecraft.getInstance().player.sendMessage(new TextComponent(Utils.chat(Main.PREFIX + "You do not have debug mode enabled")), Util.NIL_UUID);
        return 1;
    }

}
