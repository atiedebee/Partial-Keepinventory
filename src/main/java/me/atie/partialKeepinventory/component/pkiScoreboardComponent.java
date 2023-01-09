package me.atie.partialKeepinventory.component;

import dev.onyxstudios.cca.api.v3.component.Component;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import me.atie.partialKeepinventory.KeepXPMode;
import me.atie.partialKeepinventory.KeepinvMode;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.MinecraftServer;

import static me.atie.partialKeepinventory.PartialKeepInventory.SAVED_PLAYERS;

public class pkiScoreboardComponent extends pkiSettings implements Component, AutoSyncedComponent {

    public Scoreboard scoreboard;
    public MinecraftServer server;
    public Team savedPlayersTeam;


    ///////////////////////
    // Getters & Setters //
    ///////////////////////

    @Override
    public void setEnableMod(boolean enableMod) {
        super.setEnableMod(enableMod);
        pkiComponentList.configKey.sync(scoreboard);
    }


    @Override
    public void setPartialKeepinvMode(KeepinvMode partialKeepinvMode) {
        super.setPartialKeepinvMode(partialKeepinvMode);
        pkiComponentList.configKey.sync(scoreboard);
    }

    @Override
    public void setInventoryDroprate(int inventoryDroprate) {
        super.setInventoryDroprate(inventoryDroprate);
        pkiComponentList.configKey.sync(scoreboard);
    }

    @Override
    public void setCommonDroprate(int commonDroprate) {
        super.setCommonDroprate(commonDroprate);
        pkiComponentList.configKey.sync(scoreboard);
    }

    @Override
    public void setUncommonDroprate(int uncommonDroprate) {
        super.setUncommonDroprate(uncommonDroprate);
        pkiComponentList.configKey.sync(scoreboard);
    }

    @Override
    public void setRareDroprate(int rareDroprate) {
        super.setRareDroprate(rareDroprate);
        pkiComponentList.configKey.sync(scoreboard);
    }

    @Override
    public void setEpicDroprate(int epicDroprate) {
        super.setEpicDroprate(epicDroprate);
        pkiComponentList.configKey.sync(scoreboard);
    }


    @Override
    public void setExpression(String expression) {
        super.setExpression(expression);
        pkiComponentList.configKey.sync(scoreboard);
    }


    @Override
    public void setXpDrop(int xpDrop) {
        super.setXpDrop(xpDrop);
        pkiComponentList.configKey.sync(scoreboard);
    }

    @Override
    public void setXpLoss(int xpLoss) {
        super.setXpLoss(xpLoss);
        pkiComponentList.configKey.sync(scoreboard);
    }

    @Override
    public void setXpExpression(String xpExpression) {
        super.setXpExpression(xpExpression);
        pkiComponentList.configKey.sync(scoreboard);
    }

    @Override
    public void setKeepxpMode(KeepXPMode keepxpMode) {
        super.setKeepxpMode(keepxpMode);
        pkiComponentList.configKey.sync(scoreboard);
    }


    //////////////////////
    // Actual functions //
    //////////////////////

    public void sync() {
        pkiComponentList.configKey.sync(scoreboard);
    }


    @Override
    public void readFromNbt(NbtCompound nbt) {
        enableMod = nbt.getBoolean("enable");
        expression = new StringBuffer(nbt.getString("expression"));
        xpExpression = new StringBuffer(nbt.getString("xpExpression")); //not used yet

        keepinvMode = keepinvModeValues[ nbt.getInt("keepinvMode") ];
        keepxpMode = keepxpModeValues[ nbt.getInt("keepxpMode") ];

        inventoryDroprate = nbt.getInt("invDR");
        commonDroprate = nbt.getInt("commonDR");
        uncommonDroprate = nbt.getInt("uncommonDR");
        rareDroprate = nbt.getInt("rareDR");
        epicDroprate = nbt.getInt("epicDR");

        xpDrop = nbt.getInt("xpDrop");
        xpLoss = nbt.getInt("xpLoss");


    }

    @Override
    public void writeToNbt(NbtCompound nbt) {
        nbt.putBoolean("enable", enableMod);
        nbt.putString("expression", String.valueOf(expression));
        nbt.putString("xpExpression", String.valueOf(xpExpression));

        nbt.putInt("keepinvMode", keepinvMode.ordinal());
        nbt.putInt("keepxpMode", keepxpMode.ordinal());

        nbt.putInt("invDR", inventoryDroprate);
        nbt.putInt("commonDR", commonDroprate);
        nbt.putInt("uncommonDR", uncommonDroprate);
        nbt.putInt("rareDR", rareDroprate);
        nbt.putInt("epicDR", epicDroprate);

        nbt.putInt("xpDrop", xpDrop);
        nbt.putInt("xpLoss", xpLoss);
    }

    public pkiScoreboardComponent(Scoreboard scoreboard, MinecraftServer server){
        this.scoreboard = scoreboard;
        this.server = server;
    }

    public void update() {
//        this.scoreboard = PartialKeepInventory.server.getScoreboard();
        updateTeam();
    }

    private void updateTeam(){
        Team savedPlayersTeam = scoreboard.getTeam(pkiTeamComponent.teamName);
        if(savedPlayersTeam == null){
            savedPlayersTeam = scoreboard.addTeam(pkiTeamComponent.teamName);
        }
        SAVED_PLAYERS = pkiComponentList.savedPlayersKey.get(savedPlayersTeam);

        this.savedPlayersTeam = SAVED_PLAYERS.team;
    }

}
