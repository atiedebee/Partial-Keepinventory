package me.atie.partialKeepinventory;

import net.minecraft.text.Text;

public enum KeepXPMode {
    STATIC_LEVELS, STATIC_POINTS, VANILLA, CUSTOM_LEVELS, CUSTOM_POINTS;

    public KeepXPMode next() {
        return switch(this){
            case STATIC_LEVELS -> STATIC_POINTS;
            case STATIC_POINTS -> VANILLA;
            case VANILLA -> CUSTOM_LEVELS;
            case CUSTOM_LEVELS -> CUSTOM_POINTS;
            case CUSTOM_POINTS -> STATIC_LEVELS;
        };
    }

    public KeepXPMode previous() {
        return switch(this){
            case STATIC_POINTS -> STATIC_LEVELS;
            case STATIC_LEVELS -> CUSTOM_POINTS;
            case CUSTOM_POINTS -> CUSTOM_LEVELS;
            case CUSTOM_LEVELS -> VANILLA;
            case VANILLA -> STATIC_POINTS;
        };
    }

    public static Text getName(KeepXPMode e) {
        return Text.translatable(PartialKeepInventory.getID() + ".KeepXpMode." + e.toString());
    }
}
