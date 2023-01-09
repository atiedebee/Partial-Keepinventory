package me.atie.partialKeepinventory;

import me.atie.partialKeepinventory.commands.pkiCommandRegistration;
import me.atie.partialKeepinventory.component.pkiComponentList;
import me.atie.partialKeepinventory.component.pkiScoreboardComponent;
import me.atie.partialKeepinventory.component.pkiTeamComponent;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.GameRules;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PartialKeepInventory implements ModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger(getID());


	public static pkiScoreboardComponent CONFIG_COMPONENT = null;
	public static pkiTeamComponent SAVED_PLAYERS = null;

	public static String getID(){
		return "partial-keepinv";
	}


	public static MinecraftServer server;

	public static final GameRules.Key<GameRules.BooleanRule> creativeKeepInventory =
			GameRuleRegistry.register("creativeKeepInventory", GameRules.Category.PLAYER, GameRuleFactory.createBooleanRule(false));

	@Override
	public void onInitialize() {
		pkiCommandRegistration.registerCommands();

		// init the settings for a player when they join.
		ServerPlayConnectionEvents.JOIN.register( (handler, sender, server) -> {
			PartialKeepInventory.server = server;
			CONFIG_COMPONENT = pkiComponentList.configKey.get(server.getScoreboard());
			CONFIG_COMPONENT.update();
		} );

	}
}
