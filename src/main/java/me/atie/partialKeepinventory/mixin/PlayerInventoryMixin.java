package me.atie.partialKeepinventory.mixin;

import me.atie.partialKeepinventory.formula.InventoryDroprateFormula;
import me.atie.partialKeepinventory.partialKeepinventory;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
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

import static me.atie.partialKeepinventory.partialKeepinventory.CONFIG_COMPONENT;

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

    private static InventoryDroprateFormula inventoryDroprateFormula;

    private double dropPercentageFromRarity(ItemStack item) {

        switch (CONFIG_COMPONENT.partialKeepinvMode()) {
            case CUSTOM -> {
                double d;
                d = inventoryDroprateFormula.getResult(item);
                partialKeepinventory.LOGGER.info("result is " + d);
                return d;
            }
            case PERCENTAGE -> {
                return CONFIG_COMPONENT.inventoryDroprate() / 100.0;
            }
            case RARITY -> {
                double droprate = switch (item.getRarity()) {
                    case COMMON -> CONFIG_COMPONENT.getCommonDroprate();
                    case UNCOMMON -> CONFIG_COMPONENT.getUncommonDroprate();
                    case RARE -> CONFIG_COMPONENT.getRareDroprate();
                    case EPIC -> CONFIG_COMPONENT.getEpicDroprate();
                };
                return droprate / 100.0;
            }
            default -> throw new IllegalStateException("Unexpected value: " + CONFIG_COMPONENT.partialKeepinvMode());
        }
    }




    @SuppressWarnings("UnusedAssignment")
    private void dropInventoryEqually(List<ItemStack> stacks) {
        HashMap<Item, ItemStack> itemDropCount = new HashMap<>(stacks.size());


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

        if( CONFIG_COMPONENT.isEnabled() ) {
            ci.cancel(); //if the mod is enabled we make sure we don't have the function call dropInventory and friends
// TODO working per player keepinventory

//            if(CONFIG.perPlayerKeepinventory().contains(this.player.getEntityName())) {
//                // Don't drop if the player is in the list of players to "save"
//                return;
//            }

            if( CONFIG_COMPONENT.partialKeepinvMode() == partialKeepinventory.KeepinvMode.CUSTOM) {
                try{
                    inventoryDroprateFormula = new InventoryDroprateFormula( (ServerPlayerEntity)this.player, CONFIG_COMPONENT.getExpression() );
                }catch (Exception e) {
                    String ErrorMessage = "Failed loading custom expression, resorting to percentage based drop behaviour";
                    partialKeepinventory.LOGGER.error(ErrorMessage);
                    this.player.getCommandSource().sendFeedback(Text.literal(ErrorMessage), true);
                    CONFIG_COMPONENT.partialKeepinvMode(partialKeepinventory.KeepinvMode.PERCENTAGE);
                }
            }

            dropInventoryEqually(this.main);
            dropInventoryEqually(this.armor);
            dropInventoryEqually(this.offHand);
        }
    }
}
