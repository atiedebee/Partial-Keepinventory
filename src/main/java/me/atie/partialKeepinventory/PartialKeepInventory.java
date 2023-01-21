package me.atie.partialKeepinventory;

import me.atie.partialKeepinventory.commands.pkiCommandRegistration;
import me.atie.partialKeepinventory.component.pkiSettings;
import me.atie.partialKeepinventory.impl.impl;
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
 * TODO: Fix the text that explains GUI variables
 * TODO: Trinkets settings (fix trinket droprates, they are currently dropped based on trinkets' code)
 *  Ideas for trinket settings:
 * 	- Trinket specific droprate
 *  - Follow / don't follow trinket drop rules (aka destroy / drop / keep. Maybe something to add to the main mod)
 *
 * TODO: GUI for trinkets settings
 * TODO: Option for dropping shulker box contents instead of the shulkerbox themselves
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

		impl.loadImplementations();

		// init the settings for singleplayer worlds.
		ServerLifecycleEvents.SERVER_STARTED.register(server -> {
			if( environment == EnvType.CLIENT ) {
				PartialKeepInventory.server = server;
				CONFIG = pkiSettings.getServerState(server);
			}
		});

		// Prevent config component from "leaking"
		ServerLifecycleEvents.SERVER_STOPPING.register(server -> {
			CONFIG.markDirty();
		});


	}
}
