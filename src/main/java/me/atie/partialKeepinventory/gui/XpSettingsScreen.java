package me.atie.partialKeepinventory.gui;

import me.atie.partialKeepinventory.KeepXPMode;
import me.atie.partialKeepinventory.PartialKeepInventory;
import me.atie.partialKeepinventory.formula.InventoryDroprateFormula;
import me.atie.partialKeepinventory.settings.pkiSettings;
import me.atie.partialKeepinventory.gui.Widgets.*;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
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
    private SimpleButton footing;

    private final EntryList heading;
    private EntryList entries;
    final private boolean canEditValues;


    public XpSettingsScreen(Screen parent, pkiSettings settings, EntryList heading) {
        super(Text.translatable(PartialKeepInventory.getID() + ".gui.screen.xp"));
        this.parent = parent;
        this.client = MinecraftClient.getInstance();
        this.LOCAL_CONFIG = settings;
        this.heading = heading;
        assert client.player != null;
        this.canEditValues = client.player.hasPermissionLevel(2);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);

        heading.render(matrices, mouseX, mouseY, delta);
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

        footing = new SimpleButton(width - ParentSettingsScreen.sideMargin, height - ParentSettingsScreen.vertOptionMargin - ParentSettingsScreen.widgetHeight,
                ParentSettingsScreen.widgetHeight, ParentSettingsScreen.widgetHeight, Text.literal(String.format("%c", 0x2193)), null,
                this::changePage, null);
        this.addSelectableChild(footing.getSelectables().get(0));

        assert (client != null);

        for( var e: heading.getSelectables()){
            super.addSelectableChild(e);
        }

        int yPos = heading.updateY(5);

        try {
            xpTextHeaderEntry = new TextHeaderEntry(textRenderer, Text.translatable(PartialKeepInventory.getID() + ".gui.header.xp").setStyle(Style.EMPTY.withFormatting(Formatting.UNDERLINE)), yPos);

            keepXPModeButtonEntry = new ButtonEntry.Builder<KeepXPMode>(textRenderer)
                    .setName(Text.translatable(PartialKeepInventory.getID() + ".gui.text.partialxpmode"))
                    .setTooltip(Text.translatable(PartialKeepInventory.getID() + ".gui.tooltip.keepxpmode"))
                    .toText(KeepXPMode::getName)
                    .setGetter(LOCAL_CONFIG::getKeepxpMode)
                    .setSetter(s -> {
                        setEntryVisibility(s);
                        LOCAL_CONFIG.setKeepxpMode(s);
                    })
                    .nextVal(KeepXPMode::next)
                    .build();
            this.addSelectableChild(keepXPModeButtonEntry.getButtonWidget());


            DropSlider = new SliderEntry.Builder(textRenderer)
                    .intGetter(LOCAL_CONFIG::getXpDrop)
                    .intSetter(LOCAL_CONFIG::setXpDrop)
                    .toText(ParentSettingsScreen::percentageToText)
                    .setLimits(0, 100)
                    .setName(Text.translatable(PartialKeepInventory.getID() + ".gui.slider.xpdrop"))
                    .setTooltip(Text.translatable(PartialKeepInventory.getID() + ".gui.tooltip.xpdrop"))
                    .build();
            this.addSelectableChild(DropSlider.getSliderWidget());

            LossSlider = new SliderEntry.Builder(textRenderer)
                    .intGetter(LOCAL_CONFIG::getXpLoss)
                    .intSetter(LOCAL_CONFIG::setXpLoss)
                    .toText(ParentSettingsScreen::percentageToText)
                    .setLimits(0, 100)
                    .setName(Text.translatable(PartialKeepInventory.getID() + ".gui.slider.xploss"))
                    .setTooltip(Text.translatable(PartialKeepInventory.getID() + ".gui.tooltip.xploss"))
                    .build();
            this.addSelectableChild(LossSlider.getSliderWidget());

        } catch (Exception e) {
            PartialKeepInventory.LOGGER.error("Failed creating gui: " + e);
            throw new RuntimeException(e);
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

    private void changePage(ButtonWidget buttonWidget) {
        Screen next = new XpCustomSettingScreen(parent, LOCAL_CONFIG, heading);
        this.close();
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
    }

    public void setEntryVisibility(KeepXPMode mode) {
        final Entry[] toHide = {DropSlider, LossSlider, footing};
        final Entry[] VANILLA = {};
        final Entry[] STATIC_POINTS = {DropSlider, LossSlider};
        final Entry[] STATIC_LEVELS = {DropSlider, LossSlider};
        final Entry[] CUSTOM_POINTS = {DropSlider, LossSlider, footing};
        final Entry[] CUSTOM_LEVELS = {DropSlider, LossSlider, footing};

        for( var e: toHide ){ e.hidden = true; }

        final Entry[] shown = switch(mode){
            case STATIC_LEVELS -> STATIC_LEVELS;
            case STATIC_POINTS -> STATIC_POINTS;
            case CUSTOM_LEVELS -> CUSTOM_LEVELS;
            case CUSTOM_POINTS -> CUSTOM_POINTS;
            case VANILLA -> VANILLA;
        };

        for( var e: shown ){ e.hidden = false; }
        entries.updateY(entries.getY());
    }

}

class XpCustomSettingScreen extends Screen {
    private final Screen parent;
    private final pkiSettings LOCAL_CONFIG;
    private TextHeaderEntry textHeader;
    private final EntryList heading;
    private SimpleButton footing;
    private TextFieldEntry xpLossExpressionTextField;
    private TextFieldEntry xpDropExpressionTextField;
    private final boolean canEditValues;
    private SimpleButton saveLossExpressionButton;
    private SimpleButton saveDropExpressionButton;
    private CollapsableEntryList expressionTutorialEntry;

    public XpCustomSettingScreen(Screen parent, pkiSettings settings, EntryList heading) {
        super(Text.translatable(PartialKeepInventory.getID() + ".gui.screen.xp"));
        this.client = MinecraftClient.getInstance();
        this.LOCAL_CONFIG = settings;
        this.heading = heading;
        this.parent = parent;
        assert client.player != null;
        canEditValues = client.player.hasPermissionLevel(2);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta){
        this.renderBackground(matrices);
        heading.render(matrices, mouseX, mouseY, delta);
        textHeader.render(matrices, mouseX, mouseY, delta);
        footing.render(matrices, mouseX, mouseY, delta);
        saveDropExpressionButton.render(matrices, mouseX, mouseY, delta);
        saveLossExpressionButton.render(matrices, mouseX, mouseY, delta);
        xpDropExpressionTextField.render(matrices, mouseX, mouseY, delta);
        xpLossExpressionTextField.render(matrices, mouseX, mouseY, delta);
        expressionTutorialEntry.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    public void close() {
    }

    @Override
    public void resize(MinecraftClient client, int width, int height) {
        this.width = width;
        this.height = height;
        heading.updateDimensions(width);

        xpLossExpressionTextField.updateDimensions(width);
        xpDropExpressionTextField.updateDimensions(width);

        saveLossExpressionButton.updateDimensions(width);
        saveLossExpressionButton.getButtonWidget().setX(xpLossExpressionTextField.getTextFieldWidget().getX() - 10 - saveLossExpressionButton.getButtonWidget().getWidth());
        saveDropExpressionButton.updateDimensions(width);
        saveDropExpressionButton.getButtonWidget().setX(xpDropExpressionTextField.getTextFieldWidget().getX() - 10 - saveDropExpressionButton.getButtonWidget().getWidth());

        footing.getButtonWidget().setPos(width - ParentSettingsScreen.widgetHeight- ParentSettingsScreen.sideMargin, height - ParentSettingsScreen.widgetHeight - ParentSettingsScreen.vertOptionMargin);
    }

    @Override
    public void init(){
        assert (client != null);
        int yPos = heading.updateY(ParentSettingsScreen.vertOptionMargin);
        final int saveButtonWidth = 40;
        heading.hidden = false;

        for( var e: heading.getSelectables()){
            this.addSelectableChild(e);
        }

        for( var e: heading.getChildren().get(1).getSelectables() ){
            super.addSelectableChild(e);
        }

        textHeader = new TextHeaderEntry(textRenderer,
                Text.translatable(PartialKeepInventory.getID() + ".gui.text.customxpheader").setStyle(Style.EMPTY.withFormatting(Formatting.UNDERLINE)),
                0);
        yPos = textHeader.updateY(yPos);


//        XP Drop Expression
        xpDropExpressionTextField = new TextFieldEntry.Builder(textRenderer)
                .setName(Text.translatable(PartialKeepInventory.getID() + ".gui.textfield.xpdrop-expression"))
                .setTooltip(Text.translatable(PartialKeepInventory.getID() + ".gui.tooltip.xpdrop-expression"))
                .setText(new String(LOCAL_CONFIG.getXpDropExpression()))
                .build();
        this.addSelectableChild(xpDropExpressionTextField.getTextFieldWidget());


        saveDropExpressionButton = new SimpleButton(
                xpDropExpressionTextField.getTextFieldWidget().getX() - 10 - saveButtonWidth,
                yPos, 40, ParentSettingsScreen.widgetHeight,
                Text.translatable(PartialKeepInventory.getID() + ".gui.inv.button.saveexpression"),
                Text.translatable(PartialKeepInventory.getID() + ".gui.inv.button.saveexpression.tooltip_base").setStyle(Style.EMPTY.withColor(0x888888)),
                button -> this.saveExpression(button, xpDropExpressionTextField),
                button -> button.setX(xpDropExpressionTextField.getTextFieldWidget().getX() - 10 - saveButtonWidth)
        );
        this.addSelectableChild(saveDropExpressionButton.getButtonWidget());
        yPos = xpDropExpressionTextField.updateY(yPos);

//        XP Loss Expression
        xpLossExpressionTextField = new TextFieldEntry.Builder(textRenderer)
                .setName(Text.translatable(PartialKeepInventory.getID() + ".gui.textfield.xploss-expression"))
                .setTooltip(Text.translatable(PartialKeepInventory.getID() + ".gui.tooltip.xploss-expression"))
                .setText(new String(LOCAL_CONFIG.getXpLossExpression()))
                .build();
        this.addSelectableChild(xpLossExpressionTextField.getTextFieldWidget());

        saveLossExpressionButton = new SimpleButton(
                xpLossExpressionTextField.getTextFieldWidget().getX() - 10 - saveButtonWidth,
                yPos, 40, ParentSettingsScreen.widgetHeight,
                Text.translatable(PartialKeepInventory.getID() + ".gui.inv.button.saveexpression"),
                Text.translatable(PartialKeepInventory.getID() + ".gui.inv.button.saveexpression.tooltip_base").setStyle(Style.EMPTY.withColor(0x888888)),
                button -> this.saveExpression(button, xpLossExpressionTextField),
                button -> button.setX(xpLossExpressionTextField.getTextFieldWidget().getX() - 10 - saveButtonWidth)
        );
        this.addSelectableChild(saveLossExpressionButton.getButtonWidget());
        yPos = xpLossExpressionTextField.updateY(yPos);


        SimpleText text = new SimpleText(textRenderer, Text.translatable(PartialKeepInventory.getID() + ".gui.text.xpexpression-guide"), 0);
        this.addSelectableChild(text.getSelectables().get(0));

        expressionTutorialEntry = new CollapsableEntryList(
                Text.translatable(PartialKeepInventory.getID() + ".gui.list.xpexpression"),
                null, false, yPos, ParentSettingsScreen.sideMargin, ParentSettingsScreen.buttonWidth);
        expressionTutorialEntry.addChild(text);
        this.addSelectableChild(expressionTutorialEntry.getButtonWidget());
        yPos = expressionTutorialEntry.updateY(yPos);



        footing = new SimpleButton(width - ParentSettingsScreen.sideMargin, height - ParentSettingsScreen.vertOptionMargin - ParentSettingsScreen.widgetHeight,
                ParentSettingsScreen.widgetHeight, ParentSettingsScreen.widgetHeight, Text.literal(String.format("%c", 0x2191)), null,
                this::changePage, null);
        super.addSelectableChild(footing.getSelectables().get(0));

    }

    private void changePage(ButtonWidget b){
        assert client != null;
        Screen next = new XpSettingsScreen(parent, LOCAL_CONFIG, heading);
        this.close();
        client.setScreen(next);
    }

    @Override
    protected <T extends Element & Selectable> T addSelectableChild(T child) {
        if(canEditValues)
            return super.addSelectableChild(child);
        return null;
    }

    private void saveExpression(ButtonWidget button, TextFieldEntry textFieldEntry){
        String expression = textFieldEntry.getText();
        var formula = new InventoryDroprateFormula(null);
        try{
            formula.testExpression(expression);
            button.setMessage(
                    Text.translatable(PartialKeepInventory.getID() + ".gui.inv.button.saveexpression")
                            .setStyle(Style.EMPTY.withColor(0x00AA00))
            );
            button.setTooltip(Tooltip.of(
                    Text.translatable(PartialKeepInventory.getID() + ".gui.inv.button.saveexpression.tooltip_success")
                            .setStyle((Style.EMPTY.withColor(0x00FF00)))
            ));
            LOCAL_CONFIG.setExpression(expression);
        }catch(Exception e){
            button.setMessage(
                    Text.translatable(PartialKeepInventory.getID() + ".gui.inv.button.saveexpression")
                            .setStyle(Style.EMPTY.withColor(0xAA0000))
            );

            button.setTooltip(Tooltip.of(
                    Text.translatable(PartialKeepInventory.getID() + ".gui.inv.button.saveexpression.tooltip_failure")
                            .append(Text.literal(e.getMessage()))
                            .setStyle(Style.EMPTY.withColor(0xFF0000))
            ));
        }
    }


}

