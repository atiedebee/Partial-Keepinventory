package me.atie.partialKeepinventory.mixin;

import me.atie.partialKeepinventory.PartialKeepInventory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static me.atie.partialKeepinventory.PartialKeepInventory.CONFIG;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin  {

    @Shadow @Final private PlayerInventory inventory;

    @Shadow public abstract boolean isCreative();


    private final PlayerEntity player = (PlayerEntity)(Object)this;

    @Inject(method = "dropInventory()V", at = @At("HEAD"), cancellable = true)
    public void dropInventory(CallbackInfo ci) {
        if( this.isCreative() && player.getWorld().getGameRules().getBoolean(PartialKeepInventory.creativeKeepInventory) ) {
            ci.cancel();
            return;
        }

        if( !CONFIG.getEnableMod() ){
            return;
        }

        this.inventory.dropAll();
        ci.cancel();
    }
}
