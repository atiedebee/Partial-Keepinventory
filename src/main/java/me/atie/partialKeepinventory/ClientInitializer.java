package me.atie.partialKeepinventory;

import me.atie.partialKeepinventory.component.pkiSettings;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.network.Packet;

import static me.atie.partialKeepinventory.PartialKeepInventory.CONFIG;


public class ClientInitializer implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        PartialKeepInventory.environment = EnvType.CLIENT;

        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            requestConfig(sender);
        });

        ClientPlayNetworking.registerGlobalReceiver(pkiSettings.sendServerConfig,
                (client, handler, buf, responseSender) -> {
                    PartialKeepInventory.LOGGER.info("Received updated configuration");
                    CONFIG.packetReader(buf);
                });

    }

    private static void requestConfig(PacketSender sender){
        Packet<?> packet = sender.createPacket(pkiSettings.requestServerConfig, PacketByteBufs.empty());
        sender.sendPacket(packet);
    }
}
