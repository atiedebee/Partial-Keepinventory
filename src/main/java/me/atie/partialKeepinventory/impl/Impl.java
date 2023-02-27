package me.atie.partialKeepinventory.impl;

import me.atie.partialKeepinventory.PartialKeepInventory;
import me.atie.partialKeepinventory.api.pkiApi;
import me.atie.partialKeepinventory.impl.trinkets.TrinketsImpl;
import me.atie.partialKeepinventory.api.pkiSettingsApi;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

public class Impl {
    public static LinkedHashMap<String, pkiApi> entryPoints = new LinkedHashMap<>(24, 0.8f);
    public static List<pkiSettingsApi> settings;
    public static ArrayList<Supplier<Screen>> settingScreenSuppliers = new ArrayList<>();
    public static ArrayList<Text> modNames = new ArrayList<>();

    public static void loadImplementations() {
        ArrayList<pkiApi> entryPoints = new ArrayList<>(FabricLoader.getInstance().getEntrypoints("partial-keepinv", pkiApi.class));

        boolean trinketsInstalled = FabricLoader.getInstance().isModLoaded("trinkets");
        if( trinketsInstalled ) {
            entryPoints.add(new TrinketsImpl());
        }


        for( var entryPoint: entryPoints ){
            Impl.entryPoints.put(entryPoint.getModId(), entryPoint);
        }

        settings = Impl.entryPoints.values().stream().map(pkiApi::getSettings).filter(Objects::nonNull).toList();
        PartialKeepInventory.LOGGER.info("Settings: ");
        for( var setting: settings){
            PartialKeepInventory.LOGGER.info(setting.toString());
        }

    }

}
