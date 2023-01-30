package com.toomuchiq.sbp.utils;

import com.google.common.collect.Maps;
import net.minecraft.ChatFormatting;
import org.apache.commons.lang3.Validate;

import java.util.Map;
import java.util.regex.Pattern;

public enum ChatColor {
    BLACK('0', ChatFormatting.BLACK),
    DARK_BLUE('1', ChatFormatting.DARK_BLUE),
    DARK_GREEN('2', ChatFormatting.DARK_GREEN),
    DARK_AQUA('3', ChatFormatting.DARK_AQUA),
    DARK_RED('4', ChatFormatting.DARK_RED),
    DARK_PURPLE('5', ChatFormatting.DARK_PURPLE),
    GOLD('6', ChatFormatting.GOLD),
    GRAY('7', ChatFormatting.GRAY),
    DARK_GRAY('8', ChatFormatting.DARK_GRAY),
    BLUE('9', ChatFormatting.BLUE),
    GREEN('a', ChatFormatting.GREEN),
    AQUA('b', ChatFormatting.AQUA),
    RED('c', ChatFormatting.RED),
    LIGHT_PURPLE('d', ChatFormatting.LIGHT_PURPLE),
    YELLOW('e', ChatFormatting.YELLOW),
    WHITE('f', ChatFormatting.WHITE),
    MAGIC('k', ChatFormatting.OBFUSCATED, true),
    BOLD('l', ChatFormatting.BOLD, true),
    STRIKETHROUGH('m', ChatFormatting.STRIKETHROUGH, true),
    UNDERLINE('n', ChatFormatting.UNDERLINE, true),
    ITALIC('o', ChatFormatting.ITALIC, true),
    RESET('r', ChatFormatting.RESET);

    public static final char COLOR_CHAR = '§';
    private static final Pattern STRIP_COLOR_PATTERN = Pattern.compile("(?i)" + String.valueOf(COLOR_CHAR) + "[0-9A-FK-OR]");

    private final ChatFormatting chatFormatting;
    private final char code;
    private final boolean isFormat;
    private final String toString;
    private final static Map<ChatFormatting, ChatColor> BY_ID = Maps.newHashMap();
    private final static Map<Character, ChatColor> BY_CHAR = Maps.newHashMap();

    private ChatColor(char code, ChatFormatting chatFormatting) {
        this(code, chatFormatting, false);
    }

    private ChatColor(char code, ChatFormatting chatFormatting, boolean isFormat) {
        this.code = code;
        this.chatFormatting = chatFormatting;
        this.isFormat = isFormat;
        this.toString = new String(new char[] {COLOR_CHAR, code});
    }

    public char getChar() {
        return code;
    }

    @Override
    public String toString() {
        return toString;
    }

    public boolean isFormat() {
        return isFormat;
    }

    public boolean isColor() {
        return !isFormat && this != RESET;
    }

    public static ChatColor getByChar(char code) {
        return BY_CHAR.get(code);
    }

    public static ChatColor getByChar(String code) {
        Validate.notNull(code, "Code cannot be null");
        Validate.isTrue(code.length() > 0, "Code must have at least one char");

        return BY_CHAR.get(code.charAt(0));
    }

    public static String stripColor(final String input) {
        if (input == null) {
            return null;
        }

        return STRIP_COLOR_PATTERN.matcher(input).replaceAll("");
    }

    public static String translateAlternateColorCodes(char altColorChar, String textToTranslate) {
        char[] b = textToTranslate.toCharArray();
        for (int i = 0; i < b.length - 1; i++) {
            if (b[i] == altColorChar && "0123456789AaBbCcDdEeFfKkLlMmNnOoRr".indexOf(b[i+1]) > -1) {
                b[i] = COLOR_CHAR;
                b[i+1] = Character.toLowerCase(b[i+1]);
            }
        }
        return new String(b);
    }

    static {
        for (ChatColor color : values()) {
            BY_ID.put(color.chatFormatting, color);
            BY_CHAR.put(color.code, color);
        }
    }


}

