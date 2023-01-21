package me.atie.partialKeepinventory.gui.Widgets;

import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

import java.util.List;

public class SimpleButton extends Entry {
    private final ButtonWidget buttonWidget;

    public SimpleButton(int x, int y, int w, int h, Text name, Text tooltip, ButtonWidget.PressAction onClick) {
        super(y);


        buttonWidget = new ButtonWidget.Builder(name, onClick)
                .dimensions(x, y, w, h)
                .build();
        buttonWidget.setMessage(name);
        if( tooltip != null ) {
            buttonWidget.setTooltip(Tooltip.of(tooltip));
        }
    }

    public ButtonWidget getButtonWidget(){
        return buttonWidget;
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

    @Override
    public void show(){
        super.show();
        buttonWidget.active = true;
    }

    @Override
    public void hide(){
        super.hide();
        buttonWidget.active = false;
    }

}