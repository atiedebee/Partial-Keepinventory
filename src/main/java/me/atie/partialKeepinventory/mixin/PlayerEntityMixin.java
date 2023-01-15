package me.atie.partialKeepinventory.mixin;

import me.atie.partialKeepinventory.KeepXPMode;
import me.atie.partialKeepinventory.PartialKeepInventory;
import me.atie.partialKeepinventory.formula.XpDroprateFormula;
import me.atie.partialKeepinventory.util.Experience;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static me.atie.partialKeepinventory.PartialKeepInventory.CONFIG_COMPONENT;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin  {

    @Shadow @Final private PlayerInventory inventory;

    @Shadow public abstract boolean isCreative();


    PlayerEntity player = (PlayerEntity)(Object)this;


    private void updateTotalExperience(){
        int temp = 0;
        int level = player.experienceLevel;

        for( player.experienceLevel = 0; player.experienceLevel < level; player.experienceLevel++ ){
            temp += player.getNextLevelExperience();
        }
        player.totalExperience = (int) (temp + (player.getNextLevelExperience() * player.experienceProgress));
    }


    @Inject(method = "getXpToDrop()I", at = @At("HEAD"), cancellable = true)
    public void customXpDrop(CallbackInfoReturnable<Integer> cir) {
        if( !CONFIG_COMPONENT.getEnableMod() || CONFIG_COMPONENT.getKeepxpMode() == KeepXPMode.VANILLA || !Experience.shouldDropExperience((ServerPlayerEntity) (Object)this)){
            return;
        }

        updateTotalExperience();

        int dropAmount = switch (CONFIG_COMPONENT.getKeepxpMode()) {
            case STATIC_LEVELS -> {
                int levels_lost = XpDroprateFormula.getLevelsToLoseStatic((ServerPlayerEntity) player);
                int levels_dropped = XpDroprateFormula.getLevelDropStatic(levels_lost);
                int xp_dropped = 0;

                int temp_level = player.experienceLevel;

                player.experienceLevel -= levels_lost;
                for( int i = 0; i < levels_dropped; i++ ){
                    xp_dropped += player.getNextLevelExperience();
                    player.experienceLevel += 1;
                }

                player.experienceLevel = temp_level;

                yield xp_dropped;
            }
            case STATIC_POINTS -> XpDroprateFormula.getPointsDropStatic((ServerPlayerEntity) player);


            default -> throw new IllegalStateException("Unexpected value: " + CONFIG_COMPONENT.getKeepxpMode());
        };

        cir.setReturnValue(dropAmount);
        cir.cancel();
    }

    @Inject(method = "dropInventory()V", at = @At("HEAD"), cancellable = true)
    public void dropInventory(CallbackInfo ci) {
        if( this.isCreative() && player.getWorld().getGameRules().getBoolean(PartialKeepInventory.creativeKeepInventory) ) {
            ci.cancel();
            return;
        }

        if( !CONFIG_COMPONENT.getEnableMod() ){
            return;
        }

        this.inventory.dropAll();
        ci.cancel();
    }
}
