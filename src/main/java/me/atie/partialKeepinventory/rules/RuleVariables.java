package me.atie.partialKeepinventory.rules;

import me.atie.partialKeepinventory.PartialKeepInventory;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Objects;
import java.util.function.Function;

public class RuleVariables {
    public static HashMap<String, RuleVariable> variables = new HashMap<>(32);


    public static RuleVariable register(String name, RuleVariable v) throws Exception {
        if( variables.containsKey(name) ){
            throw new Exception("Couldn't register rule variable '" + name + "' because it already exists.");
        }
        return variables.put(name, v);
    }

    public static RuleVariable register(String name, RuleType type, Function<ItemStack, Object> getValue, RuleVariable.CompareRule compare) throws Exception {
        return register(name, new RuleVariable(name, type, getValue, compare));
    }


    public final static RuleVariable isArmor;
    public final static RuleVariable defense;
    public final static RuleVariable hasDurability;
    public final static RuleVariable durability;
    public final static RuleVariable damage;
    public final static RuleVariable isEnchanted;
    public final static RuleVariable isTool;
    public final static RuleVariable isFood;
    public final static RuleVariable saturation;
    public final static RuleVariable isStackable;
    public final static RuleVariable stackSize;
    public final static RuleVariable count;
    public final static RuleVariable isShulker;
    public final static RuleVariable isMaterial;
    public final static RuleVariable rarity;
    public final static RuleVariable isPlaceable;
    public final static RuleVariable hasTag;


    static{
        try {
            // ----- armor -----
            isArmor = register("isArmor", RuleType.Boolean,
                    itemStack -> itemStack.getItem() instanceof ArmorItem,
                    RuleVariables::compareBoolean);

            defense = register("defense", RuleType.Number,
                    (itemStack -> itemStack.getItem() instanceof  ArmorItem ?  ((ArmorItem)itemStack.getItem()).getProtection() : 0),
                    RuleVariables::compareInt);
            // ----- armor / tools -----
            hasDurability = register("hasDurability", RuleType.Boolean,
                    ItemStack::isDamageable,
                    RuleVariables::compareBoolean);

            durability = register("durability", RuleType.Percentage,
                    itemStack -> itemStack.getDamage() / itemStack.getMaxDamage(),
                    RuleVariables::comparePercentage);
            damage = register("damage", RuleType.Number,
                    itemStack -> {
                        if(itemStack.getItem() instanceof SwordItem)
                            return ((SwordItem) itemStack.getItem()).getAttackDamage();
                        return 0;
                    },
                    RuleVariables::compareFloat);

            isEnchanted = register("isEnchanted", RuleType.Boolean,
                    itemStack -> {
                        PartialKeepInventory.LOGGER.info("Checking if item " + itemStack.getName().toString() + " has enchantments: " + itemStack.getEnchantments().toString());
                        return !itemStack.getEnchantments().isEmpty();
                    },
                    RuleVariables::compareBoolean);

            isTool = register("isTool", RuleType.Boolean,
                    itemStack -> itemStack.getItem() instanceof ToolItem ,
                    RuleVariables::compareBoolean);

            // ----- Foods -----
            isFood = register("isFood", RuleType.Boolean,
                    ItemStack::isFood,
                    RuleVariables::compareBoolean);
            saturation = register("saturation", RuleType.Float,
                    itemStack -> itemStack.isFood() ? Objects.requireNonNull(itemStack.getItem().getFoodComponent()).getSaturationModifier() : 0,
                    RuleVariables::compareFloat);

            // ----- other items -----
            isStackable = register("isStackable", RuleType.Boolean,
                    ItemStack::isStackable,
                    RuleVariables::compareBoolean);
            stackSize = register("stackSize", RuleType.Number,
                    ItemStack::getMaxCount,
                    RuleVariables::compareInt);
            count = register("count", RuleType.Number,
                    ItemStack::getCount,
                    RuleVariables::compareInt);
            isShulker = register("isShulker", RuleType.Boolean,
                    itemstack -> itemstack.streamTags().anyMatch(tag -> tag.id().equals(new Identifier("c", "shulker_boxes"))),
                    RuleVariables::compareBoolean);
            isMaterial = null;
//            isMaterial = register("isMaterial", RuleType.Boolean,
//                    itemStack -> itemStack.getItem() instanceof ToolMaterial );
            rarity = register("rarity", RuleType.String,
                    itemStack -> itemStack.getRarity().name(),
                    RuleVariables::compareString);
            isPlaceable = register("isPlaceable", RuleType.Boolean,
                    itemStack -> itemStack.getItem() instanceof BlockItem,
                    RuleVariables::compareBoolean);
            hasTag = register("hasTag", RuleType.String,
                    ItemStack::getNbt,
                    (self, comparison, other) -> {
                        boolean result = ((NbtCompound)self).contains( (String)other );
                        if( comparison.equals(RuleComparison.NotEqual))
                            return !result;
                        return result;
                    });

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static boolean compareInt(Object self, RuleComparison comparison, Object other){
        return switch (comparison) {
            case Equal -> (int)self == (int)other;
            case NotEqual -> (int)self != (int)other;
            case LessThan -> (int)self < (int)other;
            case GreaterThan -> (int)self > (int)other;
            case LessEqual -> (int)self <= (int)other;
            case GreaterEqual -> (int)self >= (int)other;
        };
    }

    private static boolean compareBoolean(Object self, RuleComparison comparison, Object other){
        return switch (comparison) {
            case Equal -> (boolean)self == (boolean)other;
            case NotEqual -> (boolean)self != (boolean)other;
            default -> false;
        };
    }

    private static boolean compareString(Object self, RuleComparison comparison, Object other){
        return switch (comparison){
            case Equal -> self.equals(other);
            case NotEqual -> !self.equals(other);
            default -> false;
        };
    }

    private static boolean comparePercentage(Object self, RuleComparison comparison, Object other){
        return compareInt(self, comparison, other);

    }

    private static boolean compareFloat(Object self, RuleComparison comparison, Object other){
        return switch (comparison) {
            case Equal -> (float)self == (float)other;
            case NotEqual -> (float)self != (float)other;
            case LessThan -> (float)self < (float)other;
            case GreaterThan -> (float)self > (float)other;
            case LessEqual -> (float)self <= (float)other;
            case GreaterEqual -> (float)self >= (float)other;
        };
    }




}
