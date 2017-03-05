package me.esshd.api.utils;

import org.bukkit.ChatColor;

public class SpigotUtils {

    public static ChatColor toBungee(final ChatColor color) {
        switch (color) {
            case BLACK: {
                return ChatColor.BLACK;
            }
            case DARK_BLUE: {
                return ChatColor.DARK_BLUE;
            }
            case DARK_GREEN: {
                return ChatColor.DARK_GREEN;
            }
            case DARK_AQUA: {
                return ChatColor.DARK_AQUA;
            }
            case DARK_RED: {
                return ChatColor.DARK_RED;
            }
            case DARK_PURPLE: {
                return ChatColor.DARK_PURPLE;
            }
            case GOLD: {
                return ChatColor.GOLD;
            }
            case GRAY: {
                return ChatColor.GRAY;
            }
            case DARK_GRAY: {
                return ChatColor.DARK_GRAY;
            }
            case BLUE: {
                return ChatColor.BLUE;
            }
            case GREEN: {
                return ChatColor.GREEN;
            }
            case AQUA: {
                return ChatColor.AQUA;
            }
            case RED: {
                return ChatColor.RED;
            }
            case LIGHT_PURPLE: {
                return ChatColor.LIGHT_PURPLE;
            }
            case YELLOW: {
                return ChatColor.YELLOW;
            }
            case WHITE: {
                return ChatColor.WHITE;
            }
            case MAGIC: {
                return ChatColor.MAGIC;
            }
            case BOLD: {
                return ChatColor.BOLD;
            }
            case STRIKETHROUGH: {
                return ChatColor.STRIKETHROUGH;
            }
            case UNDERLINE: {
                return ChatColor.UNDERLINE;
            }
            case ITALIC: {
                return ChatColor.ITALIC;
            }
            case RESET: {
                return ChatColor.RESET;
            }
            default: {
                throw new IllegalArgumentException("Unrecognised Bukkit colour " + color.name() + ".");
            }
        }
    }
}
