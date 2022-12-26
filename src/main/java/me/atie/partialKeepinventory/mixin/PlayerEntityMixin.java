package me.atie.partialKeepinventory.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static me.atie.partialKeepinventory.partialKeepinventory.CONFIG;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin  {

    @Shadow @Final private PlayerInventory inventory;

    @Shadow public abstract boolean isCreative();

    @Shadow public abstract boolean isSpectator();

    @Inject(method = "dropInventory()V", at = @At("HEAD"), cancellable = true)
    public void dropInventory(CallbackInfo ci){
        if (CONFIG.enableMod() && !this.isCreative() && !this.isSpectator()) {
            this.inventory.dropAll();
            ci.cancel();
        }

    }
}
