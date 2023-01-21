package me.atie.partialKeepinventory;

import me.atie.partialKeepinventory.component.pkiSettings;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.List;

import static me.atie.partialKeepinventory.PartialKeepInventory.CONFIG;

public class ServerInitializer implements DedicatedServerModInitializer {
    @Override
    public void onInitializeServer() {
        PartialKeepInventory.environment = EnvType.SERVER;

        ServerPlayConnectionEvents.INIT.register( (handler, server) -> {
            PartialKeepInventory.server = server;
            CONFIG = pkiSettings.getServerState(server);

            PartialKeepInventory.LOGGER.info("Server side updated config component: " + CONFIG);
        } );

        ServerPlayNetworking.registerGlobalReceiver(pkiSettings.updateServerConfig,
                (server, player, handler, buf, responseSender) -> {
                    PartialKeepInventory.LOGGER.info("Received config update");

                    //TODO this is a temporary workaround, buttons that change settings shouldn't be clickable in the gui
                    if( !player.hasPermissionLevel(4) ){
                        player.sendMessage(Text.translatable(PartialKeepInventory.getID() + ".error.insufficientPermissions"));
                        return;
                    }

                    CONFIG.packetReader(buf);
                    // send all players the new config. Even the one who changed the config
                    List<ServerPlayerEntity> players = PartialKeepInventory.server.getPlayerManager().getPlayerList();

                    for(var p: players ){
                        ServerPlayNetworking.send(p, pkiSettings.sendServerConfig, buf);
                    }
        });



        ServerPlayNetworking.registerGlobalReceiver(pkiSettings.requestServerConfig,
                (server, player, handler, buf, responseSender) -> {
                    PartialKeepInventory.LOGGER.info("Received request for Configuration");
                    PacketByteBuf buffer = PacketByteBufs.create();

                    CONFIG.packetWriter(buffer);

                    Packet<?> packet = responseSender.createPacket(pkiSettings.sendServerConfig, buffer);
                    responseSender.sendPacket(packet);
        });

    }
}
