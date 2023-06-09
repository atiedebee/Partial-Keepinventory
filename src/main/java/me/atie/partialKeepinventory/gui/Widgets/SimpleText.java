package me.atie.partialKeepinventory.gui.Widgets;

import me.atie.partialKeepinventory.gui.ParentSettingsScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.text.Text;

import java.util.List;

@Environment(EnvType.CLIENT)
public class SimpleText extends Entry {
    private final TextRenderer textRenderer;
    private final TextWidget[] textWidgets;


    public SimpleText(TextRenderer textRenderer, Text text, int yPos) {
        super(yPos);
        this.textRenderer = textRenderer;


        List<String> lines = text.getString().lines().toList();
        int lineCount = lines.size();
//        PartialKeepInventory.LOGGER.info(text.getContent().toString());
//        PartialKeepInventory.LOGGER.info(text.getString());
        textWidgets = new TextWidget[lineCount];
        for( int i = 0; i < lineCount; i++ ){
            textWidgets[i] = new TextWidget(Text.literal(lines.get(i)), textRenderer);
            textWidgets[i].setPosition(ParentSettingsScreen.sideMargin, yPos);
            yPos += textRenderer.fontHeight + 2;
        }
        this.height = yPos - this.yPos;

    }

    @Override
    public int getHeight(){
        return height;
    }

    @Override
    public int updateY(int y) {
        yPos = y;
        for (TextWidget textWidget : textWidgets) {
            textWidget.setY(y);
            y += textRenderer.fontHeight + 2;
        }
        return y;
    }


    @Override
    public void render(DrawContext matrices, int mouseX, int mouseY, float delta) {
        if( !hidden ) {
            for( var w: textWidgets ) {
                w.render(matrices, mouseX, mouseY, delta);
            }
        }
    }

    @Override
    protected void updateDimensions(int windowWidth) {
        final int maxWidth = windowWidth - 2 * ParentSettingsScreen.sideMargin;
        for( var w: textWidgets ) {
            height = textRenderer.getWrappedLinesHeight(w.getMessage(), maxWidth);
            w.setX(ParentSettingsScreen.sideMargin);

            final int messageWidth = textRenderer.getWidth(w.getMessage());

            w.setWidth(Math.min(messageWidth, maxWidth));
        }
    }

    @Override
    public <T extends Element & Selectable> List<T> getSelectables() {
        return (List<T>) List.of(textWidgets);
    }
}
