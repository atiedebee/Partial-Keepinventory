package me.atie.partialKeepinventory.gui;

import me.atie.partialKeepinventory.KeepXPMode;
import me.atie.partialKeepinventory.PartialKeepInventory;
import me.atie.partialKeepinventory.component.pkiSettings;
import me.atie.partialKeepinventory.gui.Widgets.*;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.Arrays;

@Environment(EnvType.CLIENT)
public class XpSettingsScreen extends Screen {
    private final Screen parent;
    private final MinecraftClient client;
    private final pkiSettings LOCAL_CONFIG;

    private TextHeaderEntry xpTextHeaderEntry;
    private ButtonEntry<KeepXPMode> keepXPModeButtonEntry;
    private SliderEntry DropSlider;
    private SliderEntry LossSlider;

    private final EntryList heading;
    private EntryList entries;



    public XpSettingsScreen(Screen parent, pkiSettings settings, EntryList heading) {
        super(Text.translatable(PartialKeepInventory.getID() + ".gui.screen.xp"));
        this.parent = parent;
        this.client = MinecraftClient.getInstance();
        this.LOCAL_CONFIG = settings;
        this.heading = heading;
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);

        heading.render(matrices, mouseX, mouseY, delta);
        entries.render(matrices, mouseX, mouseY, delta);
    }


    @Override
    protected void init() {

        assert (client != null);

        for( var e: heading.getSelectables()){
            super.addSelectableChild(e);
        }

        int yPos = heading.updateY(5);

        try {
            xpTextHeaderEntry = new TextHeaderEntry(textRenderer, Text.translatable(PartialKeepInventory.getID() + ".gui.header.xp").setStyle(Style.EMPTY.withFormatting(Formatting.UNDERLINE)), yPos);

            keepXPModeButtonEntry = new ButtonEntry.Builder<KeepXPMode>(textRenderer)
                    .setName(Text.translatable(PartialKeepInventory.getID() + ".gui.text.partialxpmode"))
                    .toText(KeepXPMode::getName)
                    .setGetter(LOCAL_CONFIG::getKeepxpMode)
                    .setSetter(s -> {
                        setEntryVisibility(s);
                        LOCAL_CONFIG.setKeepxpMode(s);
                    })
                    .nextVal(KeepXPMode::next)
                    .build();
            super.addSelectableChild(keepXPModeButtonEntry.getButtonWidget());


            DropSlider = new SliderEntry.Builder(textRenderer)
                    .intGetter(LOCAL_CONFIG::getXpDrop)
                    .intSetter(LOCAL_CONFIG::setXpDrop)
                    .toText(SettingsGUI::percentageToText)
                    .setLimits(0, 100)
                    .setName(Text.translatable(PartialKeepInventory.getID() + ".gui.slider.xpdrop"))
                    .build();
            super.addSelectableChild(DropSlider.getSliderWidget());

            LossSlider = new SliderEntry.Builder(textRenderer)
                    .intGetter(LOCAL_CONFIG::getXpLoss)
                    .intSetter(LOCAL_CONFIG::setXpLoss)
                    .toText(SettingsGUI::percentageToText)
                    .setLimits(0, 100)
                    .setName(Text.translatable(PartialKeepInventory.getID() + ".gui.slider.xploss"))
                    .build();
            super.addSelectableChild(LossSlider.getSliderWidget());


        } catch (Exception e) {
            PartialKeepInventory.LOGGER.error("Failed creating gui: " + e);
        }

        entries = new EntryList(yPos);
        entries.addChildren(Arrays.asList(
                xpTextHeaderEntry,
                keepXPModeButtonEntry,
                DropSlider,
                LossSlider
        ));
        setEntryVisibility(LOCAL_CONFIG.getKeepxpMode());

        entries.updateY(yPos);

    }


    @Override
    public void close() {

    }

    @Override
    public void resize(MinecraftClient client, int width, int height) {
        this.width = width;
        this.height = height;
        heading.updateDimensions(width);
        entries.updateDimensions(width);
    }

    public void setEntryVisibility(KeepXPMode mode) {
        final Entry[] toHide = {DropSlider, LossSlider};
        final Entry[] STATIC_POINTS = {DropSlider, LossSlider};
        final Entry[] STATIC_LEVELS = {DropSlider, LossSlider};
        final Entry[] VANILLA = {};

        for( var e: toHide ){ e.hidden = true; }

        final Entry[] shown = switch(mode){
            case STATIC_LEVELS -> STATIC_LEVELS;
            case STATIC_POINTS -> STATIC_POINTS;
            case VANILLA -> VANILLA;
        };

        for( var e: shown ){ e.hidden = false; }
        entries.updateY(entries.getY());
    }

}
