package me.atie.partialKeepinventory.gui.Widgets;

import me.atie.partialKeepinventory.gui.SettingsGUI;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

import java.util.List;

/**
 * List of entries that may be collapsed
 */
public class CollapsableEntryList extends EntryList {
    private final EntryList parent;
    private final ButtonWidgetEntry buttonWidgetEntry;
    private boolean collapsed;


    public CollapsableEntryList(Text name,  EntryList parent, int y) {
        this(name, parent, false, y);
    }

    public CollapsableEntryList(Text name,  EntryList parent, boolean collapsed, int y) {
        super(y);
        this.collapsed = collapsed;
        this.parent = parent;

        int buttonWidth = MinecraftClient.getInstance().getWindow().getWidth() - 2 * SettingsGUI.sideMargin;

        buttonWidgetEntry = new ButtonWidgetEntry(new ButtonWidget.Builder(name, this::collapse)
                .dimensions(SettingsGUI.sideMargin, y, buttonWidth, SettingsGUI.widgetHeight)
                .build());
        children.add(buttonWidgetEntry);
    }

    @Override
    public int updateY(int y) {
        super.yPos = y;

        if( !collapsed ){
            y = super.updateY(y);
        }
        else{
            y = buttonWidgetEntry.updateY(yPos);
        }
        return y;
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        if(!hidden) {
            if( !collapsed ){
                super.render(matrices, mouseX, mouseY, delta);
            }
            else {
                buttonWidgetEntry.render(matrices, mouseX, mouseY, delta);
            }
        }
    }


    private void collapse(ButtonWidget buttonWidget) {
        collapsed = !collapsed;
        parent.updateY(parent.getY());
    }

    public ButtonWidget getButtonWidget(){
        return buttonWidgetEntry.getButtonWidget();
    }


    private static class ButtonWidgetEntry extends Entry {
        private final ButtonWidget buttonWidget;
        public ButtonWidgetEntry(ButtonWidget buttonWidget) {
            super(buttonWidget.getY());
            this.buttonWidget = buttonWidget;
        }

        public ButtonWidgetEntry(int yPos, ButtonWidget buttonWidget) {
            super(yPos);
            this.buttonWidget = buttonWidget;
        }

        public ButtonWidget getButtonWidget(){ return this.buttonWidget; }

        @Override
        public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
            buttonWidget.render(matrices, mouseX, mouseY, delta);
        }

        @Override
        public <T extends Element & Selectable> List<T> getSelectables(){
            return List.of((T)buttonWidget);
        }

        @Override
        public int getY() {
            return super.getY();
        }

        @Override
        public int updateY(int y) {
            this.buttonWidget.setY(y);
            return super.updateY(y);
        }

        @Override
        public void updateDimensions(int windowWidth){

        }

        public void setY(int y){
            this.yPos = y;
        }
    }


}
