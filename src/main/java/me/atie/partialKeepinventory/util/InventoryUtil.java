package me.atie.partialKeepinventory.util;

import me.atie.partialKeepinventory.KeepinvMode;
import net.minecraft.server.network.ServerPlayerEntity;

import static me.atie.partialKeepinventory.PartialKeepInventory.CONFIG;

public class InventoryUtil {
    final public static DropBehaviour NO_DROPBEHAVIOUR = new DropBehaviour(DropAction.NONE, 0.0);

    public static boolean shouldDropInventory(ServerPlayerEntity player) {
        return !CONFIG.getSavedPlayers().contains(player.getEntityName());
    }

    public static void copyInventory(ServerPlayerEntity oldPlayer, ServerPlayerEntity newPlayer, boolean alive) {
        if( !alive && CONFIG.getEnableMod() && CONFIG.getPartialKeepinvMode() != KeepinvMode.VANILLA){
            newPlayer.getInventory().clone(oldPlayer.getInventory());
        }
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
