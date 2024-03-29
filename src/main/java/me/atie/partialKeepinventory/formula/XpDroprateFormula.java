package me.atie.partialKeepinventory.formula;

import net.minecraft.server.network.ServerPlayerEntity;

public class XpDroprateFormula extends DroprateFormula {

    private int xpAmount;
    private int xpLevel;

    public XpDroprateFormula(ServerPlayerEntity player) {
        super(player);
    }


    @Override
    public void initDummyEnvVariables() {
        super.initDummyEnvVariables();
        env.addLazyVariable("xpPoints", () -> 0.0);
        env.addLazyVariable("xpLevel", () -> 0.0);
    }

    @Override
    public void initEnvVariables() {
        super.initEnvVariables();
        env.addLazyVariable("xpPoints", this::getXpAmount);
        env.addLazyVariable("xpLevel", this::getXpLevel);
    }



    public double getResult( int xpLevel, int xpAmount) {
        this.xpAmount = xpAmount;
        this.xpLevel = xpLevel;

        return super.getResult();
    }

    int getXpAmount(){
        return xpAmount;
    }
    int getXpLevel(){
        return xpLevel;
    }



}
