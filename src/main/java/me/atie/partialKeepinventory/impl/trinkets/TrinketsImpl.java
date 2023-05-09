package me.atie.partialKeepinventory.impl.trinkets;

import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.TrinketComponent;
import dev.emi.trinkets.api.TrinketEnums;
import dev.emi.trinkets.api.TrinketsApi;
import dev.emi.trinkets.api.event.TrinketDropCallback;
import me.atie.partialKeepinventory.PartialKeepInventory;
import me.atie.partialKeepinventory.api.pkiApi;
import me.atie.partialKeepinventory.api.pkiSettingsApi;
import me.atie.partialKeepinventory.util.InventoryUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Pair;
import net.minecraft.util.Rarity;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TrinketsImpl extends pkiApi {
    public static TrinketsSettings trinketSettings = new TrinketsSettings();
    private static final Random rand = new Random();


    public TrinketsImpl(){
        TrinketDropCallback.EVENT.register(((dropRule, itemStack, slotReference, livingEntity) -> {
            if( trinketSettings.OverrideDropRule() ){
                return TrinketEnums.DropRule.KEEP;
            }
            return dropRule;
        }));
        TrinketsCommands.initTrinketCommands();
    }

    @Override
    public pkiSettingsApi getSettings(){
        return trinketSettings;
    }

    double rarityDroprateFromItem(Rarity rarity){
        return switch (rarity) {
            case COMMON -> trinketSettings.getCommonDroprate();
            case UNCOMMON -> trinketSettings.getUncommonDroprate();
            case RARE -> trinketSettings.getRareDroprate();
            case EPIC -> trinketSettings.getEpicDroprate();
        };
    }

    @Override
    public @NotNull String getModId() {
        return "pki-trinkets";
    }

    @Override
    public Pair<Double, InventoryUtil.DropAction> getDropBehaviour(PlayerEntity player, ItemStack itemStack) {
        if( TrinketsApi.getTrinket( itemStack.getItem() ).equals( TrinketsApi.getDefaultTrinket() ) ) {
            return null;
        }


        double val = 0.0;
        if( trinketSettings.OverrideDropRule() ) {
            val = switch (trinketSettings.getMode()) {
                case STATIC -> trinketSettings.getStaticDroprate();
                case RARITY -> rarityDroprateFromItem(itemStack.getRarity());
                case CHANCE -> rand.nextDouble(0.0, 100.0) < rarityDroprateFromItem(itemStack.getRarity()) ? 100.0 : 0.0;
                case DEFAULT -> 100.0;
            };
            val = val / 100.0;
        }

        InventoryUtil.DropAction da = getTrinketDropaction(player, itemStack);
        return new Pair<>(val, da);
    }

    private static InventoryUtil.DropAction getTrinketDropaction(PlayerEntity player, ItemStack stack) {
        if( TrinketsApi.getTrinketComponent(player).isEmpty() ){
            // really do hope this doesn't happen
            PartialKeepInventory.LOGGER.error("Couldn't get trinket component from player " + player.getDisplayName().getString());
            return InventoryUtil.DropAction.NONE;
        }

        TrinketComponent trinketComponent = TrinketsApi.getTrinketComponent(player).get();

        var equipped = trinketComponent.getEquipped(stack.getItem());
        if( equipped.size() == 0 ){
            return InventoryUtil.DropAction.NONE;
        }
        SlotReference ref = equipped.get(0).getLeft();

        TrinketEnums.DropRule dr = TrinketEnums.DropRule.DEFAULT;
        if( !trinketSettings.OverrideDropRule()) {
            dr = TrinketsApi.getTrinket(stack.getItem()).getDropRule(stack, ref, player);
        }
        return switch (dr){
            case KEEP, DEFAULT -> InventoryUtil.DropAction.KEEP;
            case DROP -> InventoryUtil.DropAction.DROP;
            case DESTROY -> InventoryUtil.DropAction.DESTROY;
        };
    }

    public List<ItemStack> getInventorySlots(PlayerEntity player){
        return getTrinketSlots(player);
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

