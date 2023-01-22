package me.atie.partialKeepinventory;

import me.atie.partialKeepinventory.commands.pkiCommandRegistration;
import me.atie.partialKeepinventory.settings.pkiSettings;
import me.atie.partialKeepinventory.impl.Impl;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.GameRules;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
   TODO: Find a good way to implement trinkets drop behaviour
 * TODO: Trinkets settings (fix trinket droprates, they are currently dropped based on trinkets' code)
 *  Ideas for trinket settings:
 * 	- Trinkets specific droprates
 *  - Follow / don't follow trinket drop rules (aka destroy / drop / keep)
 *
 * TODO: GUI for trinkets settings
 * TODO: Option for dropping shulker box contents instead of the shulker box themselves
* */

public class PartialKeepInventory implements ModInitializer {
	public static EnvType environment;
	public static MinecraftServer server;
	public static final Logger LOGGER = LoggerFactory.getLogger(getID());

	public static pkiSettings CONFIG = new pkiSettings();

	public static final GameRules.Key<GameRules.BooleanRule> creativeKeepInventory =
			GameRuleRegistry.register("creativeKeepInventory", GameRules.Category.PLAYER, GameRuleFactory.createBooleanRule(false));


	public static String getID(){
		return "partial-keepinv";
	}

	@Override
	public void onInitialize() {
		pkiCommandRegistration.registerCommands();

		Impl.loadImplementations();

		// init the settings for singleplayer worlds.
		ServerLifecycleEvents.SERVER_STARTED.register(server -> {
			if( environment == EnvType.CLIENT ) {
				PartialKeepInventory.server = server;
				CONFIG = pkiSettings.getServerState(server);
			}
		});

		ServerLifecycleEvents.SERVER_STOPPING.register(server -> CONFIG.markDirty());


	}
}
