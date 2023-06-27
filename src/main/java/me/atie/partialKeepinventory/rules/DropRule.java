package me.atie.partialKeepinventory.rules;

import io.netty.buffer.ByteBuf;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;

public class DropRule {
    public RuleVariable left;
    public RuleComparison comparison;
    public Object right; // Type depends on what is on the left

    public DropRule(RuleVariable left, RuleComparison comparison, Object right){
        this.left = left;
        this.comparison = comparison;
        this.right = right;
    }

    public DropRule(PacketByteBuf buf) throws Exception {
        readPacket(buf);
    }


    public boolean evaluate(ItemStack stack) throws Exception {
        return left.evaluate(stack, comparison, right);
    }

    public void writeNbt(NbtCompound nbt){
        nbt.putString("left", left.name);
        nbt.putInt("comparison", comparison.ordinal());
        switch( left.type ){
            case Boolean -> nbt.putBoolean("right", (boolean) right);
            case Percentage, Number -> nbt.putInt("right", (int) right);
            case String -> nbt.putString("right", (String)right);
            case Float -> nbt.putFloat("right", (float)right);
        }
    }

    public static DropRule readNbt(NbtCompound nbt) throws Exception {
        String leftName = nbt.getString("left");
        RuleVariable left =  RuleVariables.variables.get(leftName);
        if( left == null ){
            throw new Exception("Invalid variable when reading nbt: '" + leftName + "'");
        }

        RuleComparison comparison = RuleComparison.values()[nbt.getInt("comparison")];
        Object right = switch( left.type ){
            case Boolean -> nbt.getBoolean("right");
            case Percentage, Number -> nbt.getInt("right");
            case String -> nbt.getString("right");
            case Float -> nbt.getFloat("right");
        };
        return new DropRule(left, comparison, right);
    }

    public void writePacket(PacketByteBuf buf){
        PacketByteBuf buf2 = PacketByteBufs.create();
        buf2.writeString(this.left.name);
        buf2.writeEnumConstant(this.comparison);
        switch( left.type ){
            case Boolean -> buf2.writeBoolean( (boolean)right );
            case Percentage, Number -> buf2.writeInt( (int)right );
            case String -> buf2.writeString( (String)right );
            case Float -> buf2.writeFloat( (float)right );
        }
        buf.writeInt(buf2.getWrittenBytes().length);
        buf.writeBytes(buf2);
    }

    public void readPacket(PacketByteBuf buf) throws Exception {
        PacketByteBuf buf2 = PacketByteBufs.create();
        int size = buf.readInt();
        buf.readBytes(buf2, size);

        String name = buf2.readString();
        this.comparison = buf2.readEnumConstant(RuleComparison.class);

        this.left = RuleVariables.variables.get(name);
        if( left == null ){
            throw new Exception("Unable to read drop rules: variable '" + name + "' doesn't exist");
        }

        right = switch( left.type ){
            case Boolean -> buf2.readBoolean();
            case Percentage, Number -> buf2.readInt();
            case String -> buf2.readString();
            case Float -> buf2.readFloat();
        };
    }


}
