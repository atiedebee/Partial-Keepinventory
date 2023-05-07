package me.atie.partialKeepinventory.impl.trinkets;

public enum KeepTrinketMode {
    DEFAULT, STATIC, RARITY, CHANCE;

    KeepTrinketMode next(){
        return switch(this){
            case DEFAULT -> STATIC;
            case STATIC -> RARITY;
            case RARITY -> CHANCE;
            case CHANCE -> DEFAULT;
        };
    }

    @Override
    public String toString() {
        return switch(this){
            case DEFAULT -> "Default";
            case STATIC -> "Static";
            case RARITY -> "Rarity";
            case CHANCE -> "Chance";
        };
    }
}
