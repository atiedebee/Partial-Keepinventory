package me.atie.partialKeepinventory.gui.Widgets;

import me.atie.partialKeepinventory.gui.ParentSettingsScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.text.Text;

import java.util.List;


@Environment(EnvType.CLIENT)
public class TextFieldEntry extends Entry {

    private final TextWidget nameWidget;
    private final TextFieldWidget textFieldWidget;
    private final TextRenderer textRenderer;

    public TextFieldEntry(TextRenderer textRenderer, Text name, Text tooltip, int yPos, String text) {
        super(yPos);
        this.textRenderer = textRenderer;

        final int winW = MinecraftClient.getInstance().getWindow().getScaledWidth();
        final int nameWidth = textRenderer.getWidth(name);
        final int textFieldX = Math.max(nameWidth + 2 * ParentSettingsScreen.sideMargin, winW/2);
        final int textFieldWidth = winW - textFieldX - ParentSettingsScreen.sideMargin;


        nameWidget = new TextWidget(ParentSettingsScreen.sideMargin, yPos, nameWidth, ParentSettingsScreen.widgetHeight, name, textRenderer);
        if(tooltip != null) {
            nameWidget.setTooltip(Tooltip.of(tooltip));
        }

        textFieldWidget = new TextFieldWidget(textRenderer, textFieldX, yPos, textFieldWidth, ParentSettingsScreen.widgetHeight, name);
        textFieldWidget.setMaxLength(512);
        textFieldWidget.setText(text);
    }

    @Override
    public void render(DrawContext matrices, int mouseX, int mouseY, float delta) {
        if( !hidden ) {
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
        final int nameX = ParentSettingsScreen.sideMargin;
        final int textFieldX = Math.max(nameWidth + 2 * ParentSettingsScreen.sideMargin, windowWidth/2);
        final int textFieldWidth = windowWidth - textFieldX - ParentSettingsScreen.sideMargin;

        nameWidget.setWidth(nameWidth);
        nameWidget.setX(nameX);

        textFieldWidget.setWidth(textFieldWidth);
        textFieldWidget.setX(textFieldX);

    }

    @Override
    public void show(){
        super.show();
        textFieldWidget.setEditable(true);
    }

    @Override
    public void hide(){
        super.hide();
        textFieldWidget.setEditable(false);
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
        private Text tooltip;
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

        public Builder setTooltip(Text tooltipText) {
            this.tooltip = tooltipText;
            return this;
        }
        public TextFieldEntry build() {
            return new TextFieldEntry(textRenderer, name, tooltip, yPos, text);
        }
    }
}



