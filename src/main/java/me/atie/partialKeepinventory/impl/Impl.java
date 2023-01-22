package me.atie.partialKeepinventory.impl;

import me.atie.partialKeepinventory.impl.trinkets.TrinketsImpl;
import net.fabricmc.loader.api.FabricLoader;

public class Impl {
    public static boolean trinketsInstalled;
    public static void loadImplementations() {
        trinketsInstalled = FabricLoader.getInstance().isModLoaded("trinkets");

        if( trinketsInstalled ) {
            TrinketsImpl.load();
        }
    }

}
