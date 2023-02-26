package me.atie.partialKeepinventory.gui;

import me.atie.partialKeepinventory.gui.Widgets.EntryList;
import me.atie.partialKeepinventory.gui.Widgets.SimpleButton;
import me.atie.partialKeepinventory.impl.Impl;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;


public class ImplementationsScreen extends Screen {

    private final List<Supplier<Screen>> screenSuppliers;
    private final List<SimpleButton> buttonList;
    private final EntryList header;
    private int buttonsPerScreen;
    private int screenNumber = 0;

    public ImplementationsScreen(Text title, EntryList header) {
        super(title);

        screenSuppliers = Impl.settingScreenSuppliers;
        this.header = header;
        buttonList = new ArrayList<>(screenSuppliers.size());
    }

    private void setButtonsPerScreen(){
        int buttonsStart = header.getY() + header.getHeight() + ParentSettingsScreen.vertOptionMargin;
        int buttonSize = ParentSettingsScreen.vertOptionMargin + ParentSettingsScreen.widgetHeight;
        int buttonsPlace = this.width - buttonsStart - ParentSettingsScreen.vertOptionMargin;

        buttonsPerScreen = buttonsPlace / buttonSize;
    }

    @Override
    public void init(){
        setButtonsPerScreen();

        int startY = header.getY() + header.getHeight() + ParentSettingsScreen.vertOptionMargin;
        int yPos =  startY;
        for( int i = 0; i < screenSuppliers.size(); i++ ){
            if( yPos > this.height - ParentSettingsScreen.vertOptionMargin){
                yPos = startY;
            }
            int finalI = i;
            SimpleButton e = new SimpleButton( ParentSettingsScreen.sideMargin, yPos, ParentSettingsScreen.buttonWidth, ParentSettingsScreen.widgetHeight,
                    Impl.modNames.get(i), null, (b) -> client.setScreen( screenSuppliers.get(finalI).get()) );
            yPos += ParentSettingsScreen.widgetHeight + ParentSettingsScreen.vertOptionMargin;
        }
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta){
        header.render(matrices, mouseX, mouseY, delta);

        for( int i = 0;
             i + screenNumber * buttonsPerScreen < buttonList.size() && i < buttonsPerScreen;
             i++){
            buttonList.get(i + screenNumber * buttonsPerScreen).render(matrices, mouseX, mouseY, delta);
        }

    }


}
