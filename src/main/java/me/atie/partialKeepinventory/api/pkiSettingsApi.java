package me.atie.partialKeepinventory.api;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;

public interface pkiSettingsApi extends Cloneable {
    String getModId();
    void packetWriter(PacketByteBuf buf);
    void packetReader(PacketByteBuf buf);
    NbtCompound writeNbt(NbtCompound nbt);
    NbtCompound readNbt(NbtCompound nbt);
}
