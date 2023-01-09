package me.atie.partialKeepinventory;

import net.minecraft.text.Text;

public enum KeepinvMode {
    STATIC, RARITY, CUSTOM, VANILLA;

    public static Text getName(KeepinvMode e) {
        return Text.translatable(PartialKeepInventory.getID() + ".KeepinvMode." + e.toString());
    }

    public Text getThisName(){
        return Text.translatable(PartialKeepInventory.getID() + ".KeepinvMode." + this);
    }

}
