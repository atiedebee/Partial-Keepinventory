package me.atie.partialKeepinventory.gui.Widgets;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

import java.util.List;

//TODO maybe: some container based widget system, or use callbacks to functions to resize items
@Environment(EnvType.CLIENT)
public class SimpleButton extends Entry {
    private final ButtonWidget buttonWidget;
    ButtonWidget.PressAction onResize;

    public SimpleButton(int x, int y, int w, int h, Text name, Text tooltip, ButtonWidget.PressAction onClick, ButtonWidget.PressAction onResize) {
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
    public void render(DrawContext matrices, int mouseX, int mouseY, float delta) {
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
        if( this.onResize != null ) {
            this.onResize.onPress(this.buttonWidget);
        }
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