package me.atie.partialKeepinventory.formula;

import me.atie.partialKeepinventory.PartialKeepInventory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import redempt.crunch.Crunch;
import redempt.crunch.functional.EvaluationEnvironment;

public class XpDroprateFormula extends DroprateFormula {

    private int xpAmount;
    private int xpLevel;

    public XpDroprateFormula(ServerPlayerEntity player, String expression) {
        super(player);

        PartialKeepInventory.LOGGER.info("Getting player spawn");
        final BlockPos spawnPos = getPlayerSpawn();

        env.addLazyVariable("spawnDistance", this::getSpawnDistance);
        env.addLazyVariable("spawnX", spawnPos::getX);
        env.addLazyVariable("spawnY", spawnPos::getY);
        env.addLazyVariable("spawnZ", spawnPos::getZ);

        env.addLazyVariable("playerX", this.player::getX);
        env.addLazyVariable( "playerY", this.player::getY);
        env.addLazyVariable( "playerZ", this.player::getZ);

        env.addLazyVariable("xpPoints", this::getXpAmount);
        env.addLazyVariable("xpLevel", this::getXpLevel);

        env.addFunction("max", 2, (a) -> Math.max(a[0], a[1]));
        env.addFunction("min", 2, (a) -> Math.min(a[0], a[1]));

        PartialKeepInventory.LOGGER.info("Compiling");

        cx = Crunch.compileExpression(expression, env);
    }

    public double getResult( int xpLevel, int xpAmount) {
        this.xpAmount = xpAmount;
        this.xpLevel = xpLevel;

        return super.getResult();
    }

    int getXpAmount(){
        return xpAmount;
    }
    int getXpLevel(){
        return xpLevel;
    }



}
