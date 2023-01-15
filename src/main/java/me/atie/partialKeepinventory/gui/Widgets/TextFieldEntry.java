package me.atie.partialKeepinventory.gui.Widgets;

import me.atie.partialKeepinventory.gui.SettingsGUI;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

import java.util.List;


public class TextFieldEntry extends Entry {

    private final TextWidget nameWidget;
    private final TextFieldWidget textFieldWidget;
    private final TextRenderer textRenderer;

    public TextFieldEntry(TextRenderer textRenderer, Text name, int yPos, String text) {
        super(yPos);
        this.textRenderer = textRenderer;

        final int winW = MinecraftClient.getInstance().getWindow().getScaledWidth();
        final int nameWidth = textRenderer.getWidth(name);
        final int textFieldX = Math.max(nameWidth + 2 * SettingsGUI.sideMargin, winW/2);
        final int textFieldWidth = winW - textFieldX - SettingsGUI.sideMargin;


        nameWidget = new TextWidget(SettingsGUI.sideMargin, yPos, nameWidth, SettingsGUI.widgetHeight, name, textRenderer);
        textFieldWidget = new TextFieldWidget(textRenderer, textFieldX, yPos, textFieldWidth, SettingsGUI.widgetHeight, name);
        textFieldWidget.setMaxLength(512);
        textFieldWidget.setText(text);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        if( !this.hidden ) {
            nameWidget.render(matrices, mouseX, mouseY, delta);
            textFieldWidget.render(matrices, mouseX, mouseY, delta);
        }
    }

    @Override
    public int updateY(int y){
        this.nameWidget.setY(y);
        this.textFieldWidget.setY(y);
        return super.updateY(y);
    }

    @Override
    public <T extends Element & Selectable> List<T> getSelectables(){
        return List.of((T) getTextFieldWidget());
    }

    @Override
    public void updateDimensions(int windowWidth) {
        final int nameWidth = textRenderer.getWidth(nameWidget.getMessage());
        final int nameX = SettingsGUI.sideMargin;
        final int textFieldX = Math.max(nameWidth + 2 * SettingsGUI.sideMargin, windowWidth/2);
        final int textFieldWidth = windowWidth - textFieldX - SettingsGUI.sideMargin;

        nameWidget.setWidth(nameWidth);
        nameWidget.setX(nameX);

        textFieldWidget.setWidth(textFieldWidth);
        textFieldWidget.setX(textFieldX);

    }

    public TextFieldWidget getTextFieldWidget() {
        return this.textFieldWidget;
    }

    public String getText(){
        return this.textFieldWidget.getText();
    }

    public static class Builder {
        private int yPos;
        private final TextRenderer textRenderer;
        private Text name;
        private String text;

        public Builder(TextRenderer textRenderer) {
            this.textRenderer = textRenderer;
        }

        public Builder setY(int y) {
            this.yPos = y;
            return this;
        }

        public Builder setName(Text name) {
            this.name = name;
            return this;
        }

        public Builder setText(String s) {
            text = s;
            return this;
        }

        public TextFieldEntry build() {
            return new TextFieldEntry(textRenderer, name, yPos, text);
        }
    }
}



