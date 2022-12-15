package me.atie.partialKeepinventory.mixin;

import me.atie.partialKeepinventory.config.pkiConfig;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin  {

    @Shadow @Final private PlayerInventory inventory;

    @Inject(method = "dropInventory()V", at = @At("HEAD"), cancellable = true)
    public void dropInventory(CallbackInfo ci){
        if (pkiConfig.enableMod) {
            this.inventory.dropAll();
            ci.cancel();
        }

    }
}
