package me.atie.partialKeepinventory.impl.trinkets;

import me.atie.partialKeepinventory.settings.Settings;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;

public class TrinketsSettings implements Settings {
    private boolean overrideDropRule = true;
    private KeepTrinketMode mode = KeepTrinketMode.DEFAULT;
    private boolean overrideDropRate = true;

    private int staticDroprate;
    private int commonDroprate;
    private int uncommonDroprate;
    private int rareDroprate;
    private int epicDroprate;



    public boolean OverrideDropRule() {
        return overrideDropRule;
    }

    public void setOverrideDropRule(boolean overrideDropRule) {
        this.overrideDropRule = overrideDropRule;
    }

    public boolean OverrideDropRate() {
        return overrideDropRate;
    }

    public void setOverrideDropRate(boolean overrideDropRate) {
        this.overrideDropRate = overrideDropRate;
    }


    @Override
    public void packetWriter(PacketByteBuf buf) {
        buf.writeEnumConstant(mode);
        buf.writeBoolean(overrideDropRule);
        buf.writeBoolean(overrideDropRate);
    }

    @Override
    public void packetReader(PacketByteBuf buf) {
        mode = buf.readEnumConstant(KeepTrinketMode.class);
        overrideDropRule = buf.readBoolean();
        overrideDropRate = buf.readBoolean();
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        return null;
    }

    @Override
    public NbtCompound readNbt(NbtCompound nbt) {
        return null;
    }

    public KeepTrinketMode getMode() {
        return mode;
    }

    public void setMode(KeepTrinketMode mode) {
        this.mode = mode;
    }

    public int getStaticDroprate() {
        return staticDroprate;
    }

    public void setStaticDroprate(int staticDroprate) {
        this.staticDroprate = staticDroprate;
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
}

