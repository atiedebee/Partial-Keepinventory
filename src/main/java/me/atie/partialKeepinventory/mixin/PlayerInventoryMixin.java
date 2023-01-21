package me.atie.partialKeepinventory.mixin;

import me.atie.partialKeepinventory.KeepinvMode;
import me.atie.partialKeepinventory.PartialKeepInventory;
import me.atie.partialKeepinventory.formula.InventoryDroprateFormula;
import me.atie.partialKeepinventory.util.InventoryUtil;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.collection.DefaultedList;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static me.atie.partialKeepinventory.PartialKeepInventory.CONFIG;
import static me.atie.partialKeepinventory.util.InventoryUtil.shouldDropInventory;

@Mixin(PlayerInventory.class)
public abstract class PlayerInventoryMixin {
    @Shadow @Final
    public DefaultedList<ItemStack> main;
    @Shadow @Final
    public DefaultedList<ItemStack> armor;
    @Shadow @Final
    public DefaultedList<ItemStack> offHand;

    @Shadow @Final public PlayerEntity player;

    private static InventoryDroprateFormula inventoryDroprateFormula;

    private int getInventorySize(){
        return this.main.size() + this.armor.size() + this.offHand.size();
    }

    private double dropPercentageFromRarity(ItemStack item) {

        switch (CONFIG.getPartialKeepinvMode()) {
            case CUSTOM:
                return inventoryDroprateFormula.getResult(item);
            case STATIC:
                return CONFIG.getInventoryDroprate() / 100.0;
            case RARITY:
                double droprate = switch (item.getRarity()) {
                    case COMMON -> CONFIG.getCommonDroprate();
                    case UNCOMMON -> CONFIG.getUncommonDroprate();
                    case RARE -> CONFIG.getRareDroprate();
                    case EPIC -> CONFIG.getEpicDroprate();
                };
                return droprate / 100.0;
            default:
                throw new IllegalStateException("Unexpected value: " + CONFIG.getPartialKeepinvMode());
        }
    }

    @SuppressWarnings("UnusedAssignment")
    private void getDropCounts(HashMap<Item, ItemStack> itemDropCounter, List<ItemStack> stacks){
        for (ItemStack stack : stacks) {
            //for future use, keep this
            boolean dontDrop = false;
            if (dontDrop) {
                continue;
            }

            if ( EnchantmentHelper.hasVanishingCurse(stack) ) {
                stack = ItemStack.EMPTY;
                continue;
            }

            if (!stack.isEmpty()) {
                Item itemAsKey = stack.getItem();
                int stackCount = stack.getCount();

                ItemStack itemstack = itemDropCounter.get(itemAsKey);
                if (itemstack == null) itemstack = new ItemStack(stack.getItem());

                itemstack.setCount(itemstack.getCount() + stackCount);
                itemDropCounter.put(itemAsKey, itemstack);
            }
        }

        for( Map.Entry<Item, ItemStack> set : itemDropCounter.entrySet() ){
            var v = set.getValue();
            final double percentage = dropPercentageFromRarity(v);
            int count;
            count = (int) (percentage * v.getCount());
            v.setCount(count);
        }

    }


    private void dropInventoryEqually(List<ItemStack> stacks) {
        HashMap<Item, ItemStack> itemDropCounter;

        itemDropCounter = new HashMap<>(stacks.size());

        getDropCounts(itemDropCounter, stacks);

        for (ItemStack stack : stacks) {
            ItemStack itemToDrop = itemDropCounter.get(stack.getItem());

            if (itemToDrop != null) {
                int dropAmount = Math.min(stack.getCount(), itemToDrop.getCount());


                var dropStack = stack.copy();
                dropStack.setCount(dropAmount);

                this.player.dropItem(dropStack, false);

                itemToDrop.decrement(dropAmount);

                stack.decrement(dropAmount);
            }
        }
    }

    @Inject(method = "dropAll()V", at = @At("HEAD"), cancellable = true)
    public void dropSome(CallbackInfo ci) {


        if( CONFIG.getEnableMod() && CONFIG.getPartialKeepinvMode() != KeepinvMode.VANILLA ) {
            ci.cancel(); //if the mod is enabled we make sure we don't have the function call dropInventory and friends

            if( !shouldDropInventory( (ServerPlayerEntity)this.player) ) {
                // Don't drop
                return;
            }

            if( CONFIG.getPartialKeepinvMode() == KeepinvMode.CUSTOM) {

                try{
                    inventoryDroprateFormula = new InventoryDroprateFormula( (ServerPlayerEntity)this.player, CONFIG.getExpression().toString() );
                }
                catch (Exception e) {
                    String ErrorMessage = "Failed loading custom expression: \"" + CONFIG.getExpression() + "\"\nResorting to percentage based drop behaviour";
                    PartialKeepInventory.LOGGER.error(ErrorMessage + "\n" + e.getMessage());
                    this.player.getCommandSource().sendFeedback(Text.literal(ErrorMessage).setStyle(Style.EMPTY.withColor(Formatting.RED)), true);
                    CONFIG.setPartialKeepinvMode(KeepinvMode.STATIC);
                }
            }

            int capacity = getInventorySize();
            List<ItemStack> inv = new ArrayList<>(capacity);

            inv.addAll(this.main);
            inv.addAll(this.armor);
            inv.addAll(this.offHand);

            for(var getter: InventoryUtil.inventorySlotGetters){
                inv.addAll(getter.apply(this.player));
            }
            for(var item: inv){
                if(!item.isEmpty()){
                    PartialKeepInventory.LOGGER.info("Item: " + item.getItem().getName());
                }
            }

            dropInventoryEqually(inv);
        }
    }
}
