package me.atie.partialKeepinventory.rules;

public enum RuleType {
    Boolean, Number, Percentage, String, Float;

    public boolean canBeComparedUsing(RuleComparison comparison){
        // Equal, NotEqual, LessThan, GreaterThan, LessEqual, GreaterEqual
        boolean[][] comparisons = {
            {// Boolean
                true, true, false, false, false, false
            },
            {// Number
                true, true, true, true, true, true
            },
            {// Percentage
                true, true, true, true, true, true
            },
            {// String
                true, true, false, false, false, false
            },
            {// Float
                true, true, true, true, true, true
            },
        };

        return comparisons[ this.ordinal() ][ comparison.ordinal() ];
    }


}
