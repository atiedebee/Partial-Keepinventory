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
import net.minecraft.world.GameRules;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 * TODO: GUI for trinkets settings
 * TODO: GUI for rules / groups
 * TODO: Serializing rules / groups
* */

public class PartialKeepInventory implements ModInitializer {
	public static EnvType environment;
	public static final String ID = "partial-keepinv";
	public static pkiVersion VERSION;
	public static pkiSettings CONFIG;

	public static final Logger LOGGER = LoggerFactory.getLogger(ID);

	public static final GameRules.Key<GameRules.BooleanRule> creativeKeepInventory =
			GameRuleRegistry.register("creativeKeepInventory", GameRules.Category.PLAYER, GameRuleFactory.createBooleanRule(false));




	@Override
	public void onInitialize() {
		Version version = FabricLoader.getInstance().getModContainer(ID).get().getMetadata().getVersion();
		VERSION = new pkiVersion( version.getFriendlyString() );

		// backwards compatibility functions
		BwSettingsCompat.init();

		// handles the entry points as well
		Impl.loadImplementations();

		pkiCommandRegistration.registerCommands();

		ServerPlayerEvents.COPY_FROM.register(ExperienceUtil::copyNewXpAmount);
		ServerPlayerEvents.COPY_FROM.register(InventoryUtil::copyInventory);


		// init the settings.
		ServerLifecycleEvents.SERVER_STARTED.register(server -> {
			CONFIG = pkiSettings.getServerState(server);
		});
		ServerLifecycleEvents.SERVER_STOPPING.register(server -> CONFIG.markDirty());

	}
}
