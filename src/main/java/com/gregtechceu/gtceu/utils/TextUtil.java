package com.gregtechceu.gtceu.utils;

import net.minecraft.ChatFormatting;

import static net.minecraft.ChatFormatting.*;

public class TextUtil {

    public static String formatting(String input, ChatFormatting[] colours, double delay) {
        StringBuilder sb = new StringBuilder(input.length() * 3);
        if (delay <= 0.0D)
            delay = 0.001D;
        int offset = (int) Math.floor((System.currentTimeMillis() & 0x3FFFL) / delay) % colours.length;
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            sb.append(colours[(colours.length + i - offset) % colours.length].toString());
            sb.append(c);
        }
        return sb.toString();
    }

    public static String full_color(String input) {
        return formatting(input, new ChatFormatting[] { RED, GOLD, YELLOW, GREEN, AQUA, BLUE, LIGHT_PURPLE }, 80.0D);
    }

    public static String dark_purplish_red(String input) {
        return formatting(input, new ChatFormatting[] { DARK_PURPLE, DARK_RED }, 160.0D);
    }

    public static String white_blue(String input) {
        return formatting(input, new ChatFormatting[] { BLUE, BLUE, BLUE, BLUE, WHITE, BLUE, WHITE, WHITE, BLUE,
                WHITE, WHITE, BLUE, RED, WHITE }, 80.0D);
    }

    public static String purplish_red(String input) {
        return formatting(input, new ChatFormatting[] { LIGHT_PURPLE, DARK_PURPLE }, 160.0D);
    }

    public static String golden(String input) {
        return formatting(input, new ChatFormatting[] { YELLOW, GOLD }, 160.0D);
    }

    public static String dark_green(String input) {
        return formatting(input, new ChatFormatting[] { GREEN, DARK_GREEN }, 160.0D);
    }
}
