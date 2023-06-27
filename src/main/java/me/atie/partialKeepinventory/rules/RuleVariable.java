package me.atie.partialKeepinventory.rules;

import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public class RuleVariable {
    @NotNull
    public final RuleType type;
    @NotNull
    private final Function<ItemStack, Object> getValue;
    @NotNull
    public final CompareRule compare;
    @NotNull
    public final String name;

    public RuleVariable(@NotNull String name, @NotNull RuleType type, @NotNull Function<ItemStack, Object> getValue, @NotNull CompareRule compare){
        this.type = type;
        this.getValue = getValue;
        this.compare = compare;
        this.name = name;
    }

    public boolean evaluate(ItemStack itemStack, RuleComparison comparison, Object o) throws Exception{
        if( !type.canBeComparedUsing(comparison) ){
            throw new Exception("Cannot compare type '" + type.name() + "' using " + comparison);
        }
        return compare.compare(this.getValue.apply(itemStack), comparison, o);
    }


    public interface CompareRule{
        /**
         * @param self The value obtained from the rule variable.
         * @param comparison The type of comparison performed. This comparison is checked beforehand.
         * @param other The value that is being compared with.
         * @return Whether the comparison is true or false.
         */
        boolean compare(Object self, RuleComparison comparison, Object other);
    }
}
