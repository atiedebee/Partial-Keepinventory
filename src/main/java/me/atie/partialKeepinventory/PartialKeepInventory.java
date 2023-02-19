package me.atie.partialKeepinventory;

import me.atie.partialKeepinventory.commands.pkiCommandRegistration;
import me.atie.partialKeepinventory.impl.Impl;
import me.atie.partialKeepinventory.settings.BwSettingsCompat;
import me.atie.partialKeepinventory.settings.pkiSettings;
import me.atie.partialKeepinventory.settings.pkiVersion;
import me.atie.partialKeepinventory.util.ExperienceUtil;
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
 * TODO: Trinkets settings
 *  Ideas for trinket settings:
 * 	- Trinkets specific droprates
 *  - Follow / don't follow trinket drop rules (aka destroy / drop / keep)
 * TODO: GUI for trinkets settings
 * TODO: Backwards compatibility on settings, needs more testing
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

		CONFIG = new pkiSettings();
		pkiCommandRegistration.registerCommands();

		ServerPlayerEvents.COPY_FROM.register(ExperienceUtil::copyNewXpAmount);


		// init the settings for singleplayer worlds.
		ServerLifecycleEvents.SERVER_STARTED.register(server -> {
			if( environment == EnvType.CLIENT ) {
				LOGGER.info("SINGLE PLAYER WORLD STARTED");

				PartialKeepInventory.server = server;
				CONFIG = pkiSettings.getServerState(server);
			}
		});
		ServerLifecycleEvents.SERVER_STOPPED.register(server -> PartialKeepInventory.server = null);

		ServerLifecycleEvents.SERVER_STOPPING.register(server -> CONFIG.markDirty());

	}
}
