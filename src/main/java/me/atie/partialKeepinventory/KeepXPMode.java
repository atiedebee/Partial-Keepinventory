package me.atie.partialKeepinventory;

import net.minecraft.text.Text;

public enum KeepXPMode {
    STATIC_LEVELS, STATIC_POINTS, VANILLA;

    public KeepXPMode next() {
        return switch(this){
            case STATIC_LEVELS -> STATIC_POINTS;
            case STATIC_POINTS -> VANILLA;
            case VANILLA -> STATIC_LEVELS;
        };
    }

    public KeepXPMode previous() {
        return switch(this){
            case STATIC_LEVELS -> VANILLA;
            case VANILLA -> STATIC_POINTS;
            case STATIC_POINTS -> STATIC_LEVELS;
        };
    }

    public static Text getName(KeepXPMode e) {
        return Text.translatable(PartialKeepInventory.getID() + ".KeepXpMode." + e.toString());
    }
}
