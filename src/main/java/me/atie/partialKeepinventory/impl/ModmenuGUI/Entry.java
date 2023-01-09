package me.atie.partialKeepinventory.impl.ModmenuGUI;


import net.minecraft.client.util.math.MatrixStack;

public interface Entry {
    int updateY(int y);
    void render(MatrixStack matrices, int mouseX, int mouseY, float delta);
}
