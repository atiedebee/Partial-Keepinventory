package me.atie.partialKeepinventory;

import com.mojang.brigadier.CommandDispatcher;
import me.atie.partialKeepinventory.gui.ParentSettingsScreen;
import me.atie.partialKeepinventory.network.ClientListeners;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandRegistryAccess;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class ClientInitializer implements ClientModInitializer {
    private static void registerClientCommands(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess registryAccess) {
        dispatcher.register(literal("pki-gui")
            .executes(context -> {
                var client = context.getSource().getClient();
                client.send(() -> client.setScreen(new ParentSettingsScreen(null)));
                return 1;
            })
        );
    }

    @Override
    public void onInitializeClient() {
        PartialKeepInventory.environment = EnvType.CLIENT;
        ClientListeners.init();
        ClientCommandRegistrationCallback.EVENT.register(ClientInitializer::registerClientCommands);
    }

}
