package me.atie.partialKeepinventory.network;

import me.atie.partialKeepinventory.PartialKeepInventory;
import me.atie.partialKeepinventory.settings.pkiSettings;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;

import static me.atie.partialKeepinventory.PartialKeepInventory.CONFIG;

public class ClientListeners {
    public static void init(){
        ClientPlayConnectionEvents.JOIN.register(ClientListeners::onPlayerJoin);

        ClientPlayNetworking.registerGlobalReceiver(Identifiers.configUpdatePacket,
                ClientListeners::configReceiver);
    }

    private static void onPlayerJoin(ClientPlayNetworkHandler handler, PacketSender sender, MinecraftClient client) {
        PacketByteBuf buf = PacketByteBufs.create();
        PartialKeepInventory.modVersion.writePacket(buf);
        Packet<?> packet = sender.createPacket(Identifiers.clientVersionPacket, buf);
        sender.sendPacket(packet);
    }

    private static void configReceiver(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        if( CONFIG == null ){
            CONFIG = new pkiSettings();
        }
        CONFIG.packetReader(buf);
    }
}
