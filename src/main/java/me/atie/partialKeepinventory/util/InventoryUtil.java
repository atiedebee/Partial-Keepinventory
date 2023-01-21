package me.atie.partialKeepinventory.util;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static me.atie.partialKeepinventory.PartialKeepInventory.CONFIG;

public class InventoryUtil {
    public static ArrayList<Function<PlayerEntity, List<ItemStack>>> inventorySlotGetters = new ArrayList<>();

    public static boolean shouldDropInventory(ServerPlayerEntity player) {
        return !CONFIG.getSavedPlayers().contains(player.getEntityName());
    }

}
