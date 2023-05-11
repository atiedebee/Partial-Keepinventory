package me.atie.partialKeepinventory.formula;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import redempt.crunch.CompiledExpression;
import redempt.crunch.Crunch;
import redempt.crunch.functional.EvaluationEnvironment;

import java.util.Objects;


public class DroprateFormula {
    protected ServerPlayerEntity player = null;

    final protected EvaluationEnvironment env;
    protected CompiledExpression cx = null;

    public DroprateFormula(ServerPlayerEntity player) {
        this.player = player;
        env = new EvaluationEnvironment();
    }
    public DroprateFormula() {
        env = new EvaluationEnvironment();
    }

    protected void initDummyEnvVariables(){
        env.addLazyVariable("spawnDistance", () -> 0.0);
        env.addLazyVariable("spawnX", () -> 0.0);
        env.addLazyVariable("spawnY", () -> 0.0);
        env.addLazyVariable("spawnZ", () -> 0.0);

        env.addLazyVariable("playerX", () -> 0.0);
        env.addLazyVariable( "playerY", () -> 0.0);
        env.addLazyVariable( "playerZ", () -> 0.0);

        env.addFunction("max", 2, (a) -> Math.max(a[0], a[1]));
        env.addFunction("min", 2, (a) -> Math.min(a[0], a[1]));
    }

    protected void initEnvVariables(){
        if( this.player == null ){
            throw new RuntimeException("Cannot create invExpression: no player was provided.");
        }

        env.addLazyVariable("spawnDistance", this::getSpawnDistance);
        env.addLazyVariable("spawnX", () -> Objects.requireNonNull(this.player.getSpawnPointPosition()).getX());
        env.addLazyVariable("spawnY", () -> Objects.requireNonNull(this.player.getSpawnPointPosition()).getY());
        env.addLazyVariable("spawnZ", () -> Objects.requireNonNull(this.player.getSpawnPointPosition()).getZ());

        env.addLazyVariable("playerX", this.player::getX);
        env.addLazyVariable( "playerY", this.player::getY);
        env.addLazyVariable( "playerZ", this.player::getZ);

        env.addFunction("max", 2, (a) -> Math.max(a[0], a[1]));
        env.addFunction("min", 2, (a) -> Math.min(a[0], a[1]));
    }

    public void compileExpression(String expression){
        initEnvVariables();
        cx = Crunch.compileExpression(expression, env);
    }

    public void testExpression(String expression){
        initDummyEnvVariables();
        cx = Crunch.compileExpression(expression, env);
    }

    protected BlockPos getPlayerSpawn(){
        BlockPos pos = this.player.getSpawnPointPosition();
        if (pos == null) {
            pos = this.player.getWorld().getSpawnPos();
        }
        return pos;
    }

    protected double getSpawnDistance() {
        Vec3d spawnPos = getPlayerSpawn().toCenterPos();
        Vec3d playerPos = this.player.getPos();

        return playerPos.distanceTo(spawnPos);
    }

    protected double getResult(){
        return Math.min(1.0, Math.max(cx.evaluate(), 0.0));
    }


}
