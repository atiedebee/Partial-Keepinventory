package me.atie.partialKeepinventory.impl;

import me.atie.partialKeepinventory.api.pkiApi;
import me.atie.partialKeepinventory.api.pkiSettingsApi;
import me.atie.partialKeepinventory.impl.trinkets.TrinketsImpl;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screen.Screen;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.function.Function;

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
            Impl.entryPoints.put(entryPoint.getModId(), entryPoint);

            if( entryPoint.getSettings() != null ){
                settings.add(entryPoint.getSettings());
            }




        }


    }

}
