package me.atie.partialKeepinventory.mixin;

import me.atie.partialKeepinventory.partialKeepinventory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.world.GameRules;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static me.atie.partialKeepinventory.partialKeepinventory.CONFIG_COMPONENT;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin  {

    @Shadow @Final private PlayerInventory inventory;

    @Shadow public abstract boolean isCreative();

//    public int getXpToDrop() {
//        if (!this.world.getGameRules().getBoolean(GameRules.KEEP_INVENTORY) && !this.isSpectator()) {
//            int i = this.experienceLevel * 7;
//            return i > 100 ? 100 : i;
//        } else {
//            return 0;
//        }
//    }
    PlayerEntity player = (PlayerEntity)(Object)this;

    private int xpToDrop(int levels, int xpPoints){
        if (!player.world.getGameRules().getBoolean(GameRules.KEEP_INVENTORY) && !player.isSpectator()) {
            int i = player.experienceLevel * 7;
            return i > 100 ? 100 : i;
        } else {
            return 0;
        }
    }

    @Inject(method = "getXpToDrop()I", at = @At("HEAD"), cancellable = true)
    public void customXpDrop(CallbackInfoReturnable<Integer> cir) {
        if( !CONFIG_COMPONENT.isEnabled() || CONFIG_COMPONENT.getKeepxpMode() == partialKeepinventory.KeepXPMode.VANILLA){
            return;
        }
        int dropAmount;
//TODO: experience drop stuff

        dropAmount = xpToDrop(player.experienceLevel, player.totalExperience);

        cir.setReturnValue(dropAmount);
        cir.cancel();
    }

    @Inject(method = "dropInventory()V", at = @At("HEAD"), cancellable = true)
    public void dropInventory(CallbackInfo ci) {

        if( this.isCreative() && ((PlayerEntity)(Object)this).getWorld().getGameRules().getBoolean(partialKeepinventory.creativeKeepInventory) ) {
            ci.cancel();
            return;
        }

        if( !CONFIG_COMPONENT.isEnabled() ){
            return;
        }

        this.inventory.dropAll();
        ci.cancel();
    }
}
