package me.atie.partialKeepinventory.gui;

import me.atie.partialKeepinventory.KeepinvMode;
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
public class InvSettingsScreen extends Screen {
    private final Screen parent;
    private final MinecraftClient client;
    private final pkiSettings LOCAL_CONFIG;

    private TextHeaderEntry generalKeepinvText;
    private ButtonEntry<KeepinvMode> keepinvModeButtonEntry;
    private SliderEntry invPercentSlider;
    private SliderEntry commonPercentSlider;
    private SliderEntry uncommonPercentSlider;
    private SliderEntry rarePercentSlider;
    private SliderEntry epicPercentSlider;
    private TextFieldEntry expressionTextField;
    private final EntryList heading;
    private EntryList entries;



    public InvSettingsScreen(Screen parent, pkiSettings settings, EntryList heading) {
        super(Text.translatable(PartialKeepInventory.getID() + ".gui.screen.inv"));
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
        int yPos = heading.updateY(5);

        heading.hidden = false;

        for( var e: heading.getSelectables()){
            super.addSelectableChild(e);
        }


        try {
            generalKeepinvText = new TextHeaderEntry(textRenderer, Text.translatable(PartialKeepInventory.getID() + ".gui.header.inv").setStyle(Style.EMPTY.withFormatting(Formatting.UNDERLINE)), yPos);

            keepinvModeButtonEntry = new ButtonEntry.Builder<KeepinvMode>(textRenderer)
                    .setName(Text.translatable(PartialKeepInventory.getID() + ".gui.text.partialinvmode"))
                    .toText(KeepinvMode::getName)
                    .setGetter(LOCAL_CONFIG::getPartialKeepinvMode)
                    .setSetter(LOCAL_CONFIG::setPartialKeepinvMode)
                    .nextVal(mode ->{
                        mode = mode.next();
                        setEntryVisibility(mode);
                        return mode;
                    })
                    .build();
            super.addSelectableChild(keepinvModeButtonEntry.getButtonWidget());


            invPercentSlider = new SliderEntry.Builder(textRenderer)
                    .intGetter(LOCAL_CONFIG::getInventoryDroprate)
                    .intSetter(LOCAL_CONFIG::setInventoryDroprate)
                    .toText(SettingsGUI::percentageToText)
                    .setLimits(0, 100)
                    .setName(Text.translatable(PartialKeepInventory.getID() + ".gui.slider.static"))
                    .build();
            super.addSelectableChild(invPercentSlider.getSliderWidget());

            commonPercentSlider = new SliderEntry.Builder(textRenderer)
                    .intGetter(LOCAL_CONFIG::getCommonDroprate)
                    .intSetter(LOCAL_CONFIG::setCommonDroprate)
                    .toText(SettingsGUI::percentageToText)
                    .setLimits(0, 100)
                    .setName(Text.translatable(PartialKeepInventory.getID() + ".gui.slider.common").setStyle(Style.EMPTY.withColor(0xAAAAAA)))
                    .build();
            super.addSelectableChild(commonPercentSlider.getSliderWidget());

            uncommonPercentSlider = new SliderEntry.Builder(textRenderer)
                    .intGetter(LOCAL_CONFIG::getUncommonDroprate)
                    .intSetter(LOCAL_CONFIG::setUncommonDroprate)
                    .toText(SettingsGUI::percentageToText)
                    .setLimits(0, 100)
                    .setName(Text.translatable(PartialKeepInventory.getID() + ".gui.slider.uncommon").setStyle(Style.EMPTY.withColor(0xFFFF55)))
                    .build();
            super.addSelectableChild(uncommonPercentSlider.getSliderWidget());

            rarePercentSlider = new SliderEntry.Builder(textRenderer)
                    .intGetter(LOCAL_CONFIG::getRareDroprate).intSetter(LOCAL_CONFIG::setRareDroprate)
                    .toText(SettingsGUI::percentageToText)
                    .setLimits(0, 100)
                    .setName(Text.translatable(PartialKeepInventory.getID() + ".gui.slider.rare").setStyle(Style.EMPTY.withColor(0x55FFFF)))
                    .build();
            super.addSelectableChild(rarePercentSlider.getSliderWidget());

            epicPercentSlider = new SliderEntry.Builder(textRenderer)
                    .intGetter(LOCAL_CONFIG::getEpicDroprate).intSetter(LOCAL_CONFIG::setEpicDroprate)
                    .toText(SettingsGUI::percentageToText)
                    .setLimits(0, 100)
                    .setName(Text.translatable(PartialKeepInventory.getID() + ".gui.slider.epic").setStyle(Style.EMPTY.withColor(0xFF55FF)))
                    .build();
            super.addSelectableChild(epicPercentSlider.getSliderWidget());

            expressionTextField = new TextFieldEntry.Builder(textRenderer)
                    .setName(Text.translatable(PartialKeepInventory.getID() + ".gui.textfield.invexpression"))
                    .setText(new String(LOCAL_CONFIG.getExpression()))
                    .build();
            super.addSelectableChild(expressionTextField.getTextFieldWidget());



        } catch (Exception e) {
            PartialKeepInventory.LOGGER.error("Failed creating gui: " + e);
        }

        entries = new EntryList(yPos);
        entries.addChildren(Arrays.asList(
//                enableModButtonEntry,

                generalKeepinvText,
                keepinvModeButtonEntry,
                invPercentSlider,
                commonPercentSlider,
                uncommonPercentSlider,
                rarePercentSlider,
                epicPercentSlider,
                new PaddingEntry(5),
                expressionTextField
        ));

        setEntryVisibility(LOCAL_CONFIG.getPartialKeepinvMode());

    }


    @Override
    public void close() {
        LOCAL_CONFIG.setExpression(expressionTextField.getText());
    }

    @Override
    public void resize(MinecraftClient client, int width, int height) {
        this.width = width;
        this.height = height;
        heading.updateDimensions(width);
        entries.updateDimensions(width);
    }

    private void setEntryVisibility(KeepinvMode mode) {
        final Entry[] staticShown = {invPercentSlider};
        final Entry[] rarityShown = {commonPercentSlider, uncommonPercentSlider, rarePercentSlider, epicPercentSlider};
        final Entry[] customShown = {invPercentSlider, commonPercentSlider, uncommonPercentSlider, rarePercentSlider, epicPercentSlider, expressionTextField};
        final Entry[] vanillaShown = {};
        final Entry[] toHide = customShown;

        for(var e: toHide) e.hidden = true;

        final Entry[] toShow =  switch (mode) {
            case STATIC -> staticShown;
            case RARITY -> rarityShown;
            case VANILLA -> vanillaShown;
            case CUSTOM -> customShown;
        };

        for(var e: toShow) e.hidden = false;


        entries.updateY(entries.getY());
    }


}
