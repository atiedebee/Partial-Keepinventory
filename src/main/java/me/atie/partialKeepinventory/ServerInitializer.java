package me.atie.partialKeepinventory;

import me.atie.partialKeepinventory.component.pkiComponentList;
import me.atie.partialKeepinventory.component.pkiSettings;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.List;

import static me.atie.partialKeepinventory.PartialKeepInventory.CONFIG_COMPONENT;

public class ServerInitializer implements DedicatedServerModInitializer {
    @Override
    public void onInitializeServer() {
        PartialKeepInventory.environment = EnvType.SERVER;
        PartialKeepInventory.LOCAL_CONFIG = CONFIG_COMPONENT;

        ServerPlayConnectionEvents.INIT.register( (handler, server) -> {
            PartialKeepInventory.server = server;
            CONFIG_COMPONENT = pkiComponentList.configKey.get(server.getScoreboard());
            CONFIG_COMPONENT.update();

            PartialKeepInventory.LOGGER.info("Server side updated config component: " + CONFIG_COMPONENT);
        } );

        ServerPlayNetworking.registerGlobalReceiver(pkiSettings.updateServerConfig,
                (server, player, handler, buf, responseSender) -> {
                    PartialKeepInventory.LOGGER.info("Received config update");

                    CONFIG_COMPONENT.packetReader(buf);
                    CONFIG_COMPONENT.update();

                    //Dont send the updated settings to the player who sent the packet
                    List<ServerPlayerEntity> players = PartialKeepInventory.server.getPlayerManager()
                            .getPlayerList().stream()
                            .filter(p -> !p.equals(player)).toList();

                    for(var p: players ){
                        ServerPlayNetworking.send(p, pkiSettings.serverConfigUpdated, PacketByteBufs.empty());
                    }
        });



        ServerPlayNetworking.registerGlobalReceiver(pkiSettings.requestServerConfig,
                (server, player, handler, buf, responseSender) -> {
                    PartialKeepInventory.LOGGER.info("Received request for Configuration");
                    PacketByteBuf buffer = PacketByteBufs.create();

                    CONFIG_COMPONENT.packetWriter(buffer);

                    Packet<?> packet = responseSender.createPacket(pkiSettings.sendServerConfig, buffer);
                    responseSender.sendPacket(packet);
        });

    }
}
