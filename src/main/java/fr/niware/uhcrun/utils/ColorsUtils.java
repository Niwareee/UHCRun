package fr.niware.uhcrun.utils;

import org.bukkit.Color;

public enum ColorsUtils {

    WHITE(Color.WHITE),
    AQUA(Color.AQUA),
    BLUE(Color.BLUE),
    FUCHSIA(Color.FUCHSIA),
    GRAY(Color.GRAY),
    GREEN(Color.GREEN),
    LIME(Color.LIME),
    MAROON(Color.MAROON),
    NAVY(Color.NAVY),
    OLIVE(Color.OLIVE),
    ORANGE(Color.ORANGE),
    PURPLE(Color.PURPLE),
    RED(Color.RED),
    SILVER(Color.SILVER),
    TEAL(Color.TEAL),
    YELLOW(Color.YELLOW);

    private final Color color;

    ColorsUtils(Color color) {
        this.color = color;
    }

    public static Color getColor(int i) {
        return i > values().length ? Color.WHITE : values()[i + 1].color;
    }
}