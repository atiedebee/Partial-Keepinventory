package me.atie.partialKeepinventory.gui.Widgets;

import me.atie.partialKeepinventory.gui.SettingsGUI;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.util.math.MatrixStack;

import java.util.List;
import java.util.function.Supplier;

public abstract class Entry {
    protected int yPos;
    protected int height = SettingsGUI.widgetHeight;
    public boolean hidden = false;
    protected Supplier<Integer> getScrollDistance;

    protected Entry(int yPos) {
        this.yPos = yPos;
        this.hidden = false;
    }

    public void hide(){
        hidden = true;
    }

    public void show(){
        hidden = false;
    }


    protected int updateY(int y) {
        this.yPos = y;
        return SettingsGUI.nextElementY(y);
    }


    public int getY() {
        return yPos;
    }
    public int getHeight() {
        return height;
    }
    public void setScrollDistanceGetter(Supplier<Integer> supplier){
        this.getScrollDistance = supplier;
    }

    public abstract void render(MatrixStack matrices, int mouseX, int mouseY, float delta);

    protected abstract void updateDimensions(int windowWidth);

    public abstract <T extends Element & Selectable> List<T> getSelectables();

}
