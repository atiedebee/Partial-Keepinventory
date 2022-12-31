package me.atie.partialKeepinventory.component;

import dev.onyxstudios.cca.api.v3.component.Component;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import me.atie.partialKeepinventory.partialKeepinventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;

import static me.atie.partialKeepinventory.partialKeepinventory.*;

public class pkiScoreboardComponent implements Component, AutoSyncedComponent {
    private static final ComponentKey<pkiScoreboardComponent> configKey = ComponentRegistry.getOrCreate(
            new Identifier(getID(), "config"),
            pkiScoreboardComponent.class);

    private static final ComponentKey<pkiTeamComponent> savedPlayersKey = ComponentRegistry.getOrCreate(
            new Identifier(partialKeepinventory.getID(), "saved-players"),
            pkiTeamComponent.class
    );


    public Scoreboard scoreboard;
    public MinecraftServer server;
    public Team savedPlayersTeam;

    // ----- General -----
    private boolean enableMod = true;
    private KeepinvMode keepinvMode = KeepinvMode.STATIC;
    private final KeepinvMode[] keepinvModeValues = KeepinvMode.values();


    // ----- Droprates -----
    private int inventoryDroprate = 100;

    private int commonDroprate = 100;

    private int uncommonDroprate = 100;

    private int rareDroprate = 100;

    private int epicDroprate = 100;


//    Custom expressions aren't checked on correctness yet. Please test them out in a separate world before adding them.
//    Percentages are from 0.0 - 1.0
//    Variables:
//            - spawnDistance:                distance from player to spawnpoint
//            - spawnX, spawnY, spawnZ:       spawn coordinates
//            - playerX, playerY, playerZ:    player coordinates
//            - rarityPercent:                get droprate from rarity as set in the config.
//            - isEpic, isRare, isCommon, isUncommon:
//                                            return 1.0 if true
//            - dropPercent:                  inventory droprate as set in the config
    private String expression = new String();

    // ----- XP -----
    private KeepXPMode keepxpMode = KeepXPMode.VANILLA;
    private final KeepXPMode[] keepxpModeValues = KeepXPMode.values();

    // How much of the XP that the player has is lost
    private int xpLoss = 50;

    // How much of the XP that the player loses should be dropped
    private int xpDrop = 50;

    private String xpExpression = new String();


    ///////////////////////
    // Getters & Setters //
    ///////////////////////

    public boolean isEnabled() {
        return enableMod;
    }

    public void enableMod(boolean enableMod) {
        this.enableMod = enableMod;
        configKey.sync(keyProvider);
    }

    public KeepinvMode partialKeepinvMode() {
        return keepinvMode;
    }

    public void partialKeepinvMode(KeepinvMode partialKeepinvMode) {
        this.keepinvMode = partialKeepinvMode;
        configKey.sync(keyProvider);
    }

    public int inventoryDroprate() {
        return inventoryDroprate;
    }

    public void inventoryDroprate(int inventoryDroprate) {
        this.inventoryDroprate = inventoryDroprate;
        configKey.sync(keyProvider);
    }

    public int getCommonDroprate() {
        return commonDroprate;
    }

    public void setCommonDroprate(int commonDroprate) {
        this.commonDroprate = commonDroprate;
        configKey.sync(keyProvider);
    }

    public int getUncommonDroprate() {
        return uncommonDroprate;
    }

    public void setUncommonDroprate(int uncommonDroprate) {
        this.uncommonDroprate = uncommonDroprate;
        configKey.sync(keyProvider);
    }

    public int getRareDroprate() {
        return rareDroprate;
    }

    public void setRareDroprate(int rareDroprate) {
        this.rareDroprate = rareDroprate;
        configKey.sync(keyProvider);
    }

    public int getEpicDroprate() {
        return epicDroprate;
    }

    public void setEpicDroprate(int epicDroprate) {
        this.epicDroprate = epicDroprate;
        configKey.sync(keyProvider);
    }


    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
        configKey.sync(keyProvider);
    }


    public int getXpDrop() {
        return xpDrop;
    }

    public void setXpDrop(int xpDrop) {
        this.xpDrop = xpDrop;
        configKey.sync(keyProvider);
    }

    public int getXpLoss() {
        return xpLoss;
    }

    public void setXpLoss(int xpLoss) {
        this.xpLoss = xpLoss;
        configKey.sync(keyProvider);
    }

    public String getXpExpression() {
        return this.xpExpression;
    }

    public void setXpExpression(String xpExpression) {
        this.xpExpression = xpExpression;
        configKey.sync(keyProvider);
    }

    public KeepXPMode getKeepxpMode() {
        return keepxpMode;
    }

    public void setKeepxpMode(KeepXPMode keepxpMode) {
        this.keepxpMode = keepxpMode;
        configKey.sync(keyProvider);
    }


    //////////////////////
    // Actual functions //
    //////////////////////


    @Override
    public void readFromNbt(NbtCompound nbt) {
        enableMod = nbt.getBoolean("enable");
        expression = nbt.getString("expression");
        xpExpression = nbt.getString("xpExpression"); //not used yet

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
        nbt.putString("expression", expression);
        nbt.putString("xpExpression", xpExpression);

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

        partialKeepinventory.LOGGER.info(scoreboard.toString());

    }

    public void updateTeam(){
        Team savedPlayersKeyProvider = server.getScoreboard().getPlayerTeam("pkiSAVED_PLAYERS");
        if(savedPlayersKeyProvider == null){
            savedPlayersKeyProvider = server.getScoreboard().addTeam("pkiSAVED_PLAYERS");
        }
        SAVED_PLAYERS = CONFIG_COMPONENT.savedPlayersKey.get(savedPlayersKeyProvider);

        this.savedPlayersTeam = SAVED_PLAYERS.team;
    }

}
