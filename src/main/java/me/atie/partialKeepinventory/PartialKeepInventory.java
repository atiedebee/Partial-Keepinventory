package me.atie.partialKeepinventory;

import me.atie.partialKeepinventory.commands.pkiCommandRegistration;
import me.atie.partialKeepinventory.impl.Impl;
import me.atie.partialKeepinventory.settings.BwSettingsCompat;
import me.atie.partialKeepinventory.settings.pkiSettings;
import me.atie.partialKeepinventory.settings.pkiVersion;
import me.atie.partialKeepinventory.util.ExperienceUtil;
import me.atie.partialKeepinventory.util.InventoryUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.Version;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.GameRules;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 * TODO: GUI for trinkets settings
 * TODO: Backwards compatibility on settings, needs more testing
 *  NOTE: IDEA: 18 04 2023  Option to damage armor on death
 * TODO: Find and fix bugs for 0.2.1. Also add more help messages.
* */

public class PartialKeepInventory implements ModInitializer {
	public static EnvType environment;
	public static MinecraftServer server;
	public static pkiVersion modVersion;
	public static final Logger LOGGER = LoggerFactory.getLogger(getID());

	public static pkiSettings CONFIG;

	public static final GameRules.Key<GameRules.BooleanRule> creativeKeepInventory =
			GameRuleRegistry.register("creativeKeepInventory", GameRules.Category.PLAYER, GameRuleFactory.createBooleanRule(false));


	public static String getID(){
		return "partial-keepinv";
	}

	@Override
	public void onInitialize() {
		Version version = FabricLoader.getInstance().getModContainer(PartialKeepInventory.getID()).get().getMetadata().getVersion();
		modVersion = new pkiVersion( version.getFriendlyString() );

		// backwards compatibility functions
		BwSettingsCompat.init();

		// handles the entry points as well
		Impl.loadImplementations();

		pkiCommandRegistration.registerCommands();

		ServerPlayerEvents.COPY_FROM.register(ExperienceUtil::copyNewXpAmount);
		ServerPlayerEvents.COPY_FROM.register(InventoryUtil::copyInventory);


		// init the settings.
		ServerLifecycleEvents.SERVER_STARTED.register(server -> {
			if( environment == EnvType.CLIENT ) {
				PartialKeepInventory.server = server;
			}
			CONFIG = pkiSettings.getServerState(server);
			PartialKeepInventory.LOGGER.info("READ CONFIG FROM SERVER WITH VERSION " + CONFIG.configVersion);
		});
		ServerLifecycleEvents.SERVER_STOPPED.register(server -> PartialKeepInventory.server = null);

		ServerLifecycleEvents.SERVER_STOPPING.register(server -> CONFIG.markDirty());

	}
}
