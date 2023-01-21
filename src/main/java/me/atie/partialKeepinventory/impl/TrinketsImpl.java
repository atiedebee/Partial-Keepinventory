package me.atie.partialKeepinventory.impl;

import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.TrinketComponent;
import dev.emi.trinkets.api.TrinketsApi;
import me.atie.partialKeepinventory.util.InventoryUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Pair;

import java.util.ArrayList;
import java.util.List;

public class TrinketsImpl {

    public static void addInventoryGetters(){
        InventoryUtil.inventorySlotGetters.add(TrinketsImpl::getTrinketSlots);
    }

    private static List<ItemStack> getTrinketSlots(PlayerEntity player) {
        ArrayList<ItemStack> inv = new ArrayList<>();


        var playerSlots = TrinketsApi.getTrinketComponent(player);
        if(playerSlots.isEmpty()){
            return null;
        }

        TrinketComponent component = playerSlots.get();
        var equipped = component.getAllEquipped();

        for( Pair<SlotReference, ItemStack> pair:
                equipped ){
            inv.add(pair.getRight());
        }
        return inv;
    }
}