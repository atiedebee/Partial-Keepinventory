package me.atie.partialKeepinventory.impl.trinkets;

import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.TrinketComponent;
import dev.emi.trinkets.api.TrinketEnums;
import dev.emi.trinkets.api.TrinketsApi;
import dev.emi.trinkets.api.event.TrinketDropCallback;
import me.atie.partialKeepinventory.PartialKeepInventory;
import me.atie.partialKeepinventory.util.InventoryUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Pair;

import java.util.ArrayList;
import java.util.List;

public class TrinketsImpl {
    public static TrinketsSettings trinketSettings = new TrinketsSettings();


    public static void load(){
        PartialKeepInventory.LOGGER.info("Trinkets compatibility enabled");

        TrinketDropCallback.EVENT.register(((dropRule, itemStack, slotReference, livingEntity) -> {
            PartialKeepInventory.LOGGER.info("Drop callback is being executed");
            if( trinketSettings.OverrideDropRule() ){
                return TrinketEnums.DropRule.KEEP;
            }
            return dropRule;
        }));

        InventoryUtil.inventorySlotGetters.add(TrinketsImpl::getTrinketSlots);
        InventoryUtil.droprateGetters.add(TrinketsImpl::getTrinketDroprate);
    }


    public static InventoryUtil.DropBehaviour getTrinketDroprate(PlayerEntity player, ItemStack itemStack) {
        if( TrinketsApi.getTrinket( itemStack.getItem() ).equals( TrinketsApi.getDefaultTrinket() ) ) {
            return InventoryUtil.NO_DROPBEHAVIOUR;
        }

        double val = switch(trinketSettings.getMode()) {
            case STATIC -> trinketSettings.getStaticDroprate();
            case RARITY -> switch (itemStack.getRarity()) {
                case COMMON -> trinketSettings.getCommonDroprate();
                case UNCOMMON -> trinketSettings.getUncommonDroprate();
                case RARE -> trinketSettings.getRareDroprate();
                case EPIC -> trinketSettings.getEpicDroprate();
            };
            case DEFAULT -> 100;
        } / 100.0;


        InventoryUtil.DropAction da = getTrinketDropaction(player, itemStack);
        return new InventoryUtil.DropBehaviour(da, val);
    }

    private static InventoryUtil.DropAction getTrinketDropaction(PlayerEntity player, ItemStack stack) {
        if( TrinketsApi.getTrinketComponent(player).isPresent() ){
            return InventoryUtil.DropAction.NONE;
        }

        SlotReference ref = null;
        TrinketComponent trinketComponent = TrinketsApi.getTrinketComponent(player).get();
        for( var pair: trinketComponent.getEquipped(stack.getItem()) ) {
            if(pair.getRight().isItemEqual(stack)){
                ref = pair.getLeft();
                break;
            }
        }
        if( ref == null ){
            PartialKeepInventory.LOGGER.info("Reference is null, resulting to default dropaction");
            return InventoryUtil.DropAction.DROP;
        }
        TrinketEnums.DropRule dr = TrinketsApi.getTrinket(stack.getItem()).getDropRule(stack, ref, player);

        return switch (dr){
            case KEEP -> InventoryUtil.DropAction.KEEP;
            case DROP -> InventoryUtil.DropAction.DROP;
            case DESTROY -> InventoryUtil.DropAction.DESTROY;
            case DEFAULT -> InventoryUtil.DropAction.KEEP;
        };
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

