package me.atie.partialKeepinventory.commands;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import me.atie.partialKeepinventory.KeepXPMode;
import me.atie.partialKeepinventory.KeepinvMode;
import me.atie.partialKeepinventory.PartialKeepInventory;
import me.atie.partialKeepinventory.formula.InventoryDroprateFormula;
import me.atie.partialKeepinventory.formula.XpDroprateFormula;
import me.atie.partialKeepinventory.settings.pkiSettings;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.argument.GameProfileArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;

import java.util.Collection;

import static me.atie.partialKeepinventory.PartialKeepInventory.CONFIG;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class pkiCommandRegistration {

    private static void modeMessage(CommandContext<ServerCommandSource> ctx) {
        ctx.getSource().sendFeedback(Text.literal(
                "Keepinventory mode is set to " + CONFIG.getPartialKeepinvMode().toString()
        ), true);

    }

    private static void percentMessage(CommandContext<ServerCommandSource> ctx, String valName, int val) {
        ctx.getSource().sendFeedback(Text.literal(
                valName + " is set to " + val + "%"
        ), true);
    }

    private static void syncSettings(CommandManager.RegistrationEnvironment environment){
//        if( environment.dedicated ){
            pkiSettings.updateServerConfig();
//        }
    }


    private static int sendMessage(CommandContext<ServerCommandSource> ctx, Text text){
        ctx.getSource().sendMessage(text);
        return 1;
    }

    public static void registerCommands() {
        final Text xpDroprateTextHelp = Text.literal(
        """
        Xp droprates show how much experience you drop and lose when dying:
        Drop:
            This is how much XP is DROPPED when dying.
        Loss:
            This is the percentage of your DROPPED XP that disappears on death.
        """
        );
        final Text xpModeTextHelp = Text.literal(
        """
        These settings change how xp is dropped:
        Levels:
            Each level is graded the same.
        Points:
            Use XP points as the metric for how much to drop.
        Each of these can be used with custom expressions as well.
        """
        );
        final Text invDroprateTextHelp = Text.literal(
        """
        Droprates dictate how likely certain items are to be dropped:
        Common, Uncommon, Rare, Epic:
            These are Minecraft's 4 rarities. Each indicated by the color of an item's name.
        Inventory:
            The base droprate when using "staitc" as your droprate.
        """
        );
        final Text modeTextHelp = Text.literal(
        """
        Modes dictate how items are dropped:
        Static:
            All items are dropped equally. The percentage used is the "Inventory Droprate".
        Vanilla:
            Items are dropped like they are in vanilla.
        Rarity:
            Items are dropped based off of their rarity.
        Custom"
            Item drops are calculated using an expression.
        """
        );



        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
                dispatcher.register(literal("pki").requires(source -> source.hasPermissionLevel(2) )
                        .then(literal("enable").executes(
                                ctx -> {
                                    CONFIG.setEnableMod(true);
                                    syncSettings(environment);
                                    ctx.getSource().sendFeedback(Text.literal("Enabled partial keepinventory"), true);
                                    return 1;
                                })
                        )
                        .then(literal("disable").executes(
                                ctx -> {
                                    CONFIG.setEnableMod(false);
                                    syncSettings(environment);
                                    ctx.getSource().sendFeedback(Text.literal("Disabled partial keepinventory"), true);
                                    return 1;
                                })
                        )
                        .then(literal("info").executes(
                                ctx -> {
                                    String message = "Mod " + (CONFIG.getEnableMod() ? "enabled" : "disabled") + "\n";

                                    message += "---Inventory---\n" + ">Keepinventory mode: " + CONFIG.getPartialKeepinvMode().toString() + "\n";

                                    // only show relevant information
                                    message += switch (CONFIG.getPartialKeepinvMode()) {
                                        case STATIC ->
                                                ">Inventory droprate: " + CONFIG.getInventoryDroprate() + "%\n";

                                        case RARITY ->
                                                ">Common droprate: " + CONFIG.getCommonDroprate() + "%\n" +
                                                        ">Uncommon droprate: " + CONFIG.getUncommonDroprate() + "%\n" +
                                                        ">Rare droprate: " + CONFIG.getRareDroprate() + "%\n" +
                                                        ">Epic droprate: " + CONFIG.getEpicDroprate() + "%\n";
                                        case CUSTOM -> ">Expression: \"" + CONFIG.getExpression() + "\"\n\n" +
                                                ">Inventory droprate: " + CONFIG.getInventoryDroprate() + "%\n" +
                                                ">Common droprate: " + CONFIG.getCommonDroprate() + "%\n" +
                                                ">Uncommon droprate: " + CONFIG.getUncommonDroprate() + "%\n" +
                                                ">Rare droprate: " + CONFIG.getRareDroprate() + "%\n" +
                                                ">Epic droprate: " + CONFIG.getEpicDroprate() + "%\n";
                                        case VANILLA -> "";
                                    };

                                    message += "\n---XP---\n";
                                    message += ">Mode: " + CONFIG.getKeepxpMode().toString() + "\n";
                                    message += switch (CONFIG.getKeepxpMode()) {
                                        case STATIC_POINTS, STATIC_LEVELS ->
                                                ">Loss percentage: " + CONFIG.getXpLoss() + "%\n" +
                                                ">Drop percentage: " + CONFIG.getXpDrop() + "%\n";
                                        case VANILLA -> "";
                                        case CUSTOM_LEVELS, CUSTOM_POINTS ->
                                                ">Loss percentage: " + CONFIG.getXpLoss() + "%\n" +
                                                ">Drop percentage: " + CONFIG.getXpDrop() + "%\n" +
                                                ">Drop expression: \"" + CONFIG.getXpDropExpression() + "\"\n" +
                                                ">Loss expression: \"" + CONFIG.getXpLossExpression() + "\"\n";
                                    };

                                    ctx.getSource().sendMessage(Text.literal(message));
                                    syncSettings(environment);
                                    return 1;
                                })
                        )
                        .then(literal("inv")
                                .then(literal("mode")
                                        .then(literal("static")
                                                .executes(ctx -> {
                                                    CONFIG.setPartialKeepinvMode(KeepinvMode.STATIC);
                                                    modeMessage(ctx);
                                                    syncSettings(environment);
                                                    return 1;
                                                })
                                        )
                                        .then(literal("rarity")
                                                .executes(ctx -> {
                                                    CONFIG.setPartialKeepinvMode(KeepinvMode.RARITY);
                                                    modeMessage(ctx);
                                                    syncSettings(environment);
                                                    return 1;
                                                })
                                        )
                                        .then(literal("custom")
                                                .executes(ctx -> {
                                                    CONFIG.setPartialKeepinvMode(KeepinvMode.CUSTOM);
                                                    modeMessage(ctx);
                                                    syncSettings(environment);
                                                    return 1;
                                                })
                                        )
                                        .then(literal("vanilla")
                                                .executes(ctx -> {
                                                    CONFIG.setPartialKeepinvMode(KeepinvMode.VANILLA);
                                                    modeMessage(ctx);
                                                    syncSettings(environment);
                                                    return 1;
                                                })
                                        )
                                        .executes(ctx -> {
                                            modeMessage(ctx);
                                            return 1;
                                        })
                                )
                                .then(literal("droprate")
                                        .then(literal("static")
                                                .then(argument("percentage", IntegerArgumentType.integer(0, 100))
                                                        .executes(ctx -> {
                                                            final int x = IntegerArgumentType.getInteger(ctx, "percentage");
                                                            percentMessage(ctx, "Inventory droprate", x);
                                                            CONFIG.setInventoryDroprate(x);
                                                            syncSettings(environment);
                                                            return 1;
                                                        })
                                                )
                                                .executes(ctx -> {
                                                    percentMessage(ctx, "Inventory droprate", CONFIG.getInventoryDroprate());
                                                    return 1;
                                                })
                                        )
                                        .then(literal("common")
                                                .then(argument("percentage", IntegerArgumentType.integer(0, 100))
                                                        .executes(ctx -> {
                                                            final int x = IntegerArgumentType.getInteger(ctx, "percentage");
                                                            percentMessage(ctx, "common droprate", x);
                                                            CONFIG.setCommonDroprate(x);
                                                            syncSettings(environment);
                                                            return 1;
                                                        })
                                                )
                                                .executes(ctx -> {
                                                    percentMessage(ctx, "common droprate", CONFIG.getCommonDroprate());
                                                    return 1;
                                                })
                                        )
                                        .then(literal("uncommon")
                                                .then(argument("percentage", IntegerArgumentType.integer(0, 100))
                                                        .executes(ctx -> {
                                                            final int x = IntegerArgumentType.getInteger(ctx, "percentage");
                                                            percentMessage(ctx, "uncommon droprate", x);
                                                            CONFIG.setUncommonDroprate(x);
                                                            syncSettings(environment);
                                                            return 1;
                                                        })
                                                )
                                                .executes(ctx -> {
                                                    percentMessage(ctx, "uncommon droprate", CONFIG.getUncommonDroprate());
                                                    return 1;
                                                })
                                        )
                                        .then(literal("rare")
                                                .then(argument("percentage", IntegerArgumentType.integer(0, 100))
                                                        .executes(ctx -> {
                                                            final int x = IntegerArgumentType.getInteger(ctx, "percentage");
                                                            percentMessage(ctx, "rare droprate", x);
                                                            CONFIG.setRareDroprate(x);
                                                            syncSettings(environment);
                                                            return 1;
                                                        })
                                                )
                                                .executes(ctx -> {
                                                    percentMessage(ctx, "rare droprate", CONFIG.getRareDroprate());
                                                    return 1;
                                                })
                                        )
                                        .then(literal("epic")
                                                .then(argument("percentage", IntegerArgumentType.integer(0, 100))
                                                        .executes(ctx -> {
                                                            final int x = IntegerArgumentType.getInteger(ctx, "percentage");
                                                            percentMessage(ctx, "epic droprate", x);
                                                            CONFIG.setEpicDroprate(x);
                                                            syncSettings(environment);
                                                            return 1;
                                                        })
                                                )
                                                .executes(ctx -> {
                                                    percentMessage(ctx, "epic droprate", CONFIG.getEpicDroprate());
                                                    return 1;
                                                })
                                        )
                                )
                                .then(literal("savedPlayers")
                                        .then(literal("list")
                                                .executes(ctx -> {
                                                    if( CONFIG.getSavedPlayers().isEmpty() ){
                                                        ctx.getSource().sendMessage(Text.literal("No players with regular keepinventory"));
                                                        return 1;
                                                    }

                                                    ctx.getSource().sendMessage(Text.literal("Players with regular keepinventory:"));
                                                    for (var name : CONFIG.getSavedPlayers()) {
                                                        ctx.getSource().sendMessage(Text.literal("> " + name));
                                                    }
                                                    return 1;
                                                })
                                        )
                                        .then(literal("add")
                                                .then(argument("players", GameProfileArgumentType.gameProfile())
                                                        .executes(ctx -> {
                                                            Collection<GameProfile> players = GameProfileArgumentType.getProfileArgument(ctx, "players");
                                                            StringBuilder message = new StringBuilder();
                                                            StringBuilder notAdded = new StringBuilder();

                                                            for (var player : players) {
                                                                try {
                                                                    CONFIG.addSavedPlayer(player.getName());
                                                                    message.append(player.getName()).append(", "); //fancy formatting
                                                                }catch (RuntimeException e) {
                                                                    notAdded.append(player.getName()).append(", ");
                                                                }
                                                            }

                                                            if( !message.isEmpty() ) {
                                                                ctx.getSource().sendFeedback(Text.literal("Added " + message + "to the saved players"), true);
                                                            }
                                                            if( !notAdded.isEmpty()) {
                                                                ctx.getSource().sendFeedback(Text.literal(notAdded + " were ignored, for they are in the list already."), false);
                                                            }
                                                            syncSettings(environment);
                                                            return 1;
                                                        })
                                                )
                                        )
                                        .then(literal("remove")
                                                .then(argument("player", GameProfileArgumentType.gameProfile())
                                                        .executes(ctx -> {
                                                            Collection<GameProfile> players = GameProfileArgumentType.getProfileArgument(ctx, "player");
                                                            StringBuilder message = new StringBuilder();

                                                            for( var player: players ) {
                                                                try {
                                                                    CONFIG.removeSavedPlayer(player.getName());
                                                                    message.append(player.getName()).append(", ");
                                                                } catch (RuntimeException e) {
                                                                    PartialKeepInventory.LOGGER.info(e.getMessage());
                                                                }
                                                            }
                                                            if( !message.isEmpty() ) {
                                                                ctx.getSource().sendFeedback(Text.literal("Removed " + message + "from the saved players."), true);
                                                            }
                                                            syncSettings(environment);
                                                            return 1;
                                                        })
                                                )
                                        )

                                )
                                .then(literal("expression")
                                        .then(literal("set")
                                                .then(argument("expression", StringArgumentType.greedyString())
                                                        .executes(ctx -> {
                                                            String expression = StringArgumentType.getString(ctx, "expression");
                                                            try {
                                                                var formula = new InventoryDroprateFormula(ctx.getSource().getPlayer());
                                                                formula.testExpression(expression);
                                                                ctx.getSource().sendMessage(Text.literal("Saved the expression \"" + expression + "\""));
                                                                CONFIG.setExpression(expression);
                                                                syncSettings(environment);
                                                            }catch(Exception e){
                                                                PartialKeepInventory.LOGGER.error("Invalid expression: " + e.getMessage());
                                                                ctx.getSource().sendMessage(Text.literal("Invalid expression: " + e.getMessage()).setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAA0000))));
                                                            }

                                                            return 1;
                                                        })
                                                )
                                        )
                                        .then(literal("help")
                                                .executes(ctx -> {
                                                    ctx.getSource().sendMessage(Text.literal(InventoryDroprateFormula.info));
                                                    return 1;
                                                })
                                        )
                                        .executes(ctx -> {
                                            ctx.getSource().sendMessage(Text.literal("The custom droprate formula is set to \"" + CONFIG.getExpression() + "\""));
                                            return 1;
                                        })

                                )
                        )
                        .then(literal("xp")
                                .then(literal("mode")
                                        .then(literal("vanilla")
                                                .executes(ctx -> {
                                                    CONFIG.setKeepxpMode(KeepXPMode.VANILLA);
                                                    syncSettings(environment);
                                                    return 1;
                                                })
                                        )
                                        .then(literal("static-level")
                                                .executes(ctx -> {
                                                    CONFIG.setKeepxpMode(KeepXPMode.STATIC_LEVELS);
                                                    ctx.getSource().sendMessage(Text.literal("XP mode is set to " + CONFIG.getKeepxpMode().toString()));
                                                    syncSettings(environment);
                                                    return 1;
                                                })
                                        )
                                        .then(literal("static-points")
                                                .executes(ctx -> {
                                                    CONFIG.setKeepxpMode(KeepXPMode.STATIC_POINTS);
                                                    ctx.getSource().sendMessage(Text.literal("XP mode is set to " + CONFIG.getKeepxpMode().toString()));
                                                    syncSettings(environment);
                                                    return 1;
                                                })
                                        )
                                        .then(literal("custom-points")
                                                .executes(ctx -> {
                                                    CONFIG.setKeepxpMode(KeepXPMode.CUSTOM_POINTS);
                                                    ctx.getSource().sendMessage(Text.literal("XP mode is set to " + CONFIG.getKeepxpMode().toString()));
                                                    syncSettings(environment);
                                                    return 1;
                                                })
                                        )
                                        .then(literal("custom-level")
                                                .executes(ctx -> {
                                                    CONFIG.setKeepxpMode(KeepXPMode.CUSTOM_LEVELS);
                                                    ctx.getSource().sendMessage(Text.literal("XP mode is set to " + CONFIG.getKeepxpMode().toString()));
                                                    syncSettings(environment);
                                                    return 1;
                                                })
                                        )
                                        .executes(ctx -> {
                                            ctx.getSource().sendMessage(Text.literal("XP mode is set to " + CONFIG.getKeepxpMode().toString()));
                                            return 1;
                                        })
                                )
                                .then(literal("droprate")
                                        .then(literal("loss")
                                                .then(argument("percent", IntegerArgumentType.integer(0, 100))
                                                        .executes(ctx -> {
                                                            int percent = IntegerArgumentType.getInteger(ctx, "percent");
                                                            CONFIG.setXpLoss(percent);
                                                            percentMessage(ctx, "xp loss-rate", percent);
                                                            syncSettings(environment);
                                                            return 1;
                                                        })
                                                )
                                                .executes(ctx -> {
                                                    percentMessage(ctx, "xp loss-rate", CONFIG.getXpLoss());
                                                    return 1;
                                                })
                                        )
                                        .then(literal("drop")
                                                .then(argument("percent", IntegerArgumentType.integer(0, 100))
                                                        .executes(ctx -> {
                                                            int percent = IntegerArgumentType.getInteger(ctx, "percent");
                                                            CONFIG.setXpDrop(percent);
                                                            percentMessage(ctx, "xp droprate", percent);
                                                            syncSettings(environment);
                                                            return 1;
                                                        })
                                                )
                                                .executes(ctx -> {
                                                    percentMessage(ctx, "xp droprate", CONFIG.getXpDrop());
                                                    return 1;
                                                })
                                        )
                                )
                                .then(literal("expression")
                                        .then(literal("loss")
                                                .then(argument("expression", StringArgumentType.greedyString())
                                                        .executes(ctx -> {
                                                            String expression = StringArgumentType.getString(ctx, "expression");
                                                            try {
                                                                var formula = new XpDroprateFormula(ctx.getSource().getPlayer());
                                                                formula.testExpression(expression);
                                                                ctx.getSource().sendMessage(Text.literal(
                                                                        "Saved the expression \"" + expression + "\""));
                                                                CONFIG.setXpLossExpression(expression);
                                                                syncSettings(environment);
                                                            }catch(Exception e){
                                                                PartialKeepInventory.LOGGER.error("Invalid expression: " + e.getMessage());
                                                                ctx.getSource().sendMessage(Text.literal("Invalid expression: " + e.getMessage()).setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAA0000))));
                                                            }
                                                            return 1;
                                                        })
                                                )
                                        )
                                        .then(literal("drop")
                                                .then(argument("expression", StringArgumentType.greedyString())
                                                        .executes(ctx -> {
                                                            String expression = StringArgumentType.getString(ctx, "expression");
                                                            try {
                                                                var formula = new XpDroprateFormula(ctx.getSource().getPlayer());
                                                                formula.testExpression(expression);

                                                                ctx.getSource().sendMessage(Text.literal(
                                                                        "Saved the expression \"" + expression + "\""));

                                                                CONFIG.setXpDropExpression(expression);

                                                                syncSettings(environment);
                                                            }catch(Exception e){
                                                                PartialKeepInventory.LOGGER.error("Invalid expression: " + e.getMessage());
                                                                ctx.getSource().sendMessage(Text.literal("Invalid expression: " + e.getMessage()).setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAA0000))));
                                                            }
                                                            return 1;
                                                        })
                                                )
                                        )
                                )
                        )
                        .then(literal("help")
                                .then(literal("inv-mode")
                                        .executes(ctx -> sendMessage(ctx, modeTextHelp))
                                )
                                .then(literal("xp-mode")
                                        .executes(ctx -> sendMessage(ctx, xpModeTextHelp))
                                )
                                .then(literal("inv-droprate")
                                        .executes(ctx -> sendMessage(ctx, invDroprateTextHelp))
                                )
                                .then(literal("xp-droprate")
                                        .executes(ctx -> sendMessage(ctx, xpDroprateTextHelp))
                                )

                        )
                        //TODO: proper help commands for each part of the mod
                        .executes(ctx -> {
                            ctx.getSource().sendMessage(Text.literal(
                                    """
                                            Enable/ disable the mod:
                                            > /pki [enable|disable]
                                                                                
                                            Set the drop behaviour.
                                            percentage: all items are dropped equally
                                            rarity:     Droprates are based off of the rarity of items.
                                            custom:     Use your own formula for determining how many items to drop.
                                            vanilla:    Vanilla drop behaviour
                                            > /pki inv mode [percentage / rarity / custom / vanilla]
                                                                                
                                            Set droprates
                                            > /pki inv droprate [static / common / uncommon / rare / epic] <percentage>
                                                                                
                                            Use your own formula for droprates.
                                            > /pki inv expression set [expression]
                                                                                
                                            Show variables you may use for your formulas.
                                            > /pki inv expression info
                                                                                
                                            Set xp drop behaviour
                                            /pki xp mode [static-points / static-level / vanilla]
                                                                                
                                            /pki xp droprate [loss / drop] <percentage>
                                                                                
                                            Show settings
                                            > /pki info
                                            """
                            ));
                            return 1;
                        })

                )
        );

    }
}