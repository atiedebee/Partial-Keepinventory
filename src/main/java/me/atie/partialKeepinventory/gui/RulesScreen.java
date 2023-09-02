package me.atie.partialKeepinventory.gui;


import me.atie.partialKeepinventory.gui.Widgets.EntryList;
import me.atie.partialKeepinventory.gui.Widgets.TextHeaderEntry;
import me.atie.partialKeepinventory.settings.pkiSettings;
import me.atie.partialKeepinventory.text.GuiText;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;

public class RulesScreen extends Screen {
    private final Screen parent;
    private final pkiSettings LOCAL_CONFIG;
    private final EntryList heading;
    private TextHeaderEntry title;

    protected RulesScreen(Screen parent, pkiSettings settings, EntryList heading) {
        super(GuiText.ruleScreen.title);
        this.parent = parent;
        this.LOCAL_CONFIG = settings;
        this.heading = heading;
    }

    @Override
    public void render(DrawContext matrices, int mouseX, int mouseY, float delta){
        this.renderBackground(matrices);

        heading.render(matrices, mouseX, mouseY, delta);
        title.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    public void init(){
        int yPos = heading.updateY(heading.getY());
        for( var s: heading.getSelectables() ) this.addSelectableChild(s);

        title = new TextHeaderEntry(textRenderer, GuiText.ruleScreen.title, yPos);
        yPos = title.updateY(yPos);

    }

    @Override
    public void resize(MinecraftClient client, int width, int height) {
        this.width = width;
        this.height = height;
        heading.updateDimensions(width);
        title.updateDimensions(width);
    }




}