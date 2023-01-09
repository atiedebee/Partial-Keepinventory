package me.atie.partialKeepinventory.commands;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import me.atie.partialKeepinventory.KeepXPMode;
import me.atie.partialKeepinventory.KeepinvMode;
import me.atie.partialKeepinventory.formula.InventoryDroprateFormula;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

import java.util.Collection;

import static me.atie.partialKeepinventory.PartialKeepInventory.CONFIG_COMPONENT;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class pkiCommandRegistration {

    private static void modeMessage(CommandContext<ServerCommandSource> ctx) {
        ctx.getSource().sendFeedback(Text.literal(
                "Keepinventory mode is set to " + CONFIG_COMPONENT.getPartialKeepinvMode().toString()
        ), true);

    }

    private static void percentMessage(CommandContext<ServerCommandSource> ctx, String valName, int val) {
        ctx.getSource().sendFeedback(Text.literal(
                valName + " is set to " + val + "%"
        ), true);
    }


    public static void registerCommands() {

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
                dispatcher.register(literal("pki").requires(source -> source.hasPermissionLevel(4))
                        .then(literal("enable").executes(
                                ctx -> {
                                    CONFIG_COMPONENT.setEnableMod(true);
                                    ctx.getSource().sendFeedback(Text.literal("Enabled partial keepinventory"), true);
                                    return 1;
                                })
                        )
                        .then(literal("disable").executes(
                                ctx -> {
                                    CONFIG_COMPONENT.setEnableMod(false);
                                    ctx.getSource().sendFeedback(Text.literal("Disabled partial keepinventory"), true);
                                    return 1;
                                })
                        )
                        .then(literal("info").executes(
                                ctx -> {

                                    ctx.getSource().sendMessage(
                                            Text.literal("Please submit any issues or requests you have on github.\n\n")
                                                    .setStyle(
                                                            Style.EMPTY
                                                                    .withClickEvent(
                                                                            new ClickEvent(ClickEvent.Action.OPEN_URL, "https://github.com/atiedebee/Partial-Keepinventory"))
                                                    )
                                    );
                                    String message = "Mod " + (CONFIG_COMPONENT.getEnableMod() ? "enabled" : "disabled") + "\n";

                                    message += "---Inventory---\n" + ">Keepinventory mode: " + CONFIG_COMPONENT.getPartialKeepinvMode().toString() + "\n";

                                    // only show relevant information
                                    message += switch (CONFIG_COMPONENT.getPartialKeepinvMode()) {
                                        case STATIC ->
                                                ">Inventory droprate: " + CONFIG_COMPONENT.getInventoryDroprate() + "%\n";

                                        case RARITY ->
                                                ">Common droprate: " + CONFIG_COMPONENT.getCommonDroprate() + "%\n" +
                                                        ">Uncommon droprate: " + CONFIG_COMPONENT.getUncommonDroprate() + "%\n" +
                                                        ">Rare droprate: " + CONFIG_COMPONENT.getRareDroprate() + "%\n" +
                                                        ">Epic droprate: " + CONFIG_COMPONENT.getEpicDroprate() + "%\n";
                                        case CUSTOM -> ">Expression: \"" + CONFIG_COMPONENT.getExpression() + "\"\n\n" +
                                                ">Inventory droprate: " + CONFIG_COMPONENT.getInventoryDroprate() + "%\n" +
                                                ">Common droprate: " + CONFIG_COMPONENT.getCommonDroprate() + "%\n" +
                                                ">Uncommon droprate: " + CONFIG_COMPONENT.getUncommonDroprate() + "%\n" +
                                                ">Rare droprate: " + CONFIG_COMPONENT.getRareDroprate() + "%\n" +
                                                ">Epic droprate: " + CONFIG_COMPONENT.getEpicDroprate() + "%\n";
                                        case VANILLA -> "";
                                    };

                                    message += "\n---XP---\n";
                                    message += ">Mode: " + CONFIG_COMPONENT.getKeepxpMode().toString() + "\n";
                                    message += switch (CONFIG_COMPONENT.getKeepxpMode()) {
                                        case STATIC_POINTS, STATIC_LEVEL -> "" +
                                                ">Loss percentage: " + CONFIG_COMPONENT.getXpLoss() + "%\n" +
                                                ">Drop percentage: " + CONFIG_COMPONENT.getXpDrop() + "%\n";
                                        case VANILLA -> "";
                                    };

                                    ctx.getSource().sendMessage(Text.literal(message));

                                    return 1;
                                })
                        )
                        .then(literal("inv")
                                .then(literal("mode")
                                        .then(literal("static")
                                                .executes(ctx -> {
                                                    CONFIG_COMPONENT.setPartialKeepinvMode(KeepinvMode.STATIC);
                                                    modeMessage(ctx);
                                                    return 1;
                                                })
                                        )
                                        .then(literal("rarity")
                                                .executes(ctx -> {
                                                    CONFIG_COMPONENT.setPartialKeepinvMode(KeepinvMode.RARITY);
                                                    modeMessage(ctx);
                                                    return 1;
                                                })
                                        )
                                        .then(literal("custom")
                                                .executes(ctx -> {
                                                    CONFIG_COMPONENT.setPartialKeepinvMode(KeepinvMode.CUSTOM);
                                                    modeMessage(ctx);
                                                    return 1;
                                                })
                                        )
                                        .then(literal("vanilla")
                                                .executes(ctx -> {
                                                    CONFIG_COMPONENT.setPartialKeepinvMode(KeepinvMode.VANILLA);
                                                    modeMessage(ctx);
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
                                                            CONFIG_COMPONENT.setInventoryDroprate(x);
                                                            return 1;
                                                        })
                                                )
                                                .executes(ctx -> {
                                                    percentMessage(ctx, "Inventory droprate", CONFIG_COMPONENT.getInventoryDroprate());
                                                    return 1;
                                                })
                                        )
                                        .then(literal("common")
                                                .then(argument("percentage", IntegerArgumentType.integer(0, 100))
                                                        .executes(ctx -> {
                                                            final int x = IntegerArgumentType.getInteger(ctx, "percentage");
                                                            percentMessage(ctx, "common droprate", x);
                                                            CONFIG_COMPONENT.setCommonDroprate(x);
                                                            return 1;
                                                        })
                                                )
                                                .executes(ctx -> {
                                                    percentMessage(ctx, "common droprate", CONFIG_COMPONENT.getCommonDroprate());
                                                    return 1;
                                                })
                                        )
                                        .then(literal("uncommon")
                                                .then(argument("percentage", IntegerArgumentType.integer(0, 100))
                                                        .executes(ctx -> {
                                                            final int x = IntegerArgumentType.getInteger(ctx, "percentage");
                                                            percentMessage(ctx, "uncommon droprate", x);
                                                            CONFIG_COMPONENT.setUncommonDroprate(x);
                                                            return 1;
                                                        })
                                                )
                                                .executes(ctx -> {
                                                    percentMessage(ctx, "uncommon droprate", CONFIG_COMPONENT.getUncommonDroprate());
                                                    return 1;
                                                })
                                        )
                                        .then(literal("rare")
                                                .then(argument("percentage", IntegerArgumentType.integer(0, 100))
                                                        .executes(ctx -> {
                                                            final int x = IntegerArgumentType.getInteger(ctx, "percentage");
                                                            percentMessage(ctx, "rare droprate", x);
                                                            CONFIG_COMPONENT.setRareDroprate(x);
                                                            return 1;
                                                        })
                                                )
                                                .executes(ctx -> {
                                                    percentMessage(ctx, "rare droprate", CONFIG_COMPONENT.getRareDroprate());
                                                    return 1;
                                                })
                                        )
                                        .then(literal("epic")
                                                .then(argument("percentage", IntegerArgumentType.integer(0, 100))
                                                        .executes(ctx -> {
                                                            final int x = IntegerArgumentType.getInteger(ctx, "percentage");
                                                            percentMessage(ctx, "epic droprate", x);
                                                            CONFIG_COMPONENT.setEpicDroprate(x);
                                                            return 1;
                                                        })
                                                )
                                                .executes(ctx -> {
                                                    percentMessage(ctx, "epic droprate", CONFIG_COMPONENT.getEpicDroprate());
                                                    return 1;
                                                })
                                        )
                                )
                                .then(literal("savedPlayers")
                                        .then(literal("list")
                                                .executes(ctx -> {
                                                    ctx.getSource().sendMessage(Text.literal("Players with regular keepinventory:"));
                                                    for (var name : CONFIG_COMPONENT.savedPlayersTeam.getPlayerList()) {
                                                        ctx.getSource().sendMessage(Text.literal("> " + name));
                                                    }
                                                    return 1;
                                                })
                                        )
                                        .then(literal("add")
                                                .then(argument("players", EntityArgumentType.players())
                                                        .executes(ctx -> {
                                                            Collection<ServerPlayerEntity> players = EntityArgumentType.getPlayers(ctx, "players");
                                                            String message = new String("");


                                                            for (var player : players) {
                                                                CONFIG_COMPONENT.scoreboard.addPlayerToTeam(player.getEntityName(), CONFIG_COMPONENT.savedPlayersTeam);
                                                                message += player.getEntityName() + ", "; //fancy formatting
                                                            }

                                                            ctx.getSource().sendFeedback(Text.literal("added " + message + "to the saved players"), true);
                                                            return 1;
                                                        })
                                                )
                                        )
                                        .then(literal("remove")
                                                .then(argument("player", StringArgumentType.greedyString())
                                                        .executes(ctx -> {
                                                            String name = StringArgumentType.getString(ctx, "player");

                                                            try {
                                                                CONFIG_COMPONENT.scoreboard.removePlayerFromTeam(name, CONFIG_COMPONENT.savedPlayersTeam);
                                                                ctx.getSource().sendFeedback(Text.literal("removed " + name + "from the saved players"), true);
                                                            } catch (Exception e) {
                                                                ctx.getSource().sendFeedback(Text.literal(name + " isn't in the list"), false);
                                                            }
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
                                                            ctx.getSource().sendMessage(Text.literal("Saved the expression \"" + expression + "\""));
                                                            CONFIG_COMPONENT.setExpression(expression);
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
                                            ctx.getSource().sendMessage(Text.literal("The custom droprate formula is set to \"" + CONFIG_COMPONENT.getExpression() + "\""));
                                            return 1;
                                        })

                                )
                        )
                        .then(literal("xp")
                                .then(literal("mode")
                                        .then(literal("vanilla")
                                                .executes(ctx -> {
                                                    CONFIG_COMPONENT.setKeepxpMode(KeepXPMode.VANILLA);
                                                    return 1;
                                                })
                                        )
                                        .then(literal("static-level")
                                                .executes(ctx -> {
                                                    CONFIG_COMPONENT.setKeepxpMode(KeepXPMode.STATIC_LEVEL);
                                                    ctx.getSource().sendMessage(Text.literal("XP mode is set to " + CONFIG_COMPONENT.getKeepxpMode().toString()));
                                                    return 1;
                                                })
                                        )
                                        .then(literal("static-points")
                                                .executes(ctx -> {
                                                    CONFIG_COMPONENT.setKeepxpMode(KeepXPMode.STATIC_POINTS);
                                                    ctx.getSource().sendMessage(Text.literal("XP mode is set to " + CONFIG_COMPONENT.getKeepxpMode().toString()));
                                                    return 1;
                                                })
                                        )
                                        .executes(ctx -> {
                                            ctx.getSource().sendMessage(Text.literal("XP mode is set to " + CONFIG_COMPONENT.getKeepxpMode().toString()));
                                            return 1;
                                        })
                                )
                                .then(literal("droprate")
                                        .then(literal("loss")
                                                .then(argument("percent", IntegerArgumentType.integer(0, 100))
                                                        .executes(ctx -> {
                                                            int percent = IntegerArgumentType.getInteger(ctx, "percent");
                                                            CONFIG_COMPONENT.setXpLoss(percent);
                                                            percentMessage(ctx, "xp loss-rate", percent);
                                                            return 1;
                                                        })
                                                )
                                                .executes(ctx -> {
                                                    percentMessage(ctx, "xp loss-rate", CONFIG_COMPONENT.getXpLoss());
                                                    return 1;
                                                })
                                        )
                                        .then(literal("drop")
                                                .then(argument("percent", IntegerArgumentType.integer(0, 100))
                                                        .executes(ctx -> {
                                                            int percent = IntegerArgumentType.getInteger(ctx, "percent");
                                                            CONFIG_COMPONENT.setXpDrop(percent);
                                                            percentMessage(ctx, "xp droprate", percent);
                                                            return 1;
                                                        })
                                                )
                                                .executes(ctx -> {
                                                    percentMessage(ctx, "xp droprate", CONFIG_COMPONENT.getXpDrop());
                                                    return 1;
                                                })
                                        )
                                )
                        )//TODO: proper help commands for each part of the mod
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
