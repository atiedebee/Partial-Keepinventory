package me.atie.partialKeepinventory.util;

import net.minecraft.server.network.ServerPlayerEntity;

import static me.atie.partialKeepinventory.PartialKeepInventory.CONFIG;

public class InventoryUtil {
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
