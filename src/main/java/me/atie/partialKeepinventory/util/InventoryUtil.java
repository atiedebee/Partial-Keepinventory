package me.atie.partialKeepinventory.util;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

import static me.atie.partialKeepinventory.PartialKeepInventory.CONFIG;

public class InventoryUtil {
    public static ArrayList< BiFunction< PlayerEntity, ItemStack, DropBehaviour > > droprateGetters = new ArrayList<>();
    // Optional<Double> func(PlayerEntity, ItemStack);
    public static ArrayList<Function<PlayerEntity, List<ItemStack>>> inventorySlotGetters = new ArrayList<>();

    public static DropBehaviour NO_DROPBEHAVIOUR = new DropBehaviour(DropAction.NONE, 0.0);

    public static boolean shouldDropInventory(ServerPlayerEntity player) {
        return !CONFIG.getSavedPlayers().contains(player.getEntityName());
    }

    public static class DropBehaviour{
        public DropAction action;
        public double droprate;
        public DropBehaviour(DropAction action, double droprate){
            this.action = action;
            this.droprate = droprate;
        }
    }

    public enum DropAction{
        DROP, KEEP, DESTROY, NONE
    }
}
