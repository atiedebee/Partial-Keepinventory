package me.atie.partialKeepinventory;

import me.atie.partialKeepinventory.network.ClientListeners;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;


public class ClientInitializer implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        PartialKeepInventory.environment = EnvType.CLIENT;
        ClientListeners.init();
    }

}
