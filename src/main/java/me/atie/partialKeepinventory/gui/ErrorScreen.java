package me.atie.partialKeepinventory.gui;

import me.atie.partialKeepinventory.PartialKeepInventory;
import me.atie.partialKeepinventory.gui.Widgets.TextHeaderEntry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class ErrorScreen extends Screen {
    private final Screen parent;
    private final MinecraftClient client;
    private TextHeaderEntry errorMessage;

    protected ErrorScreen(Screen parent) {
        super(Text.translatable(PartialKeepInventory.getID() + ".gui.screen.error"));
        this.parent = parent;
        this.client = MinecraftClient.getInstance();
    }

    @Override
    public void init() {
        errorMessage = new TextHeaderEntry(textRenderer,
                Text.translatable(PartialKeepInventory.getID() + ".gui.error.server_req"),
                client.getWindow().getScaledHeight() / 2);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);

        errorMessage.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    public void close() {
        parent.close();
    }

}