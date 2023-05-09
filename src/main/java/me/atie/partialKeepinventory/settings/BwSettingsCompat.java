package me.atie.partialKeepinventory.settings;


import me.atie.partialKeepinventory.KeepXPMode;
import me.atie.partialKeepinventory.KeepinvMode;
import me.atie.partialKeepinventory.PartialKeepInventory;
import me.atie.partialKeepinventory.api.pkiSettingsApi;
import me.atie.partialKeepinventory.impl.Impl;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Set of functions for reading/writing settings from older versions.
 * NOTE:
 * - writePacket* still needs to write the version data to the buffer.
 * - readPacket* assumes the version is already read.
 * - there's no need for writeNbt methods, since the only times NBT data is written is when the server saves the settings,
 *   which should always be the correct version.
 *
 */
public abstract class BwSettingsCompat extends pkiSettings{
    public final static HashMap<pkiVersion, NbtReader> nbtReaders = new HashMap<>();
    public final static HashMap<pkiVersion, PacketReader> packetReaders = new HashMap<>();
    public final static HashMap<pkiVersion, PacketWriter> packetWriters = new HashMap<>();

    public static void init(){
        // v0.1.1 doesn't have any networking code
        final pkiVersion v0_1_1 = new pkiVersion(0, 1, 1);
        nbtReaders.put(v0_1_1, BwSettingsCompat::nbtReader_0_1_1);

        // v0.2.0
        final pkiVersion v0_2_0 = new pkiVersion(0, 2, 0);
        packetReaders.put(v0_2_0, BwSettingsCompat::packetReader_0_2_0);
        nbtReaders.put(v0_2_0, BwSettingsCompat::nbtReader_0_2_0);
        packetWriters.put(v0_2_0, BwSettingsCompat::packetWriter_0_2_0);

        // v0.2.1, same as 0.2.0
        final pkiVersion v0_2_1 = new pkiVersion(0, 2, 1);
        packetReaders.put(v0_2_1, BwSettingsCompat::packetReader_0_2_0);
        nbtReaders.put(v0_2_1, BwSettingsCompat::nbtReader_0_2_0);
        packetWriters.put(v0_2_1, BwSettingsCompat::packetWriter_0_2_0);
    }

    public static void writePacket(pkiSettings s, pkiVersion v, PacketByteBuf buf) {
        packetWriters.get(v).write(s, buf);
    }

    public static void readPacket(pkiSettings s, pkiVersion v, PacketByteBuf buf){
        packetReaders.get(v).read(s, buf);
    }

    public static NbtCompound readNbt(pkiSettings s, pkiVersion v, NbtCompound nbt){
        return nbtReaders.get(v).read(s, nbt);
    }


    // NOTE: 0.1.1 compatibility isn't actually available because the location is different
    // 0.1.1
    public static NbtCompound nbtReader_0_1_1(pkiSettings s, NbtCompound nbt) {
        s.enableMod = nbt.getBoolean("enable");
        s.keepinvMode = keepinvModeValues[nbt.getByte("invMode")];

        s.inventoryDroprate = nbt.getByte("invDR");
        s.commonDroprate = nbt.getByte("commonDR");
        s.uncommonDroprate = nbt.getByte("uncommonDR");
        s.rareDroprate = nbt.getByte("rareDR");
        s.epicDroprate = nbt.getByte("epicDR");
        s.expression = new StringBuffer(nbt.getString("invExpr"));

        s.keepxpMode = keepxpModeValues[nbt.getByte("xpMode")];
        s.xpDrop = nbt.getByte("xpDrop");
        s.xpLoss = nbt.getByte("xpLoss");
        s.xpDropExpression = new StringBuffer(nbt.getString("xpDropExpr"));
        s.xpLossExpression = new StringBuffer(nbt.getString("xpLossExpr"));

        NbtCompound playerNamesNbt = nbt.getCompound("savedPlayers");
        s.savedPlayers = new ArrayList<>();
        s.savedPlayers.addAll(playerNamesNbt.getKeys());

        return nbt;
    }

    // 0.2.0
    public static void packetWriter_0_2_0(pkiSettings s, PacketByteBuf buf) {
        PartialKeepInventory.modVersion.writePacket(buf);

        buf.writeBoolean(s.enableMod);

        buf.writeByteArray(new byte[]{s.inventoryDroprate, s.commonDroprate, s.uncommonDroprate, s.rareDroprate,
                s.epicDroprate, s.xpDrop, s.xpLoss} );
        buf.writeString(s.expression.toString());

        buf.writeEnumConstant(s.keepinvMode);
        buf.writeEnumConstant(s.keepxpMode);

        buf.writeString(s.xpDropExpression.toString());
        buf.writeString(s.xpLossExpression.toString());

        for( pkiSettingsApi setting: s.implementationSettings ){
            PacketByteBuf implBuf = PacketByteBufs.create();
            setting.packetWriter(implBuf);
            int size = implBuf.readableBytes();

            buf.writeInt(size);
            buf.writeString(setting.getModId());
            buf.writeBytes(implBuf);
        }

    }

    public static void packetReader_0_2_0(pkiSettings s, PacketByteBuf buf) {
        s.enableMod = buf.readBoolean();
        final byte[] droprates = buf.readByteArray();
        s.inventoryDroprate = droprates[0];
        s.commonDroprate = droprates[1];
        s.uncommonDroprate = droprates[2];
        s.rareDroprate = droprates[3];
        s.epicDroprate = droprates[4];
        s.xpDrop = droprates[5];
        s.xpLoss = droprates[6];
        s.expression = new StringBuffer(buf.readString());
        s.keepinvMode = buf.readEnumConstant(KeepinvMode.class);
        s.keepxpMode = buf.readEnumConstant(KeepXPMode.class);
        s.xpDropExpression = new StringBuffer(buf.readString());
        s.xpLossExpression = new StringBuffer(buf.readString());

        while( buf.readerIndex() < buf.readableBytes() ) {
            int size = buf.readInt();
             int index = buf.readerIndex();

            String modId = buf.readString();

            PacketByteBuf implBuf = new PacketByteBuf(buf.readBytes(size));

            if( Impl.entryPoints.containsKey(modId) ) {
                var tempSetting = Impl.entryPoints.get(modId).getSettings();
                if( tempSetting != null ) {
                    tempSetting.packetReader(implBuf);
                }
            }

            if( size < buf.readableBytes() ) {
                buf.readerIndex(index + size);
            }
        }


    }

    public static NbtCompound nbtReader_0_2_0(pkiSettings s, NbtCompound nbt){
        nbtReader_0_1_1(s, nbt);
        s.xpDropExpression = new StringBuffer(nbt.getString("xpDropExpr"));
        s.xpLossExpression = new StringBuffer(nbt.getString("xpLossExpr"));

        for(pkiSettingsApi setting: s.implementationSettings){
            String modId = setting.getModId();
            if( nbt.contains(modId)){
                setting.readNbt(nbt.getCompound(modId));
            }
        }
        return nbt;
    }


    interface NbtReader{
        NbtCompound read(pkiSettings s, NbtCompound nbt);
    }

    interface PacketReader{
        void read(pkiSettings s, PacketByteBuf buf);
    }

    interface PacketWriter{
        void write(pkiSettings s, PacketByteBuf buf);
    }


}
