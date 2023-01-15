package me.atie.partialKeepinventory.component;

import me.atie.partialKeepinventory.KeepXPMode;
import me.atie.partialKeepinventory.KeepinvMode;
import me.atie.partialKeepinventory.PartialKeepInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public class pkiSettings {


    public static Identifier updateServerConfig = new Identifier(PartialKeepInventory.getID(), "update-config");
    public static Identifier serverConfigUpdated = new Identifier(PartialKeepInventory.getID(), "config-updated");
    public static Identifier requestServerConfig = new Identifier(PartialKeepInventory.getID(), "config-request");
    public static Identifier sendServerConfig = new Identifier(PartialKeepInventory.getID(), "config-send");

    // ----- General -----
    protected boolean enableMod = true;
    protected KeepinvMode keepinvMode = KeepinvMode.STATIC;
    protected final KeepinvMode[] keepinvModeValues = KeepinvMode.values();


    // ----- Droprates -----
    protected int inventoryDroprate = 100;

    protected int commonDroprate = 100;

    protected int uncommonDroprate = 100;

    protected int rareDroprate = 100;

    protected int epicDroprate = 100;


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
    protected StringBuffer expression = new StringBuffer();

    // ----- XP -----
    protected KeepXPMode keepxpMode = KeepXPMode.VANILLA;
    protected final KeepXPMode[] keepxpModeValues = KeepXPMode.values();

    // How much of the XP that the player has is lost
    protected int xpLoss = 50;

    // How much of the XP that the player loses should be dropped
    protected int xpDrop = 50;

    protected StringBuffer xpExpression = new StringBuffer();

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

    public void setInventoryDroprate(int inventoryDroprate) {
        this.inventoryDroprate = inventoryDroprate;
    }

    public int getCommonDroprate() {
        return commonDroprate;
    }

    public void setCommonDroprate(int commonDroprate) {
        this.commonDroprate = commonDroprate;
    }

    public int getUncommonDroprate() {
        return uncommonDroprate;
    }

    public void setUncommonDroprate(int uncommonDroprate) {
        this.uncommonDroprate = uncommonDroprate;
    }

    public int getRareDroprate() {
        return rareDroprate;
    }

    public void setRareDroprate(int rareDroprate) {
        this.rareDroprate = rareDroprate;
    }

    public int getEpicDroprate() {
        return epicDroprate;
    }

    public void setEpicDroprate(int epicDroprate) {
        this.epicDroprate = epicDroprate;
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


    public int getXpDrop() {
        return xpDrop;
    }

    public void setXpDrop(int xpDrop) {
        this.xpDrop = xpDrop;
    }

    public int getXpLoss() {
        return xpLoss;
    }

    public void setXpLoss(int xpLoss) {
        this.xpLoss = xpLoss;
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
        buf.writeIntArray(new int[]{ inventoryDroprate, commonDroprate, uncommonDroprate, rareDroprate, epicDroprate,
                                    xpDrop, xpLoss} );
        buf.writeString(expression.toString());
        buf.writeEnumConstant(keepinvMode);
        buf.writeEnumConstant(keepxpMode);
    }

    public void packetReader(PacketByteBuf buf){
        enableMod = buf.readBoolean();
        final int[] droprates = buf.readIntArray();
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

}
