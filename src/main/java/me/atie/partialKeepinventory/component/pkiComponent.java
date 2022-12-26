package me.atie.partialKeepinventory.component;

import dev.onyxstudios.cca.api.v3.component.Component;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import me.atie.partialKeepinventory.partialKeepinventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class pkiComponent implements Component, AutoSyncedComponent {
    public static final ComponentKey<pkiComponent> configKey = ComponentRegistry.getOrCreate(
            new Identifier(partialKeepinventory.getID(), "config"),
            pkiComponent.class);

    // ----- General -----
    private boolean enableMod = true;
    private partialKeepinventory.KeepinvMode partialKeepinvMode = partialKeepinventory.KeepinvMode.PERCENTAGE;

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


    public boolean isEnabled() {
        return enableMod;
    }

    public void enableMod(boolean enableMod) {
        this.enableMod = enableMod;
        configKey.sync(partialKeepinventory.keyProvider);
    }

    public partialKeepinventory.KeepinvMode partialKeepinvMode() {
        return partialKeepinvMode;
    }

    public void partialKeepinvMode(partialKeepinventory.KeepinvMode partialKeepinvMode) {
        this.partialKeepinvMode = partialKeepinvMode;
        configKey.sync(partialKeepinventory.keyProvider);
    }

    public int inventoryDroprate() {
        return inventoryDroprate;
    }

    public void inventoryDroprate(int inventoryDroprate) {
        this.inventoryDroprate = inventoryDroprate;
        configKey.sync(partialKeepinventory.keyProvider);
    }

    public int getCommonDroprate() {
        return commonDroprate;
    }

    public void setCommonDroprate(int commonDroprate) {
        this.commonDroprate = commonDroprate;
        configKey.sync(partialKeepinventory.keyProvider);
    }

    public int getUncommonDroprate() {
        return uncommonDroprate;
    }

    public void setUncommonDroprate(int uncommonDroprate) {
        this.uncommonDroprate = uncommonDroprate;
        configKey.sync(partialKeepinventory.keyProvider);
    }

    public int getRareDroprate() {
        return rareDroprate;
    }

    public void setRareDroprate(int rareDroprate) {
        this.rareDroprate = rareDroprate;
        configKey.sync(partialKeepinventory.keyProvider);
    }

    public int getEpicDroprate() {
        return epicDroprate;
    }

    public void setEpicDroprate(int epicDroprate) {
        this.epicDroprate = epicDroprate;
        configKey.sync(partialKeepinventory.keyProvider);
    }

    public List<UUID> getPerPlayerKeepinventory() {
        return perPlayerKeepinventory;
    }

    public void setPerPlayerKeepinventory(List<UUID> perPlayerKeepinventory) {
        this.perPlayerKeepinventory = perPlayerKeepinventory;
        configKey.sync(partialKeepinventory.keyProvider);
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
        configKey.sync(partialKeepinventory.keyProvider);
    }

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
