package me.atie.partialKeepinventory;

import me.atie.partialKeepinventory.commands.pkiCommandRegistration;
import me.atie.partialKeepinventory.config.pkiConfigClass;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class partialKeepinventory implements ModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("partialKeepinventory");
	public static final pkiConfigClass CONFIG = pkiConfigClass.createAndLoad();
	public enum KeepinvMode {
		PERCENTAGE, RARITY, CUSTOM
	}


	@Override
	public void onInitialize() {
		pkiCommandRegistration.registerCommands();
	}
}
