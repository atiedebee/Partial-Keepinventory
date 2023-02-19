package me.atie.partialKeepinventory.settings;

import me.atie.partialKeepinventory.PartialKeepInventory;
import net.fabricmc.loader.api.Version;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;

import java.util.List;

public class pkiVersion {
    public byte major;
    public byte minor;
    public byte patch;


    public pkiVersion(String versionString) {
        if( isValidString(versionString)) {
            parseVersionString(versionString);
        }else{
            throw new RuntimeException("Invalid version string");
        }
    }

    public pkiVersion(Version version) {
        parseVersionString(version.getFriendlyString());
    }

    public pkiVersion(int major, int minor, int patch){
        setVersion(major, minor, patch);
    }

    public pkiVersion(NbtCompound nbt){
        this.readNbt(nbt);
    }

    public pkiVersion(PacketByteBuf buf){
        this.readPacket(buf);
    }

    /**
     * Checks if the string is formatted as "x.y.z"
     * @param versionString string with version number
     * @return boolean for if the string is valid or not
     */
    public static boolean isValidString(String versionString){
        byte[] bytes = versionString.getBytes();
        int i = 1;
        if( !Character.isDigit(bytes[0]) ){
            return false;
        }

        while( Character.isDigit(bytes[i])){
            i += 1;
        }

        for( int j = 0; j < 2; j++ ){
            if (bytes[i] != '.') {
                return false;
            }
            i += 1;

            if (!Character.isDigit(bytes[i])) {
                return false;
            }

            do {
                i += 1;
            } while( i < bytes.length && Character.isDigit(bytes[i]) );
        }

        return true;
    }

    public void parseVersionString(String versionString){
        int dot1 = versionString.indexOf('.');
        int dot2 = versionString.lastIndexOf('.');

        major = Byte.parseByte( versionString.substring(0, dot1) );
        minor = Byte.parseByte( versionString.substring(dot1+1, dot2) );
        patch = Byte.parseByte( versionString.substring(dot2+1) );
    }

    public String toString(){
        return major + "." + minor + "." + patch;
    }

    public void writePacket(PacketByteBuf buf){
        buf.writeByte(major);
        buf.writeByte(minor);
        buf.writeByte(patch);
    }

    public void readPacket(PacketByteBuf buf){
        major = buf.readByte();
        minor = buf.readByte();
        patch = buf.readByte();
    }

    public void writeNbt(NbtCompound nbt){
        nbt.putByteArray("version", List.of(major, minor, patch));
    }

    public void readNbt(NbtCompound nbt){
        if( nbt.contains("version") ) {
            byte[] arr = nbt.getByteArray("version");
            setVersion(arr[0], arr[1], arr[2]);
        }
        else{
            PartialKeepInventory.LOGGER.error("Invalid NBT data saved (couldn't find field 'version')");
            setVersion(0, 0, 0);
        }
    }

    // versions won't have a number larger than 255, so this should work flawlessly
    @Override
    public int hashCode() {
        return (major << 16) | (minor << 8) | (patch);
    }

    @Override
    public boolean equals(Object o){
        if( !o.getClass().equals(this.getClass()) )
            throw new RuntimeException("Object not of class 'pkiVersion'");

        pkiVersion v = (pkiVersion) o;
        boolean ret;
        ret = v.major == major;
        ret &= v.minor == minor;
        ret &= v.patch == patch;
        return ret;
    }

    public boolean lessThan(pkiVersion v){
        boolean ret;
        ret = major < v.major;
        ret &= minor < v.minor;
        ret &= patch < v.patch;
        return ret;
    }

    public boolean moreThan(pkiVersion v){
        return !lessThan(v);
    }

    public void setVersion(Integer major, Integer minor, Integer patch) {
        setVersion(major.byteValue(), minor.byteValue(), patch.byteValue());
    }

    public void setVersion(byte major, byte minor, byte patch){
        this.major = major;
        this.minor = minor;
        this.patch = patch;
    }


}
