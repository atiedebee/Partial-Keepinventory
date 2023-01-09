package me.atie.partialKeepinventory.util;

import net.minecraft.server.network.ServerPlayerEntity;

import static me.atie.partialKeepinventory.PartialKeepInventory.CONFIG_COMPONENT;

public class Inventory {
    public static boolean shouldDropInventory(ServerPlayerEntity player) {
        return !CONFIG_COMPONENT.savedPlayersTeam.getPlayerList().contains(player.getEntityName());
    }
}
