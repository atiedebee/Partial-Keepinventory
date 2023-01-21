package me.atie.partialKeepinventory.util;

import net.minecraft.server.network.ServerPlayerEntity;

import static me.atie.partialKeepinventory.PartialKeepInventory.CONFIG;

public class ExperienceUtil {

    public static boolean shouldDropExperience(ServerPlayerEntity player){
        return !CONFIG.getSavedPlayers().contains(player.getEntityName());
    }

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
}
