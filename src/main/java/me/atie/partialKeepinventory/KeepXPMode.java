package me.atie.partialKeepinventory;

import net.minecraft.text.Text;

public enum KeepXPMode {
    STATIC_LEVELS, STATIC_POINTS, VANILLA, CUSTOM_LEVELS, CUSTOM_POINTS;

    public KeepXPMode next() {
        return switch(this){
            case VANILLA -> STATIC_LEVELS;
            case STATIC_LEVELS -> STATIC_POINTS;
            case STATIC_POINTS -> CUSTOM_LEVELS;
            case CUSTOM_LEVELS -> CUSTOM_POINTS;
            case CUSTOM_POINTS -> VANILLA;
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

    public KeepXPMode notCustom(){
        return switch(this){
            case CUSTOM_POINTS -> STATIC_POINTS;
            case CUSTOM_LEVELS -> STATIC_LEVELS;
            default -> this;
        };
    }

    public static Text getName(KeepXPMode e) {
        return Text.translatable(PartialKeepInventory.getID() + ".KeepXpMode." + e.toString());
    }
}
