package me.atie.partialKeepinventory.settings;

import me.atie.partialKeepinventory.KeepXPMode;
import me.atie.partialKeepinventory.KeepinvMode;
import me.atie.partialKeepinventory.PartialKeepInventory;
import me.atie.partialKeepinventory.api.pkiSettingsApi;
import me.atie.partialKeepinventory.formula.InventoryDroprateFormula;
import me.atie.partialKeepinventory.formula.XpDroprateFormula;
import me.atie.partialKeepinventory.impl.Impl;
import me.atie.partialKeepinventory.network.Identifiers;
import me.atie.partialKeepinventory.network.ServerListeners;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@SuppressWarnings("unused")
public class pkiSettings extends PersistentState implements pkiSettingsApi {

    // ----- Compatibility related -----
    public pkiVersion configVersion = null;
    public boolean validSettings = false;

    // ----- Implementation Settings -----
    protected List<pkiSettingsApi> implementationSettings;


    // ----- General -----
    protected boolean enableMod = true;
    protected KeepinvMode keepinvMode = KeepinvMode.STATIC;
    protected static final KeepinvMode[] keepinvModeValues = KeepinvMode.values();
    protected ArrayList<String> savedPlayers = new ArrayList<>();

    // ----- Droprates -----
    protected byte inventoryDroprate = 100;

    protected byte commonDroprate = 100;

    protected byte uncommonDroprate = 100;

    protected byte rareDroprate = 100;

    protected byte epicDroprate = 100;


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
    protected StringBuffer invExpression = new StringBuffer();

    // ----- XP -----
    protected KeepXPMode keepxpMode = KeepXPMode.VANILLA;
    protected static final KeepXPMode[] keepxpModeValues = KeepXPMode.values();

    // How much of the XP that the player has is lost
    protected byte xpLoss = 50;

    // How much of the XP that the player loses should be dropped
    protected byte xpDrop = 50;

    protected StringBuffer xpDropExpression = new StringBuffer();
    protected StringBuffer xpLossExpression = new StringBuffer();


    ///////////////////////
    // Getters & Setters //
    ///////////////////////

    public boolean getEnableMod() {
        return enableMod;
    }

    public void setEnableMod(boolean enableMod) {
        this.enableMod = enableMod;
    }

    public KeepinvMode getPartialKeepinvMode() {
        return keepinvMode;
    }

    public void setPartialKeepinvMode(KeepinvMode partialKeepinvMode) {
        this.keepinvMode = partialKeepinvMode;
    }

    public int getInventoryDroprate() {
        return inventoryDroprate;
    }

    public void setInventoryDroprate(Integer inventoryDroprate) {
        this.inventoryDroprate = inventoryDroprate.byteValue();
    }

    public int getCommonDroprate() {
        return commonDroprate;
    }

    public void setCommonDroprate(Integer commonDroprate) {
        this.commonDroprate = commonDroprate.byteValue();
    }

    public int getUncommonDroprate() {
        return uncommonDroprate;
    }

    public void setUncommonDroprate(Integer uncommonDroprate) {
        this.uncommonDroprate = uncommonDroprate.byteValue();
    }

    public int getRareDroprate() {
        return rareDroprate;
    }

    public void setRareDroprate(Integer rareDroprate) {
        this.rareDroprate = rareDroprate.byteValue();
    }

    public int getEpicDroprate() {
        return epicDroprate;
    }

    public void setEpicDroprate(Integer epicDroprate) {
        this.epicDroprate = epicDroprate.byteValue();
    }


    public StringBuffer getInvExpression() {
        return invExpression;
    }

    public void setInvExpression(String invExpression) {
        if( invExpression.length() > this.invExpression.capacity())
            this.invExpression.ensureCapacity(invExpression.length());

        this.invExpression.replace(0, this.invExpression.capacity(), invExpression);
    }
    public void setExpression(StringBuffer expression) {
        this.invExpression = expression;
    }

    public ArrayList<String> getSavedPlayers(){
        return savedPlayers;
    }

    public void addSavedPlayer(String player) throws RuntimeException {
        if( savedPlayers.contains(player) ){
            throw new RuntimeException("Player is already in list");
        }
        savedPlayers.add(player);
    }

    public void removeSavedPlayer(String player) throws RuntimeException {
        if( !savedPlayers.contains(player) ){
            throw new RuntimeException("Player not in list");
        }
        savedPlayers.remove(player);
    }


    public int getXpDrop() {
        return xpDrop;
    }

    public void setXpDrop(Integer xpDrop) {
        this.xpDrop = xpDrop.byteValue();
    }

    public int getXpLoss() {
        return xpLoss;
    }

    public void setXpLoss(Integer xpLoss) {
        this.xpLoss = xpLoss.byteValue();
    }

    public StringBuffer getXpDropExpression() {
        return this.xpDropExpression;
    }

    public void setXpDropExpression(String xpDropExpression) {
        this.xpDropExpression.ensureCapacity(xpDropExpression.length());
        this.xpDropExpression.replace(0, this.xpDropExpression.capacity(), xpDropExpression);
    }

    public void setXpDropExpression(StringBuffer xpExpression) {
        this.xpDropExpression = new StringBuffer(xpExpression);
    }


    public StringBuffer getXpLossExpression() {
        return this.xpLossExpression;
    }

    public void setXpLossExpression(String xpLossExpression) {
        this.xpLossExpression.ensureCapacity(xpLossExpression.length());
        this.xpLossExpression.replace(0, this.xpLossExpression.capacity(), xpLossExpression);
    }

    public void setXpLossExpression(StringBuffer xpLossExpression) {
        this.xpLossExpression = new StringBuffer(xpLossExpression);
    }

    public KeepXPMode getKeepxpMode() {
        return keepxpMode;
    }

    public void setKeepxpMode(KeepXPMode keepxpMode) {
        this.keepxpMode = keepxpMode;
    }


    public void validate() throws Exception {
        if( !validSettings ){
            throw new Exception("Settings identified themselves as invalid.");
        }

        var xpCheck = new XpDroprateFormula(null);
        xpCheck.testExpression(this.xpLossExpression.toString());
        xpCheck.testExpression(this.xpDropExpression.toString());

        var invCheck = new InventoryDroprateFormula(null);
        invCheck.testExpression(this.invExpression.toString());
    }


    ///////////////////
    // Serialization //
    ///////////////////

    @Override
    public void packetWriter(PacketByteBuf buf){
        BwSettingsCompat.writePacket(this, PartialKeepInventory.modVersion, buf);
    }


    public void packetReader(PacketByteBuf buf) {
        pkiVersion hostVersion = PartialKeepInventory.modVersion;
        pkiVersion requestVersion = new pkiVersion( buf );

        if( hostVersion.major < requestVersion.major || hostVersion.minor < requestVersion.minor ){
            PartialKeepInventory.LOGGER.warn("Settings obtained from are from a likely incompatible version. Server: \"" + requestVersion +  "\" Client:" + hostVersion);
            validSettings = false; // May do something with this later on
        }
        else{
            PartialKeepInventory.LOGGER.info("Received settings of version: " + requestVersion + ", host version: " + hostVersion);
            validSettings = true;
            BwSettingsCompat.readPacket(this, requestVersion, buf);
        }

    }

    @Override
    public NbtCompound readNbt(NbtCompound nbt) {
        pkiVersion v = new pkiVersion(nbt);
        this.configVersion = v;
        return BwSettingsCompat.readNbt(this, v, nbt);
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {

        PartialKeepInventory.modVersion.writeNbt(nbt);
        nbt.putBoolean("enable", enableMod);
        nbt.putByte("invMode", (byte) keepinvMode.ordinal());
        nbt.putByte("invDR", inventoryDroprate);
        nbt.putByte("commonDR", commonDroprate);
        nbt.putByte("uncommonDR", uncommonDroprate);
        nbt.putByte("rareDR", rareDroprate);
        nbt.putByte("epicDR", epicDroprate);
        nbt.putString("invExpr", invExpression.toString());

        nbt.putByte("xpMode", (byte) keepxpMode.ordinal());
        nbt.putByte("xpDrop", xpDrop);
        nbt.putByte("xpLoss", xpLoss);
        nbt.putString("xpDropExpr", xpDropExpression.toString());
        nbt.putString("xpLossExpr", xpLossExpression.toString());

        NbtCompound savedPlayersNbt = new NbtCompound();
        for( var playerName: savedPlayers ){
            savedPlayersNbt.putBoolean(playerName, true); //boolean isn't really needed. I just need to store all saved player names
        }
        nbt.put("savedPlayers", savedPlayersNbt);

        for( var setting: implementationSettings ){
            NbtCompound settingNbt = new NbtCompound();

            setting.writeNbt(settingNbt);

            nbt.put(setting.getModId(), settingNbt);
        }

        return nbt;
    }

    public static pkiSettings createFromNbt(NbtCompound nbt) {
        pkiSettings state = new pkiSettings();
        state.readNbt(nbt);
        return state;
    }


    public static pkiSettings getServerState(MinecraftServer server) {
        PersistentStateManager persistentStateManager = Objects.requireNonNull(server.getWorld(World.OVERWORLD)).getPersistentStateManager();

        pkiSettings state = persistentStateManager.getOrCreate(
                pkiSettings::createFromNbt,
                pkiSettings::new,
                PartialKeepInventory.getID());
        state.configVersion = PartialKeepInventory.modVersion;
        state.validSettings = true;
        state.markDirty();
        return state;
    }

    @Override
    public pkiSettings clone() {
        try {
            pkiSettings clone = (pkiSettings) super.clone();
            clone.invExpression = new StringBuffer(this.invExpression);
            clone.xpDropExpression = new StringBuffer(this.xpDropExpression);
            clone.xpLossExpression = new StringBuffer(this.xpLossExpression);

//            Not used when in singleplayer
            if( this.configVersion != null ){
                clone.configVersion = this.configVersion.clone();
            }

            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    @Override
    public String getModId() {
        return PartialKeepInventory.getID();
    }

    public static void updateServerConfig() {
        if( PartialKeepInventory.environment == EnvType.CLIENT && MinecraftClient.getInstance().getServer() == null ) { // Player is connected to server
            PacketByteBuf buf = PacketByteBufs.create();
            PartialKeepInventory.CONFIG.packetWriter(buf);

            ClientPlayNetworking.send(Identifiers.configUpdatePacket, buf);
        }
        else if( PartialKeepInventory.environment == EnvType.SERVER ){
            ServerListeners.sendConfigToPlayers(PartialKeepInventory.CONFIG);
        }

    }

    public pkiSettings() {
        implementationSettings = Impl.settings;
    }
}
