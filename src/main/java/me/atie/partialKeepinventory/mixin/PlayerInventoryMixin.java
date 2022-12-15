package me.atie.partialKeepinventory.mixin;

import me.atie.partialKeepinventory.config.pkiConfig;
import me.atie.partialKeepinventory.partialKeepinventory;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.List;

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


    private double dropPercentageFromRarity(ItemStack item) {

        if( pkiConfig.partialKeepinvMode != partialKeepinventory.KeepinvMode.RARITY ) {
            return pkiConfig.inventoryDroprate / 100.0;
        }

        return switch( item.getRarity() ){
            case COMMON -> pkiConfig.commonDroprate;
            case UNCOMMON -> pkiConfig.uncommonDroprate;
            case RARE -> pkiConfig.rareDroprate;
            case EPIC -> pkiConfig.epicDroprate;
        };
    }



    private void dropInventoryEqually(List<ItemStack> stacks) {
        HashMap<Integer, ItemStack> itemDropCount = new HashMap<>(stacks.size());

        for (ItemStack stack : stacks) {

            //to be changed later
            boolean dontDrop = false;
            if (dontDrop) {
                continue;
            }

            if ( EnchantmentHelper.hasVanishingCurse(stack) ) {
                //noinspection ReassignedVariable
                stack = ItemStack.EMPTY;
                continue;
            }

            if (!stack.isEmpty()) {
                int itemHashcode = stack.getItem().hashCode();
                int stackCount = stack.getCount();

                ItemStack item = itemDropCount.get(itemHashcode);
                if (item == null) item = new ItemStack(stack.getItem());

                item.setCount(item.getCount() + stackCount);
                itemDropCount.put(itemHashcode, item);
            }
        }
        itemDropCount.forEach(
                (k, v) -> {
                    final double percentage = dropPercentageFromRarity(v);
                    int count;
                    count = (int) (percentage * v.getCount());
                    v.setCount(count);
                }
        );

        for (ItemStack stack : stacks) {
            var itemToDrop = itemDropCount.get(stack.getItem().hashCode());

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
        if( pkiConfig.enableMod ) {
            dropInventoryEqually(this.main);
            dropInventoryEqually(this.armor);
            dropInventoryEqually(this.offHand);
            ci.cancel();
        }
    }
}
