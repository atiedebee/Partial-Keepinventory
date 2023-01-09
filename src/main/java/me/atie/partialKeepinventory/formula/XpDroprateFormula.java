package me.atie.partialKeepinventory.formula;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import redempt.crunch.Crunch;

import static me.atie.partialKeepinventory.PartialKeepInventory.CONFIG_COMPONENT;

public class XpDroprateFormula extends DroprateFormula {

    private int xpAmount;
    private int xpLevel;

    public XpDroprateFormula(ServerPlayerEntity player, String expression) {
        this.player = player;

        final BlockPos spawnPos = getPlayerSpawn();

        env.addLazyVariable("spawnDistance", this::getSpawnDistance);
        env.addLazyVariable("spawnX", spawnPos::getX);
        env.addLazyVariable("spawnY", spawnPos::getY);
        env.addLazyVariable("spawnZ", spawnPos::getZ);

        env.addLazyVariable("playerX", this.player::getX);
        env.addLazyVariable( "playerY", this.player::getY);
        env.addLazyVariable( "playerZ", this.player::getZ);

        env.addLazyVariable("xpPoints", () -> this.xpAmount);
        env.addLazyVariable("xpLevel", () -> this.xpLevel);

        env.addFunction("max", 2, (a) -> Math.max(a[0], a[1]));
        env.addFunction("min", 2, (a) -> Math.min(a[0], a[1]));

        cx = Crunch.compileExpression(expression, env);
    }
    public double getResult( int xpLevel, int xpAmount) {
        this.xpAmount = xpAmount;
        this.xpLevel = xpLevel;


        return cx.evaluate();
    }


    public static int getLevelDropStatic(int levelsLost) {
        int levelsDropped = (int) Math.round(CONFIG_COMPONENT.getXpDrop() * 0.01 * levelsLost);
        return levelsDropped;
    }

    public static int getLevelDropStatic(ServerPlayerEntity player) {
        int levelsLost = XpDroprateFormula.getLevelsToLoseStatic(player);
        int levelsDropped = (int) Math.round(CONFIG_COMPONENT.getXpDrop() * 0.01 * levelsLost);
        return levelsDropped;
    }

    public static int getLevelsToLoseStatic(ServerPlayerEntity player) {
        double lossPercent = CONFIG_COMPONENT.getXpLoss() * 0.01;// * 0.01 to get it to the range 0.0 - 1.0
        return (int) Math.round(player.experienceLevel * lossPercent);
    }



    public static int getPointsDropStatic(ServerPlayerEntity player) {
        int pointsLost = XpDroprateFormula.getPointsToLoseStatic(player);
        int pointsDropped = (int) Math.round(CONFIG_COMPONENT.getXpDrop() * 0.01 * pointsLost);
        return pointsDropped;
    }

    public static int getPointsToLoseStatic(ServerPlayerEntity player) {
        double lossPercent = CONFIG_COMPONENT.getXpLoss() * 0.01;// * 0.01 to get it to the range 0.0 - 1.0
        return (int) Math.round(player.totalExperience * lossPercent);
    }



}
