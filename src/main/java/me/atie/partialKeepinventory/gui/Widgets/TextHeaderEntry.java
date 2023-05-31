package me.atie.partialKeepinventory.gui.Widgets;

import me.atie.partialKeepinventory.gui.ParentSettingsScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

import java.util.List;

@Environment(EnvType.CLIENT)
public class TextHeaderEntry extends Entry {
    private final TextWidget textWidget;

    public TextHeaderEntry(TextRenderer textRenderer, Text name, int yPos) {
        super(yPos);
        int winW = MinecraftClient.getInstance().getWindow().getScaledWidth();

        int width = winW - 2 * ParentSettingsScreen.sideMargin;
        int x = ParentSettingsScreen.sideMargin;

        textWidget = new TextWidget(x, yPos, width, 20, name, textRenderer);
    }

    @Override
    public int updateY(int y) {
        textWidget.setY(y);
        return super.updateY(y);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        if(!hidden) {
            textWidget.render(matrices, mouseX, mouseY, delta);
        }
    }

    @Override
    public <T extends Element & Selectable> List<T> getSelectables(){
        return null;
    }


    @Override
    public void updateDimensions(int windowWidth) {
        int width = windowWidth - 2 * ParentSettingsScreen.sideMargin;
        int x = ParentSettingsScreen.sideMargin;

        textWidget.setPosition(x, yPos);
        textWidget.setWidth(width);
    }

    public TextWidget getTextWidget() {
        return textWidget;
    }


}
