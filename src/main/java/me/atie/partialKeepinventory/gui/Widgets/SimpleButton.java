package me.atie.partialKeepinventory.gui.Widgets;

import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

import java.util.List;

public class SimpleButton<T> extends Entry {
    private ButtonWidget buttonWidget;

    public SimpleButton(int x, int y, int w, int h, Text name, ButtonWidget.PressAction onClick) {
        super(y);


        buttonWidget = new ButtonWidget.Builder(name, onClick)
                .dimensions(x, y, w, h)
                .build();
        buttonWidget.setMessage(name);

    }

    @Override
    public int updateY(int y) {
        buttonWidget.setY(y);
        return super.updateY(y);
    }
    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        if (!hidden) {
            buttonWidget.render(matrices, mouseX, mouseY, delta);
        }
    }

    @Override
    public <T extends Element & Selectable> List<T> getSelectables() {
        return (List<T>) List.of(buttonWidget);
    }

    @Override
    public void updateDimensions(int windowWidth) {
        final int width = windowWidth/2;
        final int x = windowWidth/4;
        buttonWidget.setX(x);
        buttonWidget.setWidth(width);
    }

}