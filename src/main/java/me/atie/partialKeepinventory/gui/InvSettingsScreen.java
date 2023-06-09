package me.atie.partialKeepinventory.gui;

import me.atie.partialKeepinventory.KeepinvMode;
import me.atie.partialKeepinventory.PartialKeepInventory;
import me.atie.partialKeepinventory.formula.InventoryDroprateFormula;
import me.atie.partialKeepinventory.gui.Widgets.*;
import me.atie.partialKeepinventory.settings.pkiSettings;
import me.atie.partialKeepinventory.text.GuiText;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.Arrays;

@Environment(EnvType.CLIENT)
public class InvSettingsScreen extends Screen {
    private final MinecraftClient client;
    private final pkiSettings LOCAL_CONFIG;
    private final Screen parent;

    private TextHeaderEntry generalKeepinvText;
    private ButtonEntry<KeepinvMode> keepinvModeButtonEntry;
    private SliderEntry invPercentSlider;
    private SliderEntry commonPercentSlider;
    private SliderEntry uncommonPercentSlider;
    private SliderEntry rarePercentSlider;
    private SliderEntry epicPercentSlider;
    private final EntryList heading;
    private EntryList entries;
    private SimpleButton footing;


    private final boolean canEditValues;


    public InvSettingsScreen(Screen parent, pkiSettings settings, EntryList heading) {
        super(GuiText.invScreen.name);
        this.client = MinecraftClient.getInstance();
        this.LOCAL_CONFIG = settings;
        this.heading = heading;
        assert client.player != null;
        canEditValues = client.player.hasPermissionLevel(2);
        this.parent = parent;
    }

    @Override
    public void render(DrawContext matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);

        heading.render(matrices, mouseX, mouseY, delta);
        generalKeepinvText.render(matrices, mouseX, mouseY, delta);
        entries.render(matrices, mouseX, mouseY, delta);
        footing.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    protected <T extends Element & Selectable> T addSelectableChild(T child) {
        if(canEditValues)
            return super.addSelectableChild(child);
        return null;
    }

    @Override
    protected void init() {

        assert (client != null);
        int yPos = heading.updateY(ParentSettingsScreen.vertOptionMargin);

        heading.hidden = false;

        for( var e: heading.getSelectables()){
            this.addSelectableChild(e);
        }

        for( var e: heading.getChildren().get(1).getSelectables() ){
            super.addSelectableChild(e);
        }

        generalKeepinvText = new TextHeaderEntry(textRenderer, GuiText.invScreen.header.copy().setStyle(Style.EMPTY.withFormatting(Formatting.UNDERLINE)), yPos);
        yPos = generalKeepinvText.updateY(yPos);

        footing = new SimpleButton(width - ParentSettingsScreen.sideMargin, height - ParentSettingsScreen.vertOptionMargin - ParentSettingsScreen.widgetHeight,
                ParentSettingsScreen.widgetHeight, ParentSettingsScreen.widgetHeight, Text.literal(String.format("%c", 0x2193)), null,
                this::changePage, null);
        this.addSelectableChild(footing.getSelectables().get(0));

        initOptions(yPos);
    }

    private void initOptions(int yPos){
        entries = new EntryList(yPos);

        try {

            keepinvModeButtonEntry = new ButtonEntry.Builder<KeepinvMode>(textRenderer)
                    .setName(GuiText.invScreen.text_invMode)
                    .setTooltip(GuiText.invScreen.tooltip_invMode)
                    .toText(KeepinvMode::getName)
                    .setGetter(LOCAL_CONFIG::getPartialKeepinvMode)
                    .setSetter(LOCAL_CONFIG::setPartialKeepinvMode)
                    .nextVal(mode ->{
                        mode = mode.next();
                        setEntryVisibility(mode);
                        return mode;
                    })
                    .build();
            this.addSelectableChild(keepinvModeButtonEntry.getButtonWidget());


            invPercentSlider = new SliderEntry.Builder(textRenderer)
                    .intGetter(LOCAL_CONFIG::getInventoryDroprate)
                    .intSetter(LOCAL_CONFIG::setInventoryDroprate)
                    .toText(ParentSettingsScreen::percentageToText)
                    .setLimits(0, 100)
                    .setName(GuiText.invScreen.slider_static)
                    .setTooltip(GuiText.invScreen.tooltip_static)
                    .build();
            this.addSelectableChild(invPercentSlider.getSliderWidget());

            commonPercentSlider = new SliderEntry.Builder(textRenderer)
                    .intGetter(LOCAL_CONFIG::getCommonDroprate)
                    .intSetter(LOCAL_CONFIG::setCommonDroprate)
                    .toText(ParentSettingsScreen::percentageToText)
                    .setLimits(0, 100)
                    .setName(GuiText.invScreen.slider_common.copy().setStyle(Style.EMPTY.withColor(0xAAAAAA)))
                    .setTooltip(GuiText.invScreen.tooltip_common)
                    .build();
            this.addSelectableChild(commonPercentSlider.getSliderWidget());

            uncommonPercentSlider = new SliderEntry.Builder(textRenderer)
                    .intGetter(LOCAL_CONFIG::getUncommonDroprate)
                    .intSetter(LOCAL_CONFIG::setUncommonDroprate)
                    .toText(ParentSettingsScreen::percentageToText)
                    .setLimits(0, 100)
                    .setName(GuiText.invScreen.slider_uncommon.copy().setStyle(Style.EMPTY.withColor(0xFFFF55)))
                    .setTooltip(GuiText.invScreen.tooltip_uncommon)
                    .build();
            this.addSelectableChild(uncommonPercentSlider.getSliderWidget());

            rarePercentSlider = new SliderEntry.Builder(textRenderer)
                    .intGetter(LOCAL_CONFIG::getRareDroprate).intSetter(LOCAL_CONFIG::setRareDroprate)
                    .toText(ParentSettingsScreen::percentageToText)
                    .setLimits(0, 100)
                    .setName(GuiText.invScreen.slider_rare.copy().setStyle(Style.EMPTY.withColor(0x55FFFF)))
                    .setTooltip(GuiText.invScreen.tooltip_rare)
                    .build();
            this.addSelectableChild(rarePercentSlider.getSliderWidget());

            epicPercentSlider = new SliderEntry.Builder(textRenderer)
                    .intGetter(LOCAL_CONFIG::getEpicDroprate).intSetter(LOCAL_CONFIG::setEpicDroprate)
                    .toText(ParentSettingsScreen::percentageToText)
                    .setLimits(0, 100)
                    .setName(GuiText.invScreen.slider_epic.copy().setStyle(Style.EMPTY.withColor(0xFF55FF)))
                    .setTooltip(GuiText.invScreen.tooltip_epic)
                    .build();
            this.addSelectableChild(epicPercentSlider.getSliderWidget());



        } catch (Exception e) {
            PartialKeepInventory.LOGGER.error("Failed creating gui: " + e);
        }

        entries.addChildren(Arrays.asList(
                keepinvModeButtonEntry,
                invPercentSlider,
                commonPercentSlider,
                uncommonPercentSlider,
                rarePercentSlider,
                epicPercentSlider
        ));

        setEntryVisibility(LOCAL_CONFIG.getPartialKeepinvMode());
    }


    private void changePage(ButtonWidget b){
        Screen next = new InvCustomSettingsScreen(parent, LOCAL_CONFIG, heading);
        client.setScreen(next);
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
        footing.getButtonWidget().setPosition(width - ParentSettingsScreen.widgetHeight- ParentSettingsScreen.sideMargin, height - ParentSettingsScreen.widgetHeight - ParentSettingsScreen.vertOptionMargin);
    }

    private void setEntryVisibility(KeepinvMode mode) {
        final Entry[] staticShown = {invPercentSlider};
        final Entry[] rarityShown = {commonPercentSlider, uncommonPercentSlider, rarePercentSlider, epicPercentSlider};
        final Entry[] customShown = {invPercentSlider, commonPercentSlider, uncommonPercentSlider, rarePercentSlider, epicPercentSlider, footing};
        final Entry[] vanillaShown = {};
        final Entry[] toHide = customShown;

        for(var e: toHide) e.hide();

        final Entry[] toShow =  switch (mode) {
            case STATIC -> staticShown;
            case RARITY -> rarityShown;
            case VANILLA -> vanillaShown;
            case CUSTOM -> customShown;
        };

        for(var e: toShow) e.show();


        entries.updateY(entries.getY());
    }

}


@Environment(EnvType.CLIENT)

class InvCustomSettingsScreen extends Screen {
    private final Screen parent;
    private final pkiSettings LOCAL_CONFIG;
    private TextHeaderEntry textHeader;
    private final EntryList heading;
    private EntryList options;
    private SimpleButton checkExpressionButton;
    private SimpleButton footing;
    private TextFieldEntry expressionTextField;
    private final boolean canEditValues;

    public InvCustomSettingsScreen(Screen parent, pkiSettings settings, EntryList heading) {
        super(GuiText.invScreen.name);
        this.client = MinecraftClient.getInstance();
        this.LOCAL_CONFIG = settings;
        this.heading = heading;
        this.parent = parent;
        assert client.player != null;
        canEditValues = client.player.hasPermissionLevel(4);
    }

    @Override
    public void render(DrawContext matrices, int mouseX, int mouseY, float delta){
        this.renderBackground(matrices);
        heading.render(matrices, mouseX, mouseY, delta);
        textHeader.render(matrices, mouseX, mouseY, delta);
        options.render(matrices, mouseX, mouseY, delta);
        footing.render(matrices, mouseX, mouseY, delta);
        checkExpressionButton.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    public void close() {
        LOCAL_CONFIG.setInvExpression(expressionTextField.getText());
    }

    @Override
    public void resize(MinecraftClient client, int width, int height) {
        this.width = width;
        this.height = height;
        heading.updateDimensions(width);
        options.updateDimensions(width);
        footing.getButtonWidget().setPosition(width - ParentSettingsScreen.widgetHeight- ParentSettingsScreen.sideMargin, height - ParentSettingsScreen.widgetHeight - ParentSettingsScreen.vertOptionMargin);
    }

    @Override
    public void init(){
        assert (client != null);
        int yPos = heading.updateY(ParentSettingsScreen.vertOptionMargin);

        heading.hidden = false;

        for( var e: heading.getSelectables()){
            this.addSelectableChild(e);
        }

        for( var e: heading.getChildren().get(1).getSelectables() ){
            super.addSelectableChild(e);
        }

        textHeader = new TextHeaderEntry(textRenderer,
                GuiText.customInvScreen.text_header.copy().setStyle(Style.EMPTY.withFormatting(Formatting.UNDERLINE)),
                0);
        yPos = textHeader.updateY(yPos);


        options = new EntryList(yPos);

        expressionTextField = new TextFieldEntry.Builder(textRenderer)
                .setName(GuiText.customInvScreen.invExpr_textfield_name)
                .setTooltip(GuiText.customInvScreen.invExpr_textfield_tooltip)
                .setText(new String(LOCAL_CONFIG.getInvExpression()))
                .build();
        this.addSelectableChild(expressionTextField.getTextFieldWidget());

        checkExpressionButton = new SimpleButton(
                expressionTextField.getTextFieldWidget().getX() - 10 - 40,
                yPos, 40, ParentSettingsScreen.widgetHeight,
                GuiText.customInvScreen.saveExpr_button_name,
                GuiText.customInvScreen.saveExpr_tooltip_base.copy().setStyle(Style.EMPTY.withColor(0x888888)),
                this::saveExpression, button -> button.setX(expressionTextField.getTextFieldWidget().getX() - 10 - 40));
        for(var s: checkExpressionButton.getSelectables()){
            this.addSelectableChild(s);
        }

        SimpleText text = new SimpleText(textRenderer, GuiText.customInvScreen.invExpr_text, 0);
        this.addSelectableChild(text.getSelectables().get(0));

        CollapsableEntryList expressionTutorialEntry = new CollapsableEntryList(
                GuiText.customInvScreen.invExpr_list,
                options, false, 0, ParentSettingsScreen.sideMargin, ParentSettingsScreen.buttonWidth);
        expressionTutorialEntry.addChild(text);
        this.addSelectableChild(expressionTutorialEntry.getButtonWidget());


        options.addChildren(Arrays.asList(
                expressionTextField,
                expressionTutorialEntry
        ));
        options.updateY(yPos);


        footing = new SimpleButton(width - ParentSettingsScreen.sideMargin, height - ParentSettingsScreen.vertOptionMargin - ParentSettingsScreen.widgetHeight,
                ParentSettingsScreen.widgetHeight, ParentSettingsScreen.widgetHeight, Text.literal(String.format("%c", 0x2191)), null,
                this::changePage, null);
        this.addSelectableChild(footing.getSelectables().get(0));

    }

    private void changePage(ButtonWidget b){
        assert client != null;
        Screen next = new InvSettingsScreen(parent, LOCAL_CONFIG, heading);
        this.close();
        client.setScreen(next);
    }

    @Override
    protected <T extends Element & Selectable> T addSelectableChild(T child) {
        if(canEditValues)
            return super.addSelectableChild(child);
        return null;
    }

    private void saveExpression(ButtonWidget button){
        String expression = this.expressionTextField.getText();
        var formula = new InventoryDroprateFormula(null);
        try{
            formula.testExpression(expression);
            button.setMessage(
                    GuiText.customInvScreen.saveExpr_button_name
                            .copy().setStyle(Style.EMPTY.withColor(0x00AA00))
            );
            button.setTooltip(Tooltip.of(
                    GuiText.customInvScreen.saveExpr_tooltip_success
                            .copy().setStyle((Style.EMPTY.withColor(0x00FF00)))
            ));
            LOCAL_CONFIG.setInvExpression(expression);
        }catch(Exception e){
            button.setMessage(
                    GuiText.customInvScreen.saveExpr_button_name
                            .copy().setStyle(Style.EMPTY.withColor(0xAA0000))
            );

            button.setTooltip(Tooltip.of(
                    GuiText.customInvScreen.saveExpr_tooltip_failure
                            .copy()
                            .append(Text.literal(e.getMessage()))
                            .setStyle(Style.EMPTY.withColor(0xFF0000))
            ));
        }
    }

}

