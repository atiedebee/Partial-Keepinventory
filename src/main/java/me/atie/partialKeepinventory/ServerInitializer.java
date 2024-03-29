package me.atie.partialKeepinventory;

import me.atie.partialKeepinventory.network.ServerListeners;
import me.atie.partialKeepinventory.settings.pkiSettings;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;

import static me.atie.partialKeepinventory.PartialKeepInventory.CONFIG;

@Environment(EnvType.SERVER)
public class ServerInitializer implements DedicatedServerModInitializer {

    @Override
    public void onInitializeServer() {
        PartialKeepInventory.environment = EnvType.SERVER;

        ServerListeners.init();

        ServerPlayConnectionEvents.INIT.register( (handler, server) -> {
            CONFIG = pkiSettings.getServerState(server);
        } );
    }

}
