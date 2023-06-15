package me.atie.partialKeepinventory.mixin;

import me.atie.partialKeepinventory.KeepinvMode;
import me.atie.partialKeepinventory.PartialKeepInventory;
import me.atie.partialKeepinventory.formula.InventoryDroprateFormula;
import me.atie.partialKeepinventory.impl.Impl;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Pair;
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
import static me.atie.partialKeepinventory.util.InventoryUtil.*;

@Mixin(PlayerInventory.class)
public abstract class PlayerInventoryMixin {
    @Shadow @Final
    public DefaultedList<ItemStack> main;
    @Shadow @Final
    public DefaultedList<ItemStack> armor;
    @Shadow @Final
    public DefaultedList<ItemStack> offHand;

    @Shadow @Final
    public PlayerEntity player;

    private InventoryDroprateFormula inventoryDroprateFormula = null;

    private int getInventorySize(){
        return this.main.size() + this.armor.size() + this.offHand.size();
    }



    /**
     * @param itemStack The item
     * @return What percentage of the item should be dropped (where 1.0 == 100%)
     */
    private Pair<Double, DropAction> getDropBehaviour(ItemStack itemStack) {

        /* First test out custom drop behaviour*/
        for( var e: Impl.entryPoints.entrySet()){
            Pair<Double, DropAction> behaviour = e.getValue().getDropBehaviour(player, itemStack);
            if( behaviour != null && behaviour.getRight() != DropAction.NONE){
                return behaviour;
            }
        }

        double percentage = switch (CONFIG.getPartialKeepinvMode()) {
            case CUSTOM -> inventoryDroprateFormula.getResult(itemStack);
            case STATIC -> CONFIG.getInventoryDroprate() / 100.0;
            case RARITY -> dropPercentageFromRarity(itemStack);
            default -> throw new IllegalStateException("Unexpected value: " + CONFIG.getPartialKeepinvMode());
        };

        return new Pair<>(percentage, DropAction.DROP);
    }

    /**
     * @param itemDropCounter A hashmap containing how much to drop of each item and what behaviour to apply
     * @param stacks A list of references to item stacks in the inventory
     */
    @SuppressWarnings("UnusedAssignment")
    private void getItemCounts(HashMap< Item, Pair<ItemStack, DropAction> > itemDropCounter, List<ItemStack> stacks){
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

                Pair<ItemStack, DropAction> pair =  itemDropCounter.get(itemAsKey);
                if( pair == null ) {
                    pair = new Pair<>(new ItemStack(itemAsKey, 0), DropAction.DROP);
                    itemDropCounter.put(itemAsKey, pair);
                }

                ItemStack itemstack = pair.getLeft();
                itemstack.setCount(itemstack.getCount() + stackCount);
                itemDropCounter.get(itemAsKey).setLeft(itemstack);
            }
        }

        for( Map.Entry<Item, Pair<ItemStack, DropAction>> set : itemDropCounter.entrySet() ){
            Pair<ItemStack, DropAction> itemDropBehaviour = set.getValue();

            ItemStack itemStack = itemDropBehaviour.getLeft();

            Pair<Double, DropAction> behaviour = getDropBehaviour(itemStack);
            itemDropBehaviour.setRight( behaviour.getRight() );
            double dropPercentage = behaviour.getLeft();

            final int dropCount = (int) (dropPercentage * itemStack.getCount());
            itemStack.setCount(dropCount);
        }

    }


    /**
     * Drop / remove items.
     * @param invStack  A reference of the stack object that's in the inventory
     * @param dropCounter A reference of the stack object that's used to count how many items to drop
     */
    private void dropItems(ItemStack invStack, Pair<ItemStack, DropAction> dropCounter){
        ItemStack dropItemStack = dropCounter.getLeft();
        DropAction dropAction = dropCounter.getRight();

        int dropAmount = Math.min(invStack.getCount(), dropItemStack.getCount());

        var dropStack = invStack.copy();
        dropStack.setCount(dropAmount);

        switch(dropAction){
            case DROP:
                this.player.dropItem(dropStack, true, false);
                // fallthrough
            case DESTROY:
                dropItemStack.decrement(dropAmount);
                invStack.decrement(dropAmount);
                break;
            case KEEP:
            default://Don't do anything
                break;
        }


    }


    /**
     * @param stacks List of stacks that are references to the stacks in the inventory
     */
    private void dropInventoryEqually(List<ItemStack> stacks) {
        HashMap<Item, Pair<ItemStack, DropAction>> itemDropCounter;

        itemDropCounter = new HashMap<>(stacks.size());

        getItemCounts(itemDropCounter, stacks);

        for (ItemStack stack : stacks) {
            Pair<ItemStack, DropAction> entry = itemDropCounter.get(stack.getItem());

            if (entry != null) {
                dropItems(stack, entry);
            }
        }
    }

    private List<ItemStack> loadInventory(){
        int capacity = getInventorySize();
        List<ItemStack> inv = new ArrayList<>(capacity);

        inv.addAll(this.main);
        inv.addAll(this.armor);
        inv.addAll(this.offHand);

        var it = Impl.entryPoints.entrySet().iterator();
        while(it.hasNext()){
            List<ItemStack> slots = it.next().getValue().getInventorySlots(this.player);
            if( slots != null ) {
                inv.addAll(slots);
            }
        }
        return inv;
    }


    @Inject(method = "dropAll()V", at = @At("HEAD"), cancellable = true)
    public void dropSome(CallbackInfo ci) {

        if( CONFIG.getEnableMod()  ) {
            //if the mod is enabled we make sure we don't have 'dropAll' call dropInventory and friends
            ci.cancel();

            if( !shouldDropInventory( (ServerPlayerEntity)this.player) ) {
                return;
            }

            if( CONFIG.getPartialKeepinvMode() == KeepinvMode.CUSTOM && inventoryDroprateFormula == null) {
                try{
                    inventoryDroprateFormula = new InventoryDroprateFormula( (ServerPlayerEntity)this.player );

                    inventoryDroprateFormula.compileExpression( CONFIG.getInvExpression().toString() );
                }
                catch (Exception e) {
                    String ErrorMessage = "Failed loading custom expression: \"" + CONFIG.getInvExpression() + "\"\nResorting to percentage based drop behaviour";
                    PartialKeepInventory.LOGGER.error(ErrorMessage + "\n" + e.getMessage());
                    this.player.getCommandSource().sendFeedback(() -> Text.literal(ErrorMessage).setStyle(Style.EMPTY.withColor(Formatting.RED)), true);
                    CONFIG.setPartialKeepinvMode(KeepinvMode.STATIC);
                }
            }

            List<ItemStack> inv = loadInventory();
            dropInventoryEqually(inv);
        }


    }
}
