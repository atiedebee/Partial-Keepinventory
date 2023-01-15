package me.atie.partialKeepinventory;

import me.atie.partialKeepinventory.commands.pkiCommandRegistration;
import me.atie.partialKeepinventory.component.pkiComponentList;
import me.atie.partialKeepinventory.component.pkiScoreboardComponent;
import me.atie.partialKeepinventory.component.pkiSettings;
import me.atie.partialKeepinventory.component.pkiTeamComponent;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.GameRules;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PartialKeepInventory implements ModInitializer {
	public static EnvType environment;
	public static MinecraftServer server;
	public static final Logger LOGGER = LoggerFactory.getLogger(getID());

	public static pkiSettings LOCAL_CONFIG;
	public static pkiScoreboardComponent CONFIG_COMPONENT = null;
	public static pkiTeamComponent SAVED_PLAYERS = null;

	public static final GameRules.Key<GameRules.BooleanRule> creativeKeepInventory =
			GameRuleRegistry.register("creativeKeepInventory", GameRules.Category.PLAYER, GameRuleFactory.createBooleanRule(false));


	public static String getID(){
		return "partial-keepinv";
	}

	@Override
	public void onInitialize() {
		pkiCommandRegistration.registerCommands();

		// init the settings for singleplayer worlds.
		ServerLifecycleEvents.SERVER_STARTED.register(server -> {
			if(PartialKeepInventory.environment == EnvType.CLIENT) {
				PartialKeepInventory.server = server;
				CONFIG_COMPONENT = pkiComponentList.configKey.get(PartialKeepInventory.server.getScoreboard());
			}
		});

		// Prevent config component from "leaking"
		ServerLifecycleEvents.SERVER_STOPPED.register(server -> {
			CONFIG_COMPONENT = null;
		});


	}
}
