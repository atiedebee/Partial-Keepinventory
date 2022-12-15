package me.atie.partialKeepinventory;

import eu.midnightdust.lib.config.MidnightConfig;
import me.atie.partialKeepinventory.config.pkiConfig;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class partialKeepinventory implements ModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("partialKeepinventory");

	public enum KeepinvMode {
		PERCENTAGE, RARITY
	}

	@Override
	public void onInitialize() {
		MidnightConfig.init("partial-keepinv", pkiConfig.class);
		LOGGER.info(pkiConfig.partialKeepinvMode.toString());

	}
}
