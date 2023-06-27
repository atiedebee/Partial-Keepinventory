package me.atie.partialKeepinventory.formula;

import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Rarity;

import static me.atie.partialKeepinventory.PartialKeepInventory.CONFIG;
import static me.atie.partialKeepinventory.util.InventoryUtil.dropPercentageFromRarity;

public class InventoryDroprateFormula extends DroprateFormula {

    private ItemStack item;

    public InventoryDroprateFormula(ServerPlayerEntity player) {
        super(player);
    }

    @Override
    protected void initDummyEnvVariables() {
        super.initDummyEnvVariables();
        env.addLazyVariable( "rarityPercent", () -> CONFIG.getCommonDroprate() / 100.0);
        env.addLazyVariable( "isEpic", () -> 0.0);
        env.addLazyVariable( "isRare", () -> 0.0);
        env.addLazyVariable( "isUncommon",  () -> 0.0);
        env.addLazyVariable( "isCommon", () -> 1.0);

        env.addLazyVariable("durability", () -> 1.0);

        env.addLazyVariable( "dropPercent", () -> CONFIG.getInventoryDroprate() / 100.0);
    }
    @Override
    protected void initEnvVariables() {
        super.initEnvVariables();
        env.addLazyVariable( "rarityPercent", () -> dropPercentageFromRarity(item));
        env.addLazyVariable( "isEpic", () -> item.getRarity().equals(Rarity.EPIC) ? 1.0 : 0.0);
        env.addLazyVariable( "isRare", () -> item.getRarity().equals(Rarity.RARE) ? 1.0 : 0.0);
        env.addLazyVariable( "isUncommon",  () -> item.getRarity().equals(Rarity.UNCOMMON) ? 1.0 : 0.0);
        env.addLazyVariable( "isCommon", () -> item.getRarity().equals(Rarity.COMMON) ? 1.0 : 0.0);
        env.addLazyVariable("durability", () -> item.getMaxDamage() > 0 ? item.getDamage() / (float)item.getMaxDamage() : 1.0);

        env.addLazyVariable( "dropPercent", () -> CONFIG.getInventoryDroprate() / 100.0);
    }




    public double getResult(ItemStack item) {
        this.item = item;
        return super.getResult();
    }

}
