package me.atie.partialKeepinventory.gui;

import me.atie.partialKeepinventory.PartialKeepInventory;
import me.atie.partialKeepinventory.gui.Widgets.ButtonSelectionEntry;
import me.atie.partialKeepinventory.gui.Widgets.EntryList;
import me.atie.partialKeepinventory.gui.Widgets.SimpleButton;
import me.atie.partialKeepinventory.settings.pkiSettings;
import me.atie.partialKeepinventory.text.GuiText;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import static me.atie.partialKeepinventory.PartialKeepInventory.CONFIG;

// I suggest you don't touch this code, for your own sanity

@Environment(EnvType.CLIENT)
public class ParentSettingsScreen extends Screen {

    private Screen nextScreen;
    private Screen previousScreen;
    private pkiSettings LOCAL_CONFIG;
    public final static int vertOptionMargin = 5;
    public final static int sideMargin = 20;
    public final static int widgetHeight = 20;
    public final static int buttonWidth = 120;
    public final static int sliderWidth = 120;

    private TextRenderer textRenderer;
    private EntryList header;

    private boolean copyConfig = false;



    public ParentSettingsScreen() {
        super(Text.translatable(PartialKeepInventory.ID));
    }


    public ParentSettingsScreen(Screen previous) {
        super(Text.translatable(PartialKeepInventory.ID));
        this.previousScreen = previous;
    }

    private boolean isInWorld() {
        assert client != null;
        ClientPlayNetworkHandler clientPlayNetworkHandler = client.getNetworkHandler();
        return clientPlayNetworkHandler != null && clientPlayNetworkHandler.getConnection().isOpen();
    }

    @Override
    public void init() {
        assert (client != null);
        assert (client.player != null);
        this.textRenderer = client.textRenderer;
        copyConfig = false;

        if( !isInWorld() ){
            nextScreen = new ErrorScreen(this, GuiText.errorScreen.no_server);
            client.setScreen(nextScreen);
            return;
        }
        boolean isIntegratedServer = client.getServer() != null;

        if( CONFIG == null ){
            PartialKeepInventory.LOGGER.error("Can't open GUI: Config == null");
            nextScreen = new ErrorScreen(this, GuiText.errorScreen.no_config);
            client.setScreen(nextScreen);
            return;
        }

        if( !CONFIG.validSettings ){
            PartialKeepInventory.LOGGER.error("Can't open GUI: Invalid config");
            nextScreen = new ErrorScreen(this, GuiText.errorScreen.invalid_config);
            client.setScreen(nextScreen);
            return;
        }
        copyConfig = client.player.hasPermissionLevel(2);

        LOCAL_CONFIG = CONFIG.clone();

        initHeader();

        nextScreen = new InvSettingsScreen(this, LOCAL_CONFIG, header);
        client.setScreen(nextScreen);
    }

    public Screen constructScreen(ScreenTracker type) {
        Screen screen = null;
        switch (type) {
            case INV_CONFIG -> screen = new InvSettingsScreen(this, LOCAL_CONFIG, header);
            case XP_CONFIG -> screen = new XpSettingsScreen(this, LOCAL_CONFIG, header);
            case ERROR -> screen = new ErrorScreen(this, GuiText.errorScreen.no_server);
        }
        return screen;
    }


    private void initHeader(){
        header = new EntryList(vertOptionMargin);

        int buttonWidth = super.width/2;
        int buttonX = super.width/4;

        SimpleButton enableModButtonEntry = new SimpleButton(buttonX, 0, buttonWidth, widgetHeight, modEnabledText(CONFIG.getEnableMod()),
                GuiText.screenHeader.mod_enabled_tooltip,
                b -> {
                    boolean newVal = !CONFIG.getEnableMod();
                    CONFIG.setEnableMod(newVal);
                    b.setMessage( modEnabledText(newVal) );
                }, null);

        ButtonSelectionEntry<ScreenTracker> menu = new ButtonSelectionEntry.Builder<ScreenTracker>(3)
                .setButtonMargin(2)
                .setXMargin(sideMargin)
                .addButton(GuiText.screenHeader.close_button, ScreenTracker.CLOSE)
                .addButton(GuiText.screenHeader.inv_button, ScreenTracker.INV_CONFIG)
                .addButton(GuiText.screenHeader.xp_button, ScreenTracker.XP_CONFIG)
                .onClick(s -> {
                    assert client != null;
                    client.currentScreen.close();
                    if( s != ScreenTracker.CLOSE ) {
                        nextScreen = constructScreen(s);
                        client.setScreen(nextScreen);
                    }
                    else{
                        this.close();
                    }
                })
                .build(ScreenTracker.INV_CONFIG);

        header.addChild(enableModButtonEntry);
        header.addChild(menu);
        header.updateY(5);
    }


    @Override
    public void close() {
        super.close();

        if (!isInWorld() || LOCAL_CONFIG == null) {
            return;
        }
        //  synchronization
        if(copyConfig) {
            CONFIG.copy(LOCAL_CONFIG);
            pkiSettings.updateServerConfig();
        }
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
    }


    public static Text percentageToText(double x) {
        return Text.literal(String.format("%.0f%%", Math.floor(x)));
    }

    public static int nextElementY(int y){
        return y + widgetHeight + vertOptionMargin;
    }

    public static Text modEnabledText(boolean b){
        return b ? GuiText.screenHeader.mod_enabled_button_true.copy().setStyle(Style.EMPTY.withColor(Formatting.GREEN))
                 : GuiText.screenHeader.mod_enabled_button_false.copy().setStyle(Style.EMPTY.withColor(Formatting.RED));
    }


    private enum ScreenTracker {
        INV_CONFIG, XP_CONFIG, ERROR, CLOSE
    }

}
