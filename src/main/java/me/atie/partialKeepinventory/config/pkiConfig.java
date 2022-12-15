package me.atie.partialKeepinventory.config;


import eu.midnightdust.lib.config.MidnightConfig;
import me.atie.partialKeepinventory.partialKeepinventory;

public class pkiConfig extends MidnightConfig {

    @Entry public static boolean enableMod = true;
    @Entry public static partialKeepinventory.KeepinvMode partialKeepinvMode = partialKeepinventory.KeepinvMode.PERCENTAGE;

    @Entry public static int inventoryDroprate = 100;


    @Entry public static int commonDroprate = 100;
    @Entry public static int uncommonDroprate = 100;
    @Entry public static int rareDroprate = 100;
    @Entry public static int epicDroprate = 100;
}
