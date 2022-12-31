package me.atie.partialKeepinventory.mixin;

import me.atie.partialKeepinventory.formula.XpDroprateFormula;
import me.atie.partialKeepinventory.partialKeepinventory;
import me.atie.partialKeepinventory.util.Experience;
import me.atie.partialKeepinventory.util.Inventory;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import static me.atie.partialKeepinventory.partialKeepinventory.CONFIG_COMPONENT;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin {





    @Inject(method = "copyFrom(Lnet/minecraft/server/network/ServerPlayerEntity;Z)V",
            at = @At("TAIL"),
            locals = LocalCapture.CAPTURE_FAILHARD
    )
    public void keepXP(ServerPlayerEntity oldPlayer, boolean alive, CallbackInfo ci) {
        if( !alive && CONFIG_COMPONENT.isEnabled() ){


            if( CONFIG_COMPONENT.getKeepxpMode() != partialKeepinventory.KeepXPMode.VANILLA &&  Experience.shouldDropExperience(oldPlayer)) {
                switch( CONFIG_COMPONENT.getKeepxpMode() ) {
                    case STATIC_LEVEL -> Experience.removeXpLevels(XpDroprateFormula.getLevelsToLoseStatic(oldPlayer), (ServerPlayerEntity) (Object)this);

                    case STATIC_POINTS -> ((ServerPlayerEntity) (Object) this).addExperience(-1 * XpDroprateFormula.getPointsToLoseStatic(oldPlayer));

                    default -> throw new IllegalStateException("Unexpected value: " + CONFIG_COMPONENT.getKeepxpMode());
                }
            }

            if( CONFIG_COMPONENT.partialKeepinvMode() != partialKeepinventory.KeepinvMode.VANILLA && !Inventory.shouldDropInventory(oldPlayer) ) {
                ((ServerPlayerEntity)(Object)this).getInventory().clone(oldPlayer.getInventory());
            }

        }
    }

}
