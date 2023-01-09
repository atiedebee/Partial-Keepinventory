package me.atie.partialKeepinventory.impl.ModmenuGUI;

import me.atie.partialKeepinventory.impl.SettingsGUI;
import net.minecraft.client.util.math.MatrixStack;

public class EntryImpl implements Entry {
    protected int yPos;
    public boolean hidden = false;

    public EntryImpl(int yPos) {
        this.yPos = yPos;
    }

    @Override
    public int updateY(int y) {
        this.yPos = y;
        return SettingsGUI.nextElementY(y);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {

    }

    public int getY() {
        return yPos;
    }

}
