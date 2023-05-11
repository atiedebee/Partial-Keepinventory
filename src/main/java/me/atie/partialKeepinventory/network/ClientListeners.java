package me.atie.partialKeepinventory.network;

import me.atie.partialKeepinventory.PartialKeepInventory;
import me.atie.partialKeepinventory.settings.pkiSettings;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;

import java.util.Objects;

import static me.atie.partialKeepinventory.PartialKeepInventory.CONFIG;

@Environment(EnvType.CLIENT)
public class ClientListeners {
    public static void init(){
        ClientPlayConnectionEvents.JOIN.register(ClientListeners::onPlayerJoin);

        ClientPlayNetworking.registerGlobalReceiver(Identifiers.configUpdatePacket,
                ClientListeners::configReceiver);
    }

    private static void onPlayerJoin(ClientPlayNetworkHandler handler, PacketSender sender, MinecraftClient client) {
        ServerInfo i = handler.getServerInfo();
        if( i != null && !i.isLocal() ) {
            CONFIG = new pkiSettings();
            CONFIG.validSettings = false;
            PacketByteBuf buf = PacketByteBufs.create();

            PartialKeepInventory.modVersion.writePacket(buf);
            Packet<?> packet = sender.createPacket(Identifiers.clientVersionPacket, buf);
            sender.sendPacket(packet);
        }
    }


    private static void configReceiver(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        CONFIG.packetReader(buf);
    }
}
