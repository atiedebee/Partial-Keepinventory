package me.atie.partialKeepinventory.rules;

import java.util.Optional;

public enum RuleComparison {
    Equal, NotEqual, LessThan, GreaterThan, LessEqual, GreaterEqual;

    public String toString(){
        String[] s = {
            "==", "!=", "<", ">", "<=", ">="
        };
        return s[this.ordinal()];
    }

    public static Optional<RuleComparison> fromString(String s){
        return switch(s.toLowerCase()){
            case "eq", "==" -> Optional.of(Equal);
            case "ne", "!=" -> Optional.of(NotEqual);
            case "lt", "<" -> Optional.of(LessThan);
            case "gt", ">" -> Optional.of(GreaterThan);
            case "le", "<=" -> Optional.of(LessEqual);
            case "ge", ">=" -> Optional.of(GreaterEqual);
            default -> Optional.empty();
        };
    }

    public String toArgumentString(){
        String[] s = {
                "eq", "ne", "lt", "gt", "le", "ge"
        };
        return s[this.ordinal()];
    }


}
