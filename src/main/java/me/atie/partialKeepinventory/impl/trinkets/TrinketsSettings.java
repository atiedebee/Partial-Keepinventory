package me.atie.partialKeepinventory.impl.trinkets;

import me.atie.partialKeepinventory.api.pkiSettingsApi;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;

public class TrinketsSettings implements pkiSettingsApi {
    private boolean overrideDropRule = false;
    private boolean overrideDropRate = false;
    private KeepTrinketMode mode = KeepTrinketMode.DEFAULT;
    private static final KeepTrinketMode[] KeepTrinketModeValues = KeepTrinketMode.values();

    private int staticDroprate = 100;
    private int commonDroprate = 100;
    private int uncommonDroprate = 100;
    private int rareDroprate = 100;
    private int epicDroprate = 100;



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

    @Override
    public void packetWriter(PacketByteBuf buf) {
        buf.writeEnumConstant(mode);
        buf.writeBoolean(overrideDropRule);
        buf.writeBoolean(overrideDropRate);

        buf.writeInt(staticDroprate);
        buf.writeInt(commonDroprate);
        buf.writeInt(uncommonDroprate);
        buf.writeInt(rareDroprate);
        buf.writeInt(epicDroprate);
    }

    @Override
    public void packetReader(PacketByteBuf buf) {
        mode = buf.readEnumConstant(KeepTrinketMode.class);
        overrideDropRule = buf.readBoolean();
        overrideDropRate = buf.readBoolean();
        staticDroprate = buf.readInt();
        commonDroprate = buf.readInt();
        uncommonDroprate = buf.readInt();
        rareDroprate = buf.readInt();
        epicDroprate = buf.readInt();
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        nbt.putInt("mode", mode.ordinal());
        nbt.putBoolean("overrideDropRule", overrideDropRule);
        nbt.putBoolean("overrideDropRate", overrideDropRate);
        nbt.putInt("staticDR", staticDroprate);
        nbt.putInt("commonDR", commonDroprate);
        nbt.putInt("uncommonDR", uncommonDroprate);
        nbt.putInt("rareDR", rareDroprate);
        nbt.putInt("epicDR", epicDroprate);
        return nbt;
    }

    @Override
    public NbtCompound readNbt(NbtCompound nbt) {
        mode = KeepTrinketModeValues[nbt.getInt("mode")];
        overrideDropRule = nbt.getBoolean("overrideDropRule");
        overrideDropRate = nbt.getBoolean("overrideDropRate");

        staticDroprate = nbt.getInt("staticDR");
        commonDroprate = nbt.getInt("commonDR");
        uncommonDroprate = nbt.getInt("uncommonDR");
        rareDroprate = nbt.getInt("rareDR");
        epicDroprate = nbt.getInt("epicDR");
        return nbt;
    }

    @Override
    public String getModId() {
        return "trinkets";
    }
}

