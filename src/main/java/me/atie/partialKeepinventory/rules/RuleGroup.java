package me.atie.partialKeepinventory.rules;

import me.atie.partialKeepinventory.PartialKeepInventory;
import me.atie.partialKeepinventory.util.InventoryUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Pair;

import java.util.ArrayList;
import java.util.Optional;

public class RuleGroup {
    public final ArrayList<DropRule> rules = new ArrayList<>();
    public final InventoryUtil.DropAction dropAction;
    public final float modifier;

    public final String name;

    public RuleGroup(String name, InventoryUtil.DropAction dropAction, float modifier) {
        this.name = name;
        this.modifier = modifier;
        this.dropAction = dropAction;
    }

    public void addRule(DropRule rule){
        rules.add(rule);
    }


    public Optional< Pair<Double, InventoryUtil.DropAction> > evaluate(ItemStack stack){
        for( var rule: rules){
            try {
                boolean ret = rule.evaluate(stack);
                if( !ret ){
                    PartialKeepInventory.LOGGER.info("Rule failed: " + rule.left.name + " " + rule.comparison.toString() + " " + rule.right.toString());
                    return Optional.empty();
                }
            } catch (Exception e) {
                PartialKeepInventory.LOGGER.error("Unable to evaluate rule " + rule.left.name + ", " + e.getMessage());
                return Optional.empty();
            }
        }
        return Optional.of(new Pair<>((double)modifier, dropAction));
    }
}
