package me.atie.partialKeepinventory.gui.Widgets;

import me.atie.partialKeepinventory.gui.SettingsGUI;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public class ButtonSelectionEntry<T> extends Entry {
    private final ButtonWidget[] buttonWidgets;

    private final Consumer<T> onClick;
    private final int buttonMargin;
    private final int xMargin;
    private final int capacity;

    private void onPress(ButtonWidget buttonWidget, T value) {
        for( var button: buttonWidgets) {
            button.active = true;
        }
        buttonWidget.active = false;
        onClick.accept(value);
    }

    public ButtonSelectionEntry(int yPos, int xMargin, int buttonMargin, ArrayList<T> buttonValues, ArrayList<Text> buttonNames, Consumer<T> onClick, int capacity, T defaultVal) {
        super(yPos);

        this.xMargin = xMargin;
        this.buttonMargin = buttonMargin;
        this.capacity = capacity;

        assert MinecraftClient.getInstance().currentScreen != null;
        final int screenWidth = MinecraftClient.getInstance().currentScreen.width;
        final int availableWidth = screenWidth - 2 * xMargin;

        //  buttonWidth = (availableWidth - capacity * buttonMargin) / capacity
        final int buttonWidth = (availableWidth / capacity) - buttonMargin;
        final int buttonXDelta = buttonWidth + buttonMargin;

        this.onClick = onClick;

        buttonWidgets = new ButtonWidget[capacity];

        for( int i = 0; i < capacity; i++ ) {
            final int buttonX = xMargin + i * buttonXDelta;
            final int finalI = i;

            ButtonWidget temp = new ButtonWidget.Builder(buttonNames.get(i), b -> onPress(b, buttonValues.get(finalI)))
                    .dimensions(buttonX, yPos, buttonWidth, SettingsGUI.widgetHeight)
                    .build();

            if( buttonValues.get(i) == defaultVal ){
                temp.active = false;
            }

            buttonWidgets[i] = temp;
        }

    }


    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        for(var e: buttonWidgets){
            e.render(matrices, mouseX, mouseY, delta);
        }
    }

    @Override
    public <T extends Element & Selectable> List<T> getSelectables(){
        return (List<T>) Arrays.stream(buttonWidgets).toList();
    }

    @Override
    public int updateY(int y){
        for( var e: buttonWidgets ) {
            e.setY(y);
        }
        return super.updateY(y);
    }

    @Override
    public void updateDimensions(int windowWidth) {
        final int availableWidth = windowWidth - 2 * xMargin;

        //  buttonWidth = (availableWidth - capacity * buttonMargin) / capacity
        final int buttonWidth = (availableWidth / capacity) - buttonMargin;
        final int buttonXDelta = buttonWidth + buttonMargin;

        for (int i = 0; i < capacity; i++) {
            final int buttonX = xMargin + i * buttonXDelta;

            buttonWidgets[i].setWidth(buttonWidth);
            buttonWidgets[i].setX(buttonX);
        }
    }


    public static class Builder<T> {
        private final ArrayList<Text> names;
        private final ArrayList<T> values;
        private Consumer<T> onClick;

        private int xMargin;
        private int yPos;
        private int buttonMargin;
        private int buttonCount = 0;

        public Builder(){
            names = new ArrayList<>();
            values = new ArrayList<>();
        }

        public Builder(int buttonCount){
            names = new ArrayList<>(buttonCount);
            values = new ArrayList<>(buttonCount);
        }

        public Builder<T> addButton(Text name, T value){
            names.add(name);
            values.add(value);
            buttonCount += 1;
            return this;
        }

        public Builder<T> setY(int y){
            yPos = y;
            return this;
        }

        public Builder<T> setXMargin(int margin){
            xMargin = margin;
            return this;
        }



        public Builder<T> setButtonMargin(int margin){
            buttonMargin = margin;
            return this;
        }

        public Builder<T> onClick(Consumer<T> onClick) {
            this.onClick = onClick;
            return this;
        }

        public ButtonSelectionEntry<T> build() {
            return new ButtonSelectionEntry<T>(yPos, xMargin, buttonMargin, values, names, onClick, buttonCount, values.get(0));
        }

        public ButtonSelectionEntry<T> build(T defaultval) {
            return new ButtonSelectionEntry<T>(yPos, xMargin, buttonMargin, values, names, onClick, buttonCount, defaultval);
        }

    }

}
