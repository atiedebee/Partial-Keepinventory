package me.atie.partialKeepinventory;

import me.atie.partialKeepinventory.network.ServerListeners;
import me.atie.partialKeepinventory.settings.pkiSettings;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;

import static me.atie.partialKeepinventory.PartialKeepInventory.CONFIG;

public class ServerInitializer implements DedicatedServerModInitializer {


    @Override
    public void onInitializeServer() {
        PartialKeepInventory.environment = EnvType.SERVER;

        ServerListeners.init();

        ServerPlayConnectionEvents.INIT.register( (handler, server) -> {
            PartialKeepInventory.server = server;
            CONFIG = pkiSettings.getServerState(server);

            PartialKeepInventory.LOGGER.info("Server side initialized config component: " + CONFIG);
        } );



    }
}
