package me.atie.partialKeepinventory.gui.Widgets;

import me.atie.partialKeepinventory.gui.SettingsGUI;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.util.math.MatrixStack;

import java.util.List;

public abstract class Entry {
    protected int yPos;
    public boolean hidden = false;

    protected Entry(int yPos) {
        this.yPos = yPos;
        this.hidden = false;
    }


    protected int updateY(int y) {
        this.yPos = y;
        return SettingsGUI.nextElementY(y);
    }


    public int getY() {
        return yPos;
    }

    public abstract void render(MatrixStack matrices, int mouseX, int mouseY, float delta);

    protected abstract void updateDimensions(int windowWidth);

    public abstract <T extends Element & Selectable> List<T> getSelectables();
}
