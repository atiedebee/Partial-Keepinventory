package me.atie.partialKeepinventory.impl;

import me.atie.partialKeepinventory.PartialKeepInventory;
import net.fabricmc.loader.api.FabricLoader;

public class impl {
    public static boolean trinketsInstalled;
    public static void loadImplementations() {
        trinketsInstalled = FabricLoader.getInstance().isModLoaded("trinkets");
        loadTrinketsImpl();
    }

    private static void loadTrinketsImpl(){
        if( trinketsInstalled ){
            PartialKeepInventory.LOGGER.info("Trinkets compatibility enabled");
            TrinketsImpl.addInventoryGetters();
        }
    }
}
