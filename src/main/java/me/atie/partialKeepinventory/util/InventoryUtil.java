package me.atie.partialKeepinventory.util;

import me.atie.partialKeepinventory.KeepinvMode;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import org.apache.commons.collections.functors.NonePredicate;

import java.util.Optional;

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
    public static double dropPercentageFromRarity(ItemStack item){
        double droprate =  switch( item.getRarity() ){
            case COMMON -> CONFIG.getCommonDroprate();
            case UNCOMMON -> CONFIG.getUncommonDroprate();
            case RARE -> CONFIG.getRareDroprate();
            case EPIC -> CONFIG.getEpicDroprate();
        };

        return droprate / 100.0;
    }
    public enum DropAction{
        DROP, KEEP, DESTROY, NONE;
        public static Optional<DropAction> fromString(String s){
            return switch(s){
                case "drop" -> Optional.of(DROP);
                case "keep" -> Optional.of(KEEP);
                case "destroy" -> Optional.of(DESTROY);
                case "none" -> Optional.of(NONE);
                default -> Optional.empty();
            };
        }
    }
}
