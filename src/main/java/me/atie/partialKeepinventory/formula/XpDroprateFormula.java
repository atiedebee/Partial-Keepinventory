package me.atie.partialKeepinventory.formula;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import redempt.crunch.Crunch;

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
    public double getResult(int xpAmount) {
        this.xpAmount = xpAmount;

        return cx.evaluate();
    }
}
