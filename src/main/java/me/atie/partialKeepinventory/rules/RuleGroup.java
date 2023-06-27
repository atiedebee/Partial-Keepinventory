package me.atie.partialKeepinventory.rules;

import me.atie.partialKeepinventory.PartialKeepInventory;
import me.atie.partialKeepinventory.util.InventoryUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RuleGroup {
    public ArrayList<DropRule> rules = new ArrayList<>();
    public InventoryUtil.DropAction dropAction;
    public float modifier;
    public String name;

    public RuleGroup(String name, InventoryUtil.DropAction dropAction, float modifier) {
        this.name = name;
        this.modifier = modifier;
        this.dropAction = dropAction;
    }
    public RuleGroup(NbtCompound nbt) {
        readNbt(nbt);
    }

    public RuleGroup(PacketByteBuf buf) {
        readPacket(buf);
    }

    public void addRule(DropRule rule){
        rules.add(rule);
    }


    public Optional< Pair<Double, InventoryUtil.DropAction> > evaluate(ItemStack stack){
        for( var rule: rules){
            try {
                boolean ret = rule.evaluate(stack);
                if( !ret ){
                    PartialKeepInventory.LOGGER.info("Rule failed: " + rule.left.name + " " + rule.comparison.toString() + " " + rule.right.toString());
                    return Optional.empty();
                }
            } catch (Exception e) {
                PartialKeepInventory.LOGGER.error("Unable to evaluate rule " + rule.left.name + ", " + e.getMessage());
                return Optional.empty();
            }
        }
        return Optional.of(new Pair<>((double)modifier, dropAction));
    }

    public void writeNbt(NbtCompound nbt) {
        int i = 1;
        nbt.putString("name", this.name);
        nbt.putFloat("modifier", this.modifier);
        nbt.putInt("drop action", this.dropAction.ordinal());

        for(var rule: rules){
            NbtCompound ruleNbt = new NbtCompound();
            rule.writeNbt(ruleNbt);
            nbt.put("rule" + i, ruleNbt);
            i++;
        }
    }

    public void readNbt(NbtCompound nbt) {
        this.name = nbt.getString("name");
        this.modifier = nbt.getFloat("modifier");
        this.dropAction = InventoryUtil.DropAction.values()[ nbt.getInt("drop action") ];
        List<String> ruleKeys = nbt.getKeys().stream().filter(k -> k.getBytes()[0] == 'r').toList();

        for( var key: ruleKeys ){
            NbtCompound ruleCompound = nbt.getCompound(key);
            PartialKeepInventory.LOGGER.info("Rule compound nbt: " + ruleCompound.getKeys().toString());
            try {
                rules.add(DropRule.readNbt(ruleCompound));
            }catch(Exception e){
                PartialKeepInventory.LOGGER.error("Failed reading drop rules from nbt data: " + e.getMessage());
            }
        }
    }

    public void writePacket(PacketByteBuf buf){
        buf.writeString(this.name);
        buf.writeFloat(this.modifier);
        buf.writeEnumConstant(this.dropAction);

        buf.writeInt(rules.size());
        for( var rule: rules ){
            rule.writePacket(buf);
        }
    }

    public void readPacket(PacketByteBuf buf){
        this.name = buf.readString();
        this.modifier = buf.readFloat();
        this.dropAction = buf.readEnumConstant(InventoryUtil.DropAction.class);

        int ruleCount = buf.readInt();
        rules.ensureCapacity(ruleCount);

        try {
            for (int i = 0; i < ruleCount; i++) {
                rules.set(i, new DropRule(buf));
            }
        }catch (Exception e){
            PartialKeepInventory.LOGGER.error("Failed reading droprules: " + e.getMessage());
        }
    }

}
