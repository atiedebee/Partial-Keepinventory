package me.atie.partialKeepinventory.gui;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.atie.partialKeepinventory.PartialKeepInventory;
import me.atie.partialKeepinventory.component.pkiScoreboardComponent;
import me.atie.partialKeepinventory.component.pkiSettings;
import me.atie.partialKeepinventory.gui.Widgets.ButtonSelectionEntry;
import me.atie.partialKeepinventory.gui.Widgets.EntryList;
import me.atie.partialKeepinventory.gui.Widgets.SimpleButton;
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
import org.apache.commons.beanutils.BeanUtils;

import java.lang.reflect.InvocationTargetException;

import static me.atie.partialKeepinventory.PartialKeepInventory.CONFIG_COMPONENT;

//TODO: Tooltips for all settings explaining how to use them

@Environment(EnvType.CLIENT)
public class SettingsGUI extends Screen implements ModMenuApi {

    private Screen nextScreen;
    private Screen previousScreen;
    public pkiSettings LOCAL_CONFIG = null;
    private boolean integratedServer;
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


        //very long check to see if the player is in a world...
        if( !isInWorld() ){
            nextScreen = new ErrorScreen(this);
            client.setScreen(nextScreen);
            return;
        }
        integratedServer = client.getServer() != null;

        if( integratedServer ){
            LOCAL_CONFIG = new pkiSettings();
            try {
                BeanUtils.copyProperties(LOCAL_CONFIG, CONFIG_COMPONENT);
            } catch (IllegalAccessException | InvocationTargetException e) {
                PartialKeepInventory.LOGGER.error(e.getMessage());
                throw new RuntimeException(e);
            }

            PartialKeepInventory.LOGGER.info("Got CONFIG_COMPONENT: " + LOCAL_CONFIG);
        }
        else {
            LOCAL_CONFIG = PartialKeepInventory.LOCAL_CONFIG;
            PartialKeepInventory.LOGGER.info("Got LOCAL CONFIG: " + LOCAL_CONFIG);
        }

        initHeader();


        nextScreen = new InvSettingsScreen(this, LOCAL_CONFIG, header);
        client.setScreen(nextScreen);
    }

    public Screen constructScreen(ScreenTracker type) {
        Screen screen = null;
        switch (type) {
            case INV_CONFIG -> {
                screen = new InvSettingsScreen(this, LOCAL_CONFIG, header);
            }
            case XP_CONFIG -> {
                screen = new XpSettingsScreen(this, LOCAL_CONFIG, header);
            }
            case ERROR -> {
                screen = new ErrorScreen(this);
            }
        }
        return screen;
    }


    private void initHeader(){
        header = new EntryList(vertOptionMargin);

        int buttonWidth = super.width/2;
        int buttonX = super.width/4;

        SimpleButton<Boolean> enableModButtonEntry = new SimpleButton<>(buttonX, 0, buttonWidth, widgetHeight, modEnabledText(LOCAL_CONFIG.getEnableMod()),
                b -> {
                    boolean newVal = !LOCAL_CONFIG.getEnableMod();
                    LOCAL_CONFIG.setEnableMod(newVal);
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

        if(MinecraftClient.getInstance().getServer() != null) {// integrated server
            try {
                BeanUtils.copyProperties(CONFIG_COMPONENT, LOCAL_CONFIG);
            } catch (IllegalAccessException | InvocationTargetException e) {
                PartialKeepInventory.LOGGER.error(e.getMessage());
                throw new RuntimeException(e);
            }
            CONFIG_COMPONENT.sync();
        }
        else{
            pkiScoreboardComponent.updateServerConfig(LOCAL_CONFIG);
        }


    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
    }


    public static Text percentageToText(double x) {
        return Text.literal(String.format("%.0f%%", Math.floor(x)));
    }

    public static Text boolToText(boolean b) {
        if (b)
            return Text.translatable(PartialKeepInventory.getID() + ".True");
        return Text.translatable(PartialKeepInventory.getID() + ".False");
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
