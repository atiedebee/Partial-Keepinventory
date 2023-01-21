package me.atie.partialKeepinventory.gui.Widgets;

import me.atie.partialKeepinventory.PartialKeepInventory;
import me.atie.partialKeepinventory.gui.SettingsGUI;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

import java.util.List;

public class SimpleText extends Entry {
    private final TextRenderer textRenderer;
    private final TextWidget textWidget;

    public SimpleText(TextRenderer textRenderer, Text text, int yPos) {
        super(yPos);
        this.textRenderer = textRenderer;

        PartialKeepInventory.LOGGER.info(text.getContent().toString());
        PartialKeepInventory.LOGGER.info(text.getString());
        this.textWidget = new TextWidget(text, textRenderer);
        textWidget.setPos(SettingsGUI.sideMargin, yPos);
    }

    @Override
    public int getHeight(){
        return height;
    }

    @Override
    public int updateY(int y) {
        textWidget.setY(y);
        return super.updateY(y);
    }


    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        if( !hidden ) {
            textWidget.render(matrices, mouseX, mouseY, delta);
        }
    }

    @Override
    protected void updateDimensions(int windowWidth) {
        final int maxWidth = windowWidth - 2 * SettingsGUI.sideMargin;
        height = textRenderer.getWrappedLinesHeight(textWidget.getMessage(), maxWidth);
        textWidget.setX(SettingsGUI.sideMargin);

        final int messageWidth = textRenderer.getWidth(textWidget.getMessage());

        textWidget.setWidth(Math.min(messageWidth, maxWidth));
    }

    @Override
    public <T extends Element & Selectable> List<T> getSelectables() {
        return (List<T>) List.of(textWidget);
    }
}
