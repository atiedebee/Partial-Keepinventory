package me.atie.partialKeepinventory;

import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import me.atie.partialKeepinventory.commands.pkiCommandRegistration;
import me.atie.partialKeepinventory.component.pkiComponent;
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
		PERCENTAGE, RARITY, CUSTOM
	}

	public enum KeepXPMode {
		PERCENTAGE, VANILLA
	}


	public static pkiComponent CONFIG_COMPONENT = null;

	public static String getID(){
		return "partial-keepinv";
	}


	public static final ComponentKey<pkiComponent> configKey = ComponentRegistry.getOrCreate(
			new Identifier(partialKeepinventory.getID(), "config"),
			pkiComponent.class);
	public static Scoreboard keyProvider = null;



	public static final GameRules.Key<GameRules.BooleanRule> creativeKeepInventory =
			GameRuleRegistry.register("creativeKeepInventory", GameRules.Category.PLAYER, GameRuleFactory.createBooleanRule(false));

	@Override
	public void onInitialize() {
		pkiCommandRegistration.registerCommands();

		// init scoreboard thingy
		ServerPlayConnectionEvents.JOIN.register( (handler, sender, server) -> {
			keyProvider = server.getScoreboard();
			CONFIG_COMPONENT = configKey.get(keyProvider);
		} );
	}

}
