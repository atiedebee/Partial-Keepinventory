package me.atie.partialKeepinventory.rules;

import net.minecraft.item.ItemStack;

//TODO: find a good way to represent the data in json format
public class DropRule {
    public RuleVariable left;
    public RuleComparison comparison;
    public Object right; // Type depends on what is on the left

    public DropRule(RuleVariable left, RuleComparison comparison, Object right){
        this.left = left;
        this.comparison = comparison;
        this.right = right;
    }

    public boolean evaluate(ItemStack stack) throws Exception {
        return left.evaluate(stack, comparison, right);
    }

}
