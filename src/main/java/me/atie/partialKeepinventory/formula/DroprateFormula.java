package me.atie.partialKeepinventory.formula;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import redempt.crunch.CompiledExpression;
import redempt.crunch.functional.EvaluationEnvironment;


public class DroprateFormula {
    protected final ServerPlayerEntity player;

    protected EvaluationEnvironment env = null;
    protected CompiledExpression cx = null;

    public DroprateFormula(ServerPlayerEntity player) {
        this.player = player;
        env = new EvaluationEnvironment();
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
