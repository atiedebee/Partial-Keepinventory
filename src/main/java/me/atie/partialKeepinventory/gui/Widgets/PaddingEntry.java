package me.atie.partialKeepinventory.gui.Widgets;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.util.math.MatrixStack;

import java.util.List;

@Environment(EnvType.CLIENT)
public class PaddingEntry extends Entry {

    public PaddingEntry(int padding) {
        super(0);
        this.height = padding;
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
        yPos = y + height;
        return yPos;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public void updateDimensions(int windowWidth){

    }
}
