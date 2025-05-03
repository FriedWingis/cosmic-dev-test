package me.friedwingis.plugin.starforging.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;

import java.text.DecimalFormat;

/**
 * Copyright Fried - 2025
 * All code is private and not to be used by any
 * other entity unless explicitly stated otherwise.
 **/

/*
 * Utility class for converting Strings w/ Minimessage color
 * codes to Minimessage Components.
 */
public class Chat {
    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();

    public static Component format(String message) {
        return MINI_MESSAGE.deserialize(message).decoration(TextDecoration.ITALIC, false);
    }

    public static Component severe(String message) {
        return format("<#E74C3C><b><!></b> " + message);
    }

    public static Component warn(String message) {
        return format("<#F1C40F><b><!></b> " + message);
    }

    public static Component praise(String message) {
        return format("<#2ECC71><b><!></b> " + message);
    }
}
