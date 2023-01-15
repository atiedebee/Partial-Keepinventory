package me.atie.partialKeepinventory.formula;

import me.atie.partialKeepinventory.PartialKeepInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Rarity;
import net.minecraft.util.math.BlockPos;
import redempt.crunch.Crunch;
import redempt.crunch.functional.EvaluationEnvironment;

import static me.atie.partialKeepinventory.PartialKeepInventory.CONFIG_COMPONENT;

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

    public InventoryDroprateFormula(ServerPlayerEntity player, String expression) {
        super(player);
        env = new EvaluationEnvironment();

        final BlockPos spawnPos = getPlayerSpawn();

        PartialKeepInventory.LOGGER.info(expression);

        env.addLazyVariable("spawnDistance", this::getSpawnDistance);
        env.addLazyVariable("spawnX", spawnPos::getX);
        env.addLazyVariable("spawnY", spawnPos::getY);
        env.addLazyVariable("spawnZ", spawnPos::getZ);

        env.addLazyVariable("playerX", this.player::getX);
        env.addLazyVariable( "playerY", this.player::getY);
        env.addLazyVariable( "playerZ", this.player::getZ);


        env.addLazyVariable( "rarityPercent", () -> dropPercentageFromRarity(item));
        env.addLazyVariable( "isEpic", () -> item.getRarity().equals(Rarity.EPIC) ? 1.0 : 0.0);
        env.addLazyVariable( "isRare", () -> item.getRarity().equals(Rarity.RARE) ? 1.0 : 0.0);
        env.addLazyVariable( "isUncommon",  () -> item.getRarity().equals(Rarity.UNCOMMON) ? 1.0 : 0.0);
        env.addLazyVariable( "isCommon", () -> item.getRarity().equals(Rarity.COMMON) ? 1.0 : 0.0);

        env.addLazyVariable( "dropPercent", () -> CONFIG_COMPONENT.getInventoryDroprate() / 100.0);

        env.addFunction("max", 2, (a) -> Math.max(a[0], a[1]));
        env.addFunction("min", 2, (a) -> Math.min(a[0], a[1]));

        cx = Crunch.compileExpression(expression, env);
    }




    private double dropPercentageFromRarity(ItemStack item){
        double droprate =  switch( item.getRarity() ){
            case COMMON -> CONFIG_COMPONENT.getCommonDroprate();
            case UNCOMMON -> CONFIG_COMPONENT.getUncommonDroprate();
            case RARE -> CONFIG_COMPONENT.getRareDroprate();
            case EPIC -> CONFIG_COMPONENT.getEpicDroprate();
        };

        return droprate / 100.0;
    }


    public double getResult(ItemStack itemStack) {
        this.item = itemStack;

        return cx.evaluate();
    }


}
