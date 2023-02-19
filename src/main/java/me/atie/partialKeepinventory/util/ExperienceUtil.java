package me.atie.partialKeepinventory.util;

import me.atie.partialKeepinventory.KeepXPMode;
import me.atie.partialKeepinventory.PartialKeepInventory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;

import static me.atie.partialKeepinventory.PartialKeepInventory.CONFIG;

public class ExperienceUtil {

    public static boolean shouldDropExperience(PlayerEntity player){
        return !CONFIG.getSavedPlayers().contains(player.getEntityName());

    }

    /**
     * @param levels amount of levels that should be removed
     * @param player player entity
     */
    public static void removeXpLevels(int levels, ServerPlayerEntity player) {

        if( levels <= 0 )
            return;

        player.totalExperience -= ((float)player.getNextLevelExperience() * player.experienceProgress);

        while( levels > 0 ){
            levels--;
            player.experienceLevel--;
            player.totalExperience -= player.getNextLevelExperience();
        }

        if( player.totalExperience < 0){
            player.totalExperience = 0;
            player.experienceLevel = 0;
            player.experienceProgress = 0.0F;
        }

    }
    public static int getLevelDropStatic(int levelsLost) {
        int levelsDropped = (int) Math.round(CONFIG.getXpDrop() * 0.01 * levelsLost);
        return levelsDropped;
    }

    public static int getLevelDropStatic(PlayerEntity player) {
        int levelsLost = getLevelsToLoseStatic(player);
        int levelsDropped = (int) Math.round(CONFIG.getXpDrop() * 0.01 * levelsLost);
        return levelsDropped;
    }

    public static int getLevelsToLoseStatic(PlayerEntity player) {
        double lossPercent = CONFIG.getXpLoss() * 0.01;// * 0.01 to get it to the range 0.0 - 1.0
        return (int) Math.round(player.experienceLevel * lossPercent);
    }



    public static int getPointsDropStatic(int pointsLost) {
        int pointsDropped = (int) Math.round(CONFIG.getXpDrop() * 0.01 * pointsLost);
        return pointsDropped;
    }


    public static int getPointsDropStatic(PlayerEntity player) {
        int pointsLost = getPointsToLoseStatic(player);
        int pointsDropped = (int) Math.round(CONFIG.getXpDrop() * 0.01 * pointsLost);
        return pointsDropped;
    }

    public static int getPointsToLoseStatic(PlayerEntity player) {
        double lossPercent = CONFIG.getXpLoss() * 0.01;// * 0.01 to get it to the range 0.0 - 1.0
        return (int) Math.round(player.totalExperience * lossPercent);
    }


    public static void updateTotalExperience(PlayerEntity player){
        int temp = 0;
        int level = player.experienceLevel;

        for( player.experienceLevel = 0; player.experienceLevel < level; player.experienceLevel++ ){
            temp += player.getNextLevelExperience();
        }
        player.totalExperience = (int) (temp + (player.getNextLevelExperience() * player.experienceProgress));
        PartialKeepInventory.LOGGER.info("Player total experience is equal to: " + player.totalExperience);
    }

    public static void copyNewXpAmount(ServerPlayerEntity oldPlayer, ServerPlayerEntity newPlayer, boolean alive){
        PartialKeepInventory.LOGGER.info("-- CopyFrom event --");
        if( !alive && CONFIG.getEnableMod() ){
            KeepXPMode xpMode = CONFIG.getKeepxpMode();

            if( xpMode != KeepXPMode.VANILLA &&  ExperienceUtil.shouldDropExperience(oldPlayer) ) {

                int xpLossAmount = ((getXpLoss)oldPlayer).getXpLossAmount();

                switch(xpMode)
                {
                    case CUSTOM_LEVELS, STATIC_LEVELS:
                        PartialKeepInventory.LOGGER.info("Removing XP levels: " + xpLossAmount);
                        newPlayer.experienceLevel = oldPlayer.experienceLevel;
                        ExperienceUtil.removeXpLevels(xpLossAmount, newPlayer);
                        break;
                    case CUSTOM_POINTS, STATIC_POINTS:
                        PartialKeepInventory.LOGGER.info("Removing XP points: " + xpLossAmount);
                        newPlayer.experienceLevel = oldPlayer.experienceLevel;
                        newPlayer.totalExperience = oldPlayer.totalExperience;
                        newPlayer.addExperience(-1 * xpLossAmount);
                        break;
                }
            }
        }
    }


}
