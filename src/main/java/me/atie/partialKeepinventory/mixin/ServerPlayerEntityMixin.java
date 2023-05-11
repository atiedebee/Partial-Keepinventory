package me.atie.partialKeepinventory.mixin;

import com.mojang.authlib.GameProfile;
import me.atie.partialKeepinventory.KeepXPMode;
import me.atie.partialKeepinventory.PartialKeepInventory;
import me.atie.partialKeepinventory.formula.XpDroprateFormula;
import me.atie.partialKeepinventory.settings.pkiVersion;
import me.atie.partialKeepinventory.util.ExperienceUtil;
import me.atie.partialKeepinventory.util.ServerPlayerClientVersion;
import me.atie.partialKeepinventory.util.getXpLoss;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static me.atie.partialKeepinventory.PartialKeepInventory.CONFIG;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity
        implements getXpLoss, ServerPlayerClientVersion
{
    private int xpDropAmount;

    private int xpLossAmount;
    private pkiVersion clientPKIVersion;


    public ServerPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile gameProfile) {
        super(world, pos, yaw, gameProfile);
    }

    @Shadow public abstract void addExperience(int experience);


    @Override
    public int getXpLossAmount(){
        return xpLossAmount;
    }

    @Override
    public int getXpToDrop() {
        if( xpDropAmount < 0 ) {
            return super.getXpToDrop();
        }
        return xpDropAmount;
    }

    @Override
    public pkiVersion getClientPKIVersion() {
        return this.clientPKIVersion;
    }

    @Override
    public void setClientPKIVersion(pkiVersion version) {
        this.clientPKIVersion = version;
    }

    public int levelsToDrop(int startLevel, int levels){
        final int temp_level = this.experienceLevel;
        int dropAmount = 0;

        this.experienceLevel = startLevel;
        for( int i = 0; i < levels; i++ ){
            dropAmount += this.getNextLevelExperience();
            this.experienceLevel += 1;
        }
        this.experienceLevel = temp_level;
        return dropAmount;
    }

    /**
     * sets the xpLossAmount and xpDropAmount variables for later use
     * @param mode keepxp mode
     */
    public void setXpDropAmounts(KeepXPMode mode){
        ServerPlayerEntity player = (ServerPlayerEntity)(Object) this;
        int experienceLevel = player.experienceLevel;
        int totalExperience = player.totalExperience;

        switch( mode )
        {
            case STATIC_LEVELS:
                xpLossAmount = ExperienceUtil.getLevelsToLoseStatic(player);
                final int xpLevelsDropped = ExperienceUtil.getLevelDropStatic(xpLossAmount);
                xpDropAmount = levelsToDrop(this.experienceLevel - xpLossAmount, xpLevelsDropped);
                break;

            case STATIC_POINTS:
                xpLossAmount = ExperienceUtil.getPointsToLoseStatic(player);
                xpDropAmount = ExperienceUtil.getPointsDropStatic(xpLossAmount);
                break;

            case CUSTOM_LEVELS:
            case CUSTOM_POINTS:
                XpDroprateFormula dropForm = new XpDroprateFormula(player);
                XpDroprateFormula lossForm = new XpDroprateFormula(player);
                try {
                    dropForm.compileExpression(CONFIG.getXpDropExpression().toString());
                    lossForm.compileExpression(CONFIG.getXpLossExpression().toString());
                }catch(Exception e){
                    // Send error string to player and server.
                    String errorStr = "Failed compiling drop expression: " + e.getMessage() + "\nResorting to static droprate of 0";
                    PartialKeepInventory.LOGGER.error(errorStr);
                    player.getCommandSource().sendFeedback(Text.literal(errorStr).setStyle(Style.EMPTY.withColor(Formatting.RED)), true);

                    xpLossAmount = 0;
                    xpDropAmount = 0;
                    return;
                }

                double dropPercentage = dropForm.getResult(experienceLevel, totalExperience);
                double lossPercentage = lossForm.getResult(experienceLevel, totalExperience);
                lossPercentage = Math.max(Math.min(1.0, lossPercentage), 0.0);
                dropPercentage = Math.max(Math.min(1.0, dropPercentage), 0.0);

                xpLossAmount = (int) (totalExperience * lossPercentage);
                final int xpDropLevels = (int) (xpLossAmount * dropPercentage);

                if( mode == KeepXPMode.CUSTOM_LEVELS ){
                    xpDropAmount = levelsToDrop(this.experienceLevel - xpLossAmount, xpDropLevels);
                }
                break;

            default:
                throw new IllegalStateException("Unexpected value: " + mode);
        }
    }


    @Inject(method = "onDeath", at = @At("HEAD"))
    public void prepareXpDroprates(CallbackInfo ci){
        if( CONFIG.getEnableMod() && CONFIG.getKeepxpMode() != KeepXPMode.VANILLA ) {
            ExperienceUtil.updateTotalExperience(this);
            setXpDropAmounts(CONFIG.getKeepxpMode());
        }else{
            xpLossAmount = -1;
            xpDropAmount = -1;
        }
    }

}
