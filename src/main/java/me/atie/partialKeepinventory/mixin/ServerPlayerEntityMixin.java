package me.atie.partialKeepinventory.mixin;

import me.atie.partialKeepinventory.KeepXPMode;
import me.atie.partialKeepinventory.KeepinvMode;
import me.atie.partialKeepinventory.formula.XpDroprateFormula;
import me.atie.partialKeepinventory.util.ExperienceUtil;
import me.atie.partialKeepinventory.util.InventoryUtil;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import static me.atie.partialKeepinventory.PartialKeepInventory.CONFIG;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin {

    @Inject(method = "copyFrom(Lnet/minecraft/server/network/ServerPlayerEntity;Z)V",
            at = @At("TAIL"),
            locals = LocalCapture.CAPTURE_FAILHARD
    )
    public void keepXP(ServerPlayerEntity oldPlayer, boolean alive, CallbackInfo ci) {
        if( !alive && CONFIG.getEnableMod() ){


            if( CONFIG.getKeepxpMode() != KeepXPMode.VANILLA &&  ExperienceUtil.shouldDropExperience(oldPlayer)) {
                switch( CONFIG.getKeepxpMode() ) {
                    case STATIC_LEVELS -> ExperienceUtil.removeXpLevels(XpDroprateFormula.getLevelsToLoseStatic(oldPlayer), (ServerPlayerEntity) (Object)this);

                    case STATIC_POINTS -> ((ServerPlayerEntity) (Object) this).addExperience(-1 * XpDroprateFormula.getPointsToLoseStatic(oldPlayer));

                    default -> throw new IllegalStateException("Unexpected value: " + CONFIG.getKeepxpMode());
                }
            }

            if( CONFIG.getPartialKeepinvMode() != KeepinvMode.VANILLA && !InventoryUtil.shouldDropInventory(oldPlayer) ) {
                ((ServerPlayerEntity)(Object)this).getInventory().clone(oldPlayer.getInventory());
            }

        }
    }

}
