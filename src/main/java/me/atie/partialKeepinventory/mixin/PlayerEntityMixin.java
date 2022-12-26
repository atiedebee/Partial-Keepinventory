package me.atie.partialKeepinventory.mixin;

import me.atie.partialKeepinventory.partialKeepinventory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static me.atie.partialKeepinventory.partialKeepinventory.CONFIG_COMPONENT;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin  {

    @Shadow @Final private PlayerInventory inventory;

    @Shadow public abstract boolean isCreative();

    private boolean shouldDropInventory() {
        return !(this.isCreative() && ((PlayerEntity)(Object)this).getWorld().getGameRules().getBoolean(partialKeepinventory.creativeKeepInventory));
    }

    @Inject(method = "dropInventory()V", at = @At("HEAD"), cancellable = true)
    public void dropInventory(CallbackInfo ci){
        if( CONFIG_COMPONENT.isEnabled() ){

            if( shouldDropInventory() ) {
                this.inventory.dropAll();
            }
            ci.cancel();
        }

    }
}
