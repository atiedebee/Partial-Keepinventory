package me.atie.partialKeepinventory.util;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.Rarity;

public class KeyItemStack extends Object {
    public final Item item;
    public final NbtList enchantments;
    public final Rarity rarity;
    public KeyItemStack(ItemStack s){
        item = s.getItem();
        enchantments = s.getEnchantments();
        rarity = s.getRarity();
    }

    @Override
    public boolean equals(Object other){
        KeyItemStack kis = (KeyItemStack) other;
        return kis.item.equals(item) && kis.rarity.equals(rarity) && kis.enchantments.equals(enchantments);
    }

    @Override
    public int hashCode(){
        return item.hashCode() ^ rarity.hashCode();
    }
}
