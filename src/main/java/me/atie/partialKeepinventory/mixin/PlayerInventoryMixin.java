package me.atie.partialKeepinventory.mixin;

import me.atie.partialKeepinventory.StatementInterpreter;
import me.atie.partialKeepinventory.partialKeepinventory;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.collection.DefaultedList;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static me.atie.partialKeepinventory.partialKeepinventory.CONFIG;

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

    private static StatementInterpreter statementInterpreter;

    private double dropPercentageFromRarity(ItemStack item) {

        switch( CONFIG.partialKeepinvMode() ){
            case CUSTOM:
                double d;
                    d = statementInterpreter.getResult(item);
                    partialKeepinventory.LOGGER.info("result is " + d);
                    return d;

            case PERCENTAGE:
                return CONFIG.inventoryDroprate() / 100.0;

            case RARITY:
                double droprate =  switch( item.getRarity() ){
                    case COMMON -> CONFIG.commonDroprate();
                    case UNCOMMON -> CONFIG.uncommonDroprate();
                    case RARE -> CONFIG.rareDroprate();
                    case EPIC -> CONFIG.epicDroprate();
                };

                return droprate / 100.0;
            default:
                throw new IllegalStateException("Unexpected value: " + CONFIG.partialKeepinvMode());
        }
    }




    @SuppressWarnings("UnusedAssignment")
    private void dropInventoryEqually(List<ItemStack> stacks) {
        HashMap<Item, ItemStack> itemDropCount = new HashMap<>(stacks.size());


        for (ItemStack stack : stacks) {
            //for future use
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

                ItemStack itemstack = itemDropCount.get(itemAsKey);
                if (itemstack == null) itemstack = new ItemStack(stack.getItem());

                itemstack.setCount(itemstack.getCount() + stackCount);
                itemDropCount.put(itemAsKey, itemstack);
            }
        }

        for( Map.Entry<Item, ItemStack> set : itemDropCount.entrySet() ){
            var v = set.getValue();
            final double percentage = dropPercentageFromRarity(v);
            int count;
            count = (int) (percentage * v.getCount());
            v.setCount(count);
        }

        for (ItemStack stack : stacks) {
            var itemToDrop = itemDropCount.get(stack.getItem());

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

        if( CONFIG.enableMod() ) {
            ci.cancel(); //if the mod is enabled we make sure we don't have the function call dropInventory and friends

            if(CONFIG.perPlayerKeepinventory().contains(this.player.getEntityName())) {
                // Don't drop if the player is in the list of players to "save"
                return;
            }

            if( CONFIG.partialKeepinvMode() == partialKeepinventory.KeepinvMode.CUSTOM) {
                try{
                    statementInterpreter = new StatementInterpreter( (ServerPlayerEntity)this.player, "HI" );
                }catch (Exception e) {
                    partialKeepinventory.LOGGER.error("Failed loading custom expression, resorting to percentage based drop behaviour");
                    CONFIG.partialKeepinvMode(partialKeepinventory.KeepinvMode.PERCENTAGE);
                }
            }

            dropInventoryEqually(this.main);
            dropInventoryEqually(this.armor);
            dropInventoryEqually(this.offHand);
        }
    }
}
