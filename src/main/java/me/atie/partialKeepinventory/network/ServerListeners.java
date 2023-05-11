package me.atie.partialKeepinventory.network;

import me.atie.partialKeepinventory.PartialKeepInventory;
import me.atie.partialKeepinventory.settings.BwSettingsCompat;
import me.atie.partialKeepinventory.settings.pkiSettings;
import me.atie.partialKeepinventory.settings.pkiVersion;
import me.atie.partialKeepinventory.util.ServerPlayerClientVersion;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.HashMap;
import java.util.HashSet;

import static me.atie.partialKeepinventory.PartialKeepInventory.CONFIG;

@Environment(EnvType.SERVER)
public class ServerListeners {

    private static final HashSet<ServerPlayerEntity> pkiPlayers = new HashSet<>();

    public static void init() {
//        ServerPlayConnectionEvents.JOIN.register(ServerListeners::sendConfig);
        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> pkiPlayers.remove(handler.getPlayer()));

        ServerPlayNetworking.registerGlobalReceiver(Identifiers.configUpdatePacket, ServerListeners::updateConfig);
        ServerPlayNetworking.registerGlobalReceiver(Identifiers.clientVersionPacket, (server, player, handler, buf, sender) -> {
            pkiVersion playerVersion = new pkiVersion(buf);

            if( playerVersion.lessThan(PartialKeepInventory.modVersion) ){
                ((ServerPlayerClientVersion)player).setClientPKIVersion(playerVersion);
            }else {
                ((ServerPlayerClientVersion) player).setClientPKIVersion(PartialKeepInventory.modVersion);
            }
            pkiPlayers.add(player);
            sendConfig(handler, sender, server, playerVersion);
        });
    }


    private static void updateConfig(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        if( !player.hasPermissionLevel(2) ) {
            player.sendMessage(Text.translatable(PartialKeepInventory.getID() + ".error.insufficientPermissions"));
            return;
        }
        pkiSettings temp = new pkiSettings();
        temp.packetReader(buf);
        try{
            temp.validate();
        }catch(Exception e){
            player.sendMessage(Text.literal("Invalid settings: " + e.getMessage()));
            return;
        }

        CONFIG = temp;
        sendConfigToPlayers(CONFIG);
    }

    public static void sendConfigToPlayers(pkiSettings setting){
        HashMap<pkiVersion, PacketByteBuf> bufList = new HashMap<>();

        // send all players with the mod the new config

        for (ServerPlayerEntity p : pkiPlayers) {
            pkiVersion v = ((ServerPlayerClientVersion)p).getClientPKIVersion();
            PacketByteBuf sendBuf = bufList.get(v);

            if( sendBuf == null ){
                sendBuf = PacketByteBufs.create();
                BwSettingsCompat.writePacket(setting, v, sendBuf);
                bufList.put(v, sendBuf);
            }

            ServerPlayNetworking.send(p, Identifiers.configUpdatePacket, sendBuf);
        }
    }

    private static void sendConfig(ServerPlayNetworkHandler handler, PacketSender sender, MinecraftServer server, pkiVersion version) {
        PacketByteBuf buf = PacketByteBufs.create();
        BwSettingsCompat.writePacket(CONFIG, version, buf);

        Packet<?> packet = sender.createPacket(Identifiers.configUpdatePacket, buf);
        sender.sendPacket(packet);
    }

}
