package me.atie.partialKeepinventory.gui;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.atie.partialKeepinventory.PartialKeepInventory;
import me.atie.partialKeepinventory.gui.Widgets.ButtonSelectionEntry;
import me.atie.partialKeepinventory.gui.Widgets.EntryList;
import me.atie.partialKeepinventory.gui.Widgets.SimpleButton;
import me.atie.partialKeepinventory.settings.pkiSettings;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import static me.atie.partialKeepinventory.PartialKeepInventory.CONFIG;

//TODO: Tooltips for all settings explaining how to use them

@Environment(EnvType.CLIENT)
public class SettingsGUI extends Screen implements ModMenuApi {

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

    private boolean isInWorld() {
        ClientPlayNetworkHandler clientPlayNetworkHandler = client.getNetworkHandler();
        return clientPlayNetworkHandler != null && clientPlayNetworkHandler.getConnection().isOpen();
    }

    @Override
    public void init() {
        assert (client != null);
        this.textRenderer = client.textRenderer;

        if( !isInWorld() ){
            nextScreen = new ErrorScreen(this);
            client.setScreen(nextScreen);
            return;
        }
        boolean isIntegratedServer = client.getServer() != null;

        LOCAL_CONFIG = CONFIG;

        initHeader();

        nextScreen = new InvSettingsScreen(this, LOCAL_CONFIG, header);
        client.setScreen(nextScreen);
    }

    public Screen constructScreen(ScreenTracker type) {
        Screen screen = null;
        switch (type) {
            case INV_CONFIG -> screen = new InvSettingsScreen(this, LOCAL_CONFIG, header);
            case XP_CONFIG -> screen = new XpSettingsScreen(this, LOCAL_CONFIG, header);
            case ERROR -> screen = new ErrorScreen(this);
        }
        return screen;
    }


    private void initHeader(){
        header = new EntryList(vertOptionMargin);

        int buttonWidth = super.width/2;
        int buttonX = super.width/4;

        SimpleButton enableModButtonEntry = new SimpleButton(buttonX, 0, buttonWidth, widgetHeight, modEnabledText(CONFIG.getEnableMod()),
                Text.translatable(PartialKeepInventory.getID() + ".gui.modenabled.tooltip"),
                b -> {
                    boolean newVal = !CONFIG.getEnableMod();
                    CONFIG.setEnableMod(newVal);
                    b.setMessage( modEnabledText(newVal) );
                });

        ButtonSelectionEntry<ScreenTracker> menu = new ButtonSelectionEntry.Builder<ScreenTracker>(3)
                .setButtonMargin(2)
                .setXMargin(sideMargin)
                .addButton(Text.translatable(PartialKeepInventory.getID() + ".gui.menu.close"), ScreenTracker.CLOSE)
                .addButton(Text.translatable(PartialKeepInventory.getID() + ".gui.menu.inventory"), ScreenTracker.INV_CONFIG)
                .addButton(Text.translatable(PartialKeepInventory.getID() + ".gui.menu.experience"), ScreenTracker.XP_CONFIG)
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

        if (!isInWorld()) {
            return;
        }
        //  synchronization
        assert MinecraftClient.getInstance().player != null;
        if(MinecraftClient.getInstance().player.hasPermissionLevel(4)) {
            pkiSettings.updateServerConfig(LOCAL_CONFIG);
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
        return b ?
                Text.translatable(PartialKeepInventory.getID() + ".gui.modenabled.true").setStyle(Style.EMPTY.withColor(Formatting.GREEN)) :
                Text.translatable(PartialKeepInventory.getID() + ".gui.modenabled.false").setStyle(Style.EMPTY.withColor(Formatting.RED));
    }


    private enum ScreenTracker {
        INV_CONFIG, XP_CONFIG, ERROR, CLOSE
    }

}
