package me.atie.partialKeepinventory.component;

import dev.onyxstudios.cca.api.v3.component.Component;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static me.atie.partialKeepinventory.partialKeepinventory.*;

public class pkiComponent implements Component, AutoSyncedComponent {
    public static final ComponentKey<pkiComponent> configKey = ComponentRegistry.getOrCreate(
            new Identifier(getID(), "config"),
            pkiComponent.class);


    // ----- General -----
    private boolean enableMod = true;
    private KeepinvMode keepinvMode = KeepinvMode.PERCENTAGE;

    // ----- Droprates -----
    private int inventoryDroprate = 100;

    private int commonDroprate = 100;

    private int uncommonDroprate = 100;

    private int rareDroprate = 100;

    private int epicDroprate = 100;

    private List<UUID> perPlayerKeepinventory = new ArrayList<>();


//                    Custom expressions aren't checked on correctness yet. Please test them out in a separate world before adding them.\n
//                    Percentages are from 0.0 - 1.0
//                    Variables:
//                            - spawnDistance:                distance from player to spawnpoint
//                            - spawnX, spawnY, spawnZ:       spawn coordinates
//                            - playerX, playerY, playerZ:    player coordinates
//                            - rarityPercent:                get droprate from rarity as set in the config.
//                            - isEpic, isRare, isCommon, isUncommon:
//                                                            return 1.0 if true
//                            - dropPercent:                  inventory droprate as set in the config
    private String expression = new String();

    // ----- XP -----
    private KeepXPMode keepxpMode = KeepXPMode.VANILLA;
    private int xpLoss = 50;
    private int xpDrop = 50;


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

    public List<UUID> getPerPlayerKeepinventory() {
        return perPlayerKeepinventory;
    }

    public void setPerPlayerKeepinventory(List<UUID> perPlayerKeepinventory) {
        this.perPlayerKeepinventory = perPlayerKeepinventory;
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

        inventoryDroprate = nbt.getInt("invDR");
        commonDroprate = nbt.getInt("commonDR");
        uncommonDroprate = nbt.getInt("uncommonDR");
        rareDroprate = nbt.getInt("rareDR");
        epicDroprate = nbt.getInt("epicDR");


    }

    @Override
    public void writeToNbt(NbtCompound nbt) {

        nbt.putBoolean("enable", enableMod);
        nbt.putString("expression", expression);

        nbt.putInt("invDR", inventoryDroprate);
        nbt.putInt("commonDR", commonDroprate);
        nbt.putInt("uncommonDR", uncommonDroprate);
        nbt.putInt("rareDR", rareDroprate);
        nbt.putInt("epicDR", epicDroprate);
    }

    public pkiComponent(){
    }
}
