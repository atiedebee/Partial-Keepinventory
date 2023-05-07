package me.atie.partialKeepinventory.formula;

import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Rarity;

import static me.atie.partialKeepinventory.PartialKeepInventory.CONFIG;
import static me.atie.partialKeepinventory.util.InventoryUtil.dropPercentageFromRarity;

public class InventoryDroprateFormula extends DroprateFormula {

    public static final String info =  """
                Custom expressions aren't checked on correctness yet. Please test them out in a separate world before adding them.
                Percentages are from 0.0 - 1.0
                Variables:
                        - spawnDistance:                distance from player to spawnpoint
                        - spawnX, spawnY, spawnZ:       spawn coordinates
                        - playerX, playerY, playerZ:    player coordinates
                        - rarityPercent:                get droprate from rarity as set in the config.
                        - isEpic, isRare, isCommon, isUncommon:
                                                        return 1.0 if true
                        - dropPercent:                  inventory droprate as set in the config
                 """;

    private ItemStack item;

    public InventoryDroprateFormula(ServerPlayerEntity player) {
        super(player);
    }

    public InventoryDroprateFormula() {
        super();
    }

    @Override
    protected void initDummyEnvVariables() {
        super.initDummyEnvVariables();
        env.addLazyVariable( "rarityPercent", () -> CONFIG.getCommonDroprate() / 100.0);
        env.addLazyVariable( "isEpic", () -> 0.0);
        env.addLazyVariable( "isRare", () -> 0.0);
        env.addLazyVariable( "isUncommon",  () -> 0.0);
        env.addLazyVariable( "isCommon", () -> 1.0);

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

        env.addLazyVariable( "dropPercent", () -> CONFIG.getInventoryDroprate() / 100.0);
    }




    public double getResult(ItemStack item) {
        this.item = item;
        return super.getResult();
    }

}
