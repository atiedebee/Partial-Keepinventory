package me.atie.partialKeepinventory;

import net.minecraft.text.Text;

public enum KeepinvMode {
    STATIC, RARITY, CUSTOM, VANILLA;

    public static Text getName(KeepinvMode e) {
        return Text.translatable(PartialKeepInventory.ID + ".KeepinvMode." + e.toString());
    }

    public Text getThisName(){
        return Text.translatable(PartialKeepInventory.ID + ".KeepinvMode." + this);
    }

    public KeepinvMode next() {
        return switch (this) {
            case STATIC -> RARITY;
            case RARITY -> CUSTOM;
            case CUSTOM -> VANILLA;
            case VANILLA -> STATIC;
        };
    }

    public KeepinvMode previous() {
        return switch (this) {
            case STATIC -> VANILLA;
            case VANILLA -> CUSTOM;
            case CUSTOM -> RARITY;
            case RARITY -> STATIC;
        };
    }

}
