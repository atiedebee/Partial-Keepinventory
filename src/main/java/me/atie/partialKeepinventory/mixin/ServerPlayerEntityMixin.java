package me.atie.partialKeepinventory.mixin;

import me.atie.partialKeepinventory.formula.XpDroprateFormula;
import me.atie.partialKeepinventory.partialKeepinventory;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import static me.atie.partialKeepinventory.partialKeepinventory.CONFIG_COMPONENT;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin {


    public void removeXpLevels(int levels) {
        ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;

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


    @Inject(method = "copyFrom(Lnet/minecraft/server/network/ServerPlayerEntity;Z)V",
            at = @At("TAIL"),
            locals = LocalCapture.CAPTURE_FAILHARD
    )
    public void keepXP(ServerPlayerEntity oldPlayer, boolean alive, CallbackInfo ci) {
        if( !alive && CONFIG_COMPONENT.isEnabled() ){

            if( CONFIG_COMPONENT.getKeepxpMode() != partialKeepinventory.KeepXPMode.VANILLA ) {
                switch (CONFIG_COMPONENT.getKeepxpMode()) {
                    case STATIC_LEVEL -> removeXpLevels(XpDroprateFormula.getLevelsToLoseStatic(oldPlayer));
                    case STATIC_POINTS -> ((ServerPlayerEntity) (Object) this).addExperience(-1 * XpDroprateFormula.getPointsToLoseStatic(oldPlayer));
                    default -> throw new IllegalStateException("Unexpected value: " + CONFIG_COMPONENT.getKeepxpMode());
                }
            }

            if( CONFIG_COMPONENT.partialKeepinvMode() != partialKeepinventory.KeepinvMode.VANILLA ) {
                ((ServerPlayerEntity)(Object)this).getInventory().clone(oldPlayer.getInventory());
            }

        }
    }

}
