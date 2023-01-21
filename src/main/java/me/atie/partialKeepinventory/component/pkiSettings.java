package me.atie.partialKeepinventory.component;

import me.atie.partialKeepinventory.KeepXPMode;
import me.atie.partialKeepinventory.KeepinvMode;
import me.atie.partialKeepinventory.PartialKeepInventory;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Objects;


@SuppressWarnings("unused")
public class pkiSettings extends PersistentState implements Cloneable {
    public static Identifier updateServerConfig = new Identifier(PartialKeepInventory.getID(), "update-config");
    public static Identifier requestServerConfig = new Identifier(PartialKeepInventory.getID(), "config-request");
    public static Identifier sendServerConfig = new Identifier(PartialKeepInventory.getID(), "config-send");


    // ----- General -----
    private boolean enableMod = true;
    private KeepinvMode keepinvMode = KeepinvMode.STATIC;
    private static final KeepinvMode[] keepinvModeValues = KeepinvMode.values();
    private ArrayList<String> savedPlayers = new ArrayList<>();
    private boolean dropShulkerContents = false;

    // ----- Droprates -----
    private byte inventoryDroprate = 100;

    private byte commonDroprate = 100;

    private byte uncommonDroprate = 100;

    private byte rareDroprate = 100;

    private byte epicDroprate = 100;


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
    private StringBuffer expression = new StringBuffer();

    // ----- XP -----
    private KeepXPMode keepxpMode = KeepXPMode.VANILLA;
    private static final KeepXPMode[] keepxpModeValues = KeepXPMode.values();

    // How much of the XP that the player has is lost
    private byte xpLoss = 50;

    // How much of the XP that the player loses should be dropped
    private byte xpDrop = 50;

    private StringBuffer xpExpression = new StringBuffer();

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
    public boolean getDropShulkerContents() {
        return dropShulkerContents;
    }
    public void setDropShulkerContents(boolean dropShulkerContents) {
        this.dropShulkerContents = dropShulkerContents;
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


    public StringBuffer getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        if( expression.length() > this.expression.capacity())
            this.expression.ensureCapacity(expression.length());

        this.expression.replace(0, this.expression.capacity(), expression);
    }
    public void setExpression(StringBuffer expression) {
        this.expression = expression;
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

    public StringBuffer getXpExpression() {
        return this.xpExpression;
    }

    public void setXpExpression(String xpExpression) {
        this.xpExpression.replace(0, this.xpExpression.capacity(), xpExpression);
    }

    public void setXpExpression(StringBuffer xpExpression) {
        this.xpExpression = new StringBuffer(xpExpression);
    }


    public KeepXPMode getKeepxpMode() {
        return keepxpMode;
    }

    public void setKeepxpMode(KeepXPMode keepxpMode) {
        this.keepxpMode = keepxpMode;
    }

    public void packetWriter(PacketByteBuf buf){
        buf.writeBoolean(enableMod);
        buf.writeByteArray(new byte[]{inventoryDroprate, commonDroprate, uncommonDroprate, rareDroprate,
                epicDroprate, xpDrop, xpLoss} );
        buf.writeString(expression.toString());
        buf.writeEnumConstant(keepinvMode);
        buf.writeEnumConstant(keepxpMode);
    }

    public void packetReader(PacketByteBuf buf){
        enableMod = buf.readBoolean();
        final byte[] droprates = buf.readByteArray();
        inventoryDroprate = droprates[0];
        commonDroprate = droprates[1];
        uncommonDroprate = droprates[2];
        rareDroprate = droprates[3];
        epicDroprate = droprates[4];
        xpDrop = droprates[5];
        xpLoss = droprates[6];
        setExpression(buf.readString());
        keepinvMode = buf.readEnumConstant(KeepinvMode.class);
        keepxpMode = buf.readEnumConstant(KeepXPMode.class);
    }


    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        nbt.putBoolean("enable", enableMod);
        nbt.putByte("invMode", (byte) keepinvMode.ordinal());
        nbt.putByte("invDR", inventoryDroprate);
        nbt.putByte("commonDR", commonDroprate);
        nbt.putByte("uncommonDR", uncommonDroprate);
        nbt.putByte("rareDR", rareDroprate);
        nbt.putByte("epicDR", epicDroprate);
        nbt.putString("invExpr", expression.toString());

        nbt.putByte("xpMode", (byte) keepxpMode.ordinal());
        nbt.putByte("xpDrop", xpDrop);
        nbt.putByte("xpLoss", xpLoss);

        NbtCompound savedPlayersNbt = new NbtCompound();
        for( var playerName: savedPlayers ){
            savedPlayersNbt.putBoolean(playerName, true); //boolean isn't really needed. I just need to store all saved player names
        }

        nbt.put("savedPlayers", savedPlayersNbt);

        return nbt;
    }

    public static pkiSettings createFromNbt(NbtCompound nbt) {

        pkiSettings state = new pkiSettings();
        state.enableMod = nbt.getBoolean("enable");
        state.keepinvMode = keepinvModeValues[nbt.getByte("invMode")];
        state.inventoryDroprate = nbt.getByte("invDR");
        state.commonDroprate = nbt.getByte("commonDR");
        state.uncommonDroprate = nbt.getByte("uncommonDR");
        state.rareDroprate = nbt.getByte("rareDR");
        state.epicDroprate = nbt.getByte("epicDR");
        state.expression = new StringBuffer(nbt.getString("invExpr"));

        state.keepxpMode = keepxpModeValues[nbt.getByte("xpMode")];
        state.xpDrop = nbt.getByte("xpDrop");
        state.xpLoss = nbt.getByte("xpLoss");

        NbtCompound playerNamesNbt = nbt.getCompound("savedPlayers");
        state.savedPlayers = new ArrayList<>();
        state.savedPlayers.addAll(playerNamesNbt.getKeys());


        return state;
    }


    public static pkiSettings getServerState(MinecraftServer server) {
        PersistentStateManager persistentStateManager = Objects.requireNonNull(server.getWorld(World.OVERWORLD)).getPersistentStateManager();

        pkiSettings state = persistentStateManager.getOrCreate(
                pkiSettings::createFromNbt,
                pkiSettings::new,
                PartialKeepInventory.getID());

        state.markDirty();
        return state;
    }

    @Override
    public pkiSettings clone() {
        try {
            pkiSettings clone = (pkiSettings) super.clone();
            clone.expression = new StringBuffer(this.expression);
            clone.xpExpression = new StringBuffer(this.xpExpression);
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    @Environment(EnvType.CLIENT)
    public static void updateServerConfig(pkiSettings settings) {
        if(MinecraftClient.getInstance().getServer() == null) {
            PacketByteBuf buf = PacketByteBufs.create();
            settings.packetWriter(buf);

            ClientPlayNetworking.send(updateServerConfig, buf);
        }
    }
}
