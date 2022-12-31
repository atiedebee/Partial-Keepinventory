package me.atie.partialKeepinventory;

import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import me.atie.partialKeepinventory.commands.pkiCommandRegistration;
import me.atie.partialKeepinventory.component.pkiScoreboardComponent;
import me.atie.partialKeepinventory.component.pkiTeamComponent;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.Identifier;
import net.minecraft.world.GameRules;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class partialKeepinventory implements ModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("partialKeepinventory");
	public enum KeepinvMode {
		STATIC, RARITY, CUSTOM, VANILLA
	}

	public enum KeepXPMode {
		STATIC_LEVEL, STATIC_POINTS, VANILLA
	}


	public static pkiScoreboardComponent CONFIG_COMPONENT = null;
	public static pkiTeamComponent SAVED_PLAYERS = null;

	public static String getID(){
		return "partial-keepinv";
	}


	public static final ComponentKey<pkiScoreboardComponent> configKey = ComponentRegistry.getOrCreate(
			new Identifier(partialKeepinventory.getID(), "config"),
			pkiScoreboardComponent.class);
	public static Scoreboard keyProvider = null;


	public static final GameRules.Key<GameRules.BooleanRule> creativeKeepInventory =
			GameRuleRegistry.register("creativeKeepInventory", GameRules.Category.PLAYER, GameRuleFactory.createBooleanRule(false));

	@Override
	public void onInitialize() {
		pkiCommandRegistration.registerCommands();


		// init the settings for a player when they join.
		ServerPlayConnectionEvents.JOIN.register( (handler, sender, server) -> {
			keyProvider = server.getScoreboard();
			CONFIG_COMPONENT = configKey.get(keyProvider);
			CONFIG_COMPONENT.updateTeam();

		} );
	}
}
