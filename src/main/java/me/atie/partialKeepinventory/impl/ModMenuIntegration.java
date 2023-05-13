package me.atie.partialKeepinventory.impl;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.atie.partialKeepinventory.gui.ParentSettingsScreen;

public class ModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<ParentSettingsScreen> getModConfigScreenFactory() {
        return ParentSettingsScreen::new;
    }

}
