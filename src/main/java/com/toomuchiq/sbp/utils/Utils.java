package com.toomuchiq.sbp.utils;

import com.ibm.icu.impl.locale.LocaleSyntaxException;
import net.minecraft.Util;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.player.Player;

public class Utils {

    public static String chat(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    public static void sendMessage(Player player, String text) {
        player.sendMessage(new TextComponent(Utils.chat(text)), Util.NIL_UUID);
    }

}
