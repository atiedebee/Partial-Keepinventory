package me.atie.partialKeepinventory.gui.Widgets;

import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.util.math.MatrixStack;

import java.util.List;

public class PaddingEntry extends Entry {

    private final int padding;

    public PaddingEntry(int padding) {
        super(0);
        this.padding = padding;
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
    }

    @Override
    public <T extends Element & Selectable> List<T> getSelectables(){
        return null;
    }


    @Override
    public int updateY(int y) {
        yPos = y + padding;
        return yPos;
    }

    @Override
    public void updateDimensions(int windowWidth){

    }
}
