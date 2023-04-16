package me.atie.partialKeepinventory.impl;

import me.atie.partialKeepinventory.PartialKeepInventory;
import me.atie.partialKeepinventory.api.pkiApi;
import me.atie.partialKeepinventory.impl.trinkets.TrinketsImpl;
import me.atie.partialKeepinventory.api.pkiSettingsApi;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

public class Impl {
    public static LinkedHashMap<String, pkiApi> entryPoints;
    public static ArrayList<pkiSettingsApi> settings;
    public static LinkedHashMap< String, Function<Screen, Screen> > screenFactories = new LinkedHashMap<>();
    public static void loadImplementations() {
        ArrayList<pkiApi> entryPoints = new ArrayList<>(FabricLoader.getInstance().getEntrypoints("partial-keepinv", pkiApi.class));

        boolean trinketsInstalled = FabricLoader.getInstance().isModLoaded("trinkets");
        if( trinketsInstalled ) {
            entryPoints.add(new TrinketsImpl());
        }
        Impl.entryPoints = new LinkedHashMap<>((int) (entryPoints.size() * 1.5), 0.7f);
        settings = new ArrayList<>(entryPoints.size());


        for( var entryPoint: entryPoints ){
            PartialKeepInventory.LOGGER.info("Added " + entryPoint.getModId());

            Impl.entryPoints.put(entryPoint.getModId(), entryPoint);

            if( entryPoint.getSettings() != null ){
                settings.add(entryPoint.getSettings());
            }




        }


    }

}
