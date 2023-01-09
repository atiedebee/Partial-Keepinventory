package me.atie.partialKeepinventory.impl.ModmenuGUI;

import me.atie.partialKeepinventory.PartialKeepInventory;
import me.atie.partialKeepinventory.impl.SettingsGUI;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

public class TextHeaderEntry extends EntryImpl implements Entry {
    private final TextWidget textWidget;

    public TextHeaderEntry(TextRenderer textRenderer, Text name, int yPos) {
        super(yPos);
        int winW = MinecraftClient.getInstance().getWindow().getScaledWidth();

        int width = winW - 2 * SettingsGUI.sideMargin;
        int x = SettingsGUI.sideMargin;

        textWidget = new TextWidget(x, yPos, width, 20, name, textRenderer);
    }

    @Override
    public int updateY(int y) {
        textWidget.setY(y);
        PartialKeepInventory.LOGGER.info("Updated textheader Y to: " + y);
        return super.updateY(y);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        if(!hidden) {
            textWidget.render(matrices, mouseX, mouseY, delta);
        }
    }

    public TextWidget getTextWidget() {
        return textWidget;
    }


}
