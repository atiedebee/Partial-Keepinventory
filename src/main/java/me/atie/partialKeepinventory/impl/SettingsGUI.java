package me.atie.partialKeepinventory.impl;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.atie.partialKeepinventory.KeepinvMode;
import me.atie.partialKeepinventory.PartialKeepInventory;
import me.atie.partialKeepinventory.component.pkiSettings;
import me.atie.partialKeepinventory.impl.ModmenuGUI.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import org.apache.commons.beanutils.BeanUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;

import static me.atie.partialKeepinventory.PartialKeepInventory.CONFIG_COMPONENT;

public class SettingsGUI extends Screen implements ModMenuApi {

    private Screen nextScreen;
    private Screen previousScreen;
    public pkiSettings LOCAL_CONFIG;
    public final static int vertOptionMargin = 5;
    public final static int sideMargin = 20;
    public final static int widgetHeight = 20;
    public final static int buttonWidth = 120;
    public final static int sliderWidth = 120;

    private TextRenderer textRenderer;

    private TextHeaderEntry errorMessage;


    @Override
    public ConfigScreenFactory<SettingsGUI> getModConfigScreenFactory() {
        return SettingsGUI::new;
    }

    public SettingsGUI() {
        super(Text.translatable(PartialKeepInventory.getID()));
    }


    public SettingsGUI(Screen previous) {
        super(Text.translatable(PartialKeepInventory.getID()));
        this.previousScreen = previous;
    }

    @Override
    public void init() {
        assert (client != null);
        this.textRenderer = client.textRenderer;

        if (client.getServer() == null) {
            nextScreen = new ErrorScreen(Text.translatable(PartialKeepInventory.getID() + ".gui.screen.error"), this);
            client.setScreen(nextScreen);
            return;
        }

        LOCAL_CONFIG = new pkiSettings();
        try {
            BeanUtils.copyProperties(LOCAL_CONFIG, CONFIG_COMPONENT);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }

        nextScreen = new InvSettings(Text.translatable(PartialKeepInventory.getID() + ".gui.screen.inv"), this);
        client.setScreen(nextScreen);
    }


    @Override
    public void close() {
        super.close();

        if (client.getServer() == null) {
            return;
        }

        //  synchronization
        try {
            BeanUtils.copyProperties(CONFIG_COMPONENT, LOCAL_CONFIG);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }

        CONFIG_COMPONENT.sync();

        PartialKeepInventory.LOGGER.info("MAIN GUI SCREEN WAS CLOSED");
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        nextScreen.render(matrices, mouseX, mouseY, delta);
    }


    private static KeepinvMode nextKeepinvMode(KeepinvMode x) {
        return switch (x) {
            case STATIC -> KeepinvMode.RARITY;
            case RARITY -> KeepinvMode.CUSTOM;
            case CUSTOM -> KeepinvMode.VANILLA;
            case VANILLA -> KeepinvMode.STATIC;
        };
    }

    public static Text percentageToText(double x) {
        return Text.literal(String.format("%.0f%%", Math.floor(x)));
    }

    public static Text boolToText(boolean b) {
        if (b)
            return Text.translatable(PartialKeepInventory.getID() + ".True");
        return Text.translatable(PartialKeepInventory.getID() + ".False");
    }

    public MinecraftClient getClient() {
        return client;
    }

    public static int nextElementY(int y){
        return y + widgetHeight + vertOptionMargin;
    }


    private class InvSettings extends Screen {
        private Screen parent;

        private MinecraftClient client;

        private TextHeaderEntry generalKeepinvText;
        private ButtonEntry<Boolean> enableModButtonEntry;
        private ButtonEntry<KeepinvMode> keepinvModeButtonEntry;
        private SliderEntry invPercentSlider;
        private SliderEntry commonPercentSlider;
        private SliderEntry uncommonPercentSlider;
        private SliderEntry rarePercentSlider;
        private SliderEntry epicPercentSlider;

        private EntryList entries;


        public InvSettings(Text title, Screen parent) {
            super(title);
            this.parent = parent;
            this.client = getClient();
        }

        @Override
        public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
            this.renderBackground(matrices);

            entries.render(matrices, mouseX, mouseY, delta);
        }



        @Override
        protected void init() {

            assert (client != null);


            int yPos = 20;
            try {
                generalKeepinvText = new TextHeaderEntry(textRenderer, Text.translatable(PartialKeepInventory.getID() + ".gui.header.inv"), yPos);

                enableModButtonEntry = new ButtonEntry.Builder<Boolean>(textRenderer)
                        .setName(Text.literal("Enable mod"))
                        .toText(SettingsGUI::boolToText)
                        .setGetter(LOCAL_CONFIG::getEnableMod)
                        .setSetter(LOCAL_CONFIG::setEnableMod)
                        .nextVal((b) -> !b)
                        .build();
                super.addSelectableChild(enableModButtonEntry.getButtonWidget());

                keepinvModeButtonEntry = new ButtonEntry.Builder<KeepinvMode>(textRenderer)
                        .setName(Text.literal("Keepinv mode"))
                        .toText(KeepinvMode::getName)
                        .setGetter(LOCAL_CONFIG::getPartialKeepinvMode)
                        .setSetter(LOCAL_CONFIG::setPartialKeepinvMode)
                        .nextVal(this::changeKeepinvMode)
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


            } catch (Exception e) {
                PartialKeepInventory.LOGGER.error("Failed creating gui: " + e);
            }

            entries = new EntryList(yPos);
            entries.addChildren(Arrays.asList(
                    enableModButtonEntry,

                    generalKeepinvText,
                    keepinvModeButtonEntry,
                    invPercentSlider,
                    commonPercentSlider,
                    uncommonPercentSlider,
                    rarePercentSlider,
                    epicPercentSlider
            ));
            entries.updateY(20);

        }

        @Override
        public void close() {
            parent.close();
        }

        private KeepinvMode changeKeepinvMode(KeepinvMode mode) {
            final List<EntryImpl> staticShown = Arrays.asList(invPercentSlider);
            final List<EntryImpl> rarityShown = Arrays.asList(commonPercentSlider, uncommonPercentSlider, rarePercentSlider, epicPercentSlider);
            final List<EntryImpl> customShown = Arrays.asList(invPercentSlider, commonPercentSlider, uncommonPercentSlider, rarePercentSlider, epicPercentSlider);
            final List<EntryImpl> vanillaShown= Arrays.asList();
            final List<EntryImpl> toHide = customShown;

            toHide.forEach( e -> e.hidden = true );

            mode = SettingsGUI.nextKeepinvMode(mode);
            switch (mode) {
                case STATIC -> staticShown.forEach( e -> e.hidden = false );
                case RARITY -> rarityShown.forEach( e -> e.hidden = false );
                case VANILLA -> vanillaShown.forEach( e -> e.hidden = false );
                case CUSTOM -> customShown.forEach( e -> e.hidden = false );
            }

            entries.updateY(entries.getY());

            return mode;
        }

    }


    private class ErrorScreen extends Screen {
        private Screen parent;
        private MinecraftClient client;
        private TextHeaderEntry errorMessage;

        protected ErrorScreen(Text title, Screen parent) {
            super(title);
            this.parent = parent;
            this.client = getClient();
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


    private enum ScreenTracker {
        INV_CONFIG, XP_CONFIG, ERROR
    }

}
