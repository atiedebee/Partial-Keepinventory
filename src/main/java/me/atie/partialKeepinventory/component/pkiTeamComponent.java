package me.atie.partialKeepinventory.component;

import dev.onyxstudios.cca.api.v3.component.Component;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.network.ServerPlayerEntity;

public class pkiTeamComponent implements Component, AutoSyncedComponent {
    public final Team team;


    public pkiTeamComponent(Team team){
        this.team = team;
    }


    @Override
    public boolean shouldSyncWith(ServerPlayerEntity player) {
        return player.getScoreboardTeam() == this.team;
    }

    @Override
    public void readFromNbt(NbtCompound nbtCompound) {

    }

    @Override
    public void writeToNbt(NbtCompound nbtCompound) {

    }
}
