package me.atie.partialKeepinventory.commands;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import me.atie.partialKeepinventory.StatementInterpreter;
import me.atie.partialKeepinventory.partialKeepinventory;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

import static me.atie.partialKeepinventory.partialKeepinventory.CONFIG_COMPONENT;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class pkiCommandRegistration {

    private static void modeMessage(CommandContext<ServerCommandSource> ctx){
        ctx.getSource().sendFeedback( Text.literal(
                        "Keepinventory mode is set to " + CONFIG_COMPONENT.partialKeepinvMode().toString().toLowerCase()
                ), true);

    }

    public static void registerCommands() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
        dispatcher.register(literal("pki").requires(source -> source.hasPermissionLevel(4))
                .then( literal("enable").executes(
                        ctx -> {
                            CONFIG_COMPONENT.enableMod(true);
                            ctx.getSource().sendFeedback(Text.literal("Enabled partial keepinventory"), true);
                            return 1;
                        })
                )
                .then( literal("disable").executes(
                        ctx -> {
                            CONFIG_COMPONENT.enableMod(false);
                            ctx.getSource().sendFeedback(Text.literal("Disabled partial keepinventory"), true);
                            return 1;
                        })
                )
                .then(literal("info").executes(
                        ctx -> {

                            ctx.getSource().sendMessage(
                                    Text.literal("Please submit any issues or requests you have on my github.\n\n")
                                            .setStyle(
                                                    Style.EMPTY
                                                            .withClickEvent(
                                                            new ClickEvent(ClickEvent.Action.OPEN_URL, "https://github.com/atiedebee/Partial-Keepinventory"))
                                            )
                            );

                            ctx.getSource().sendMessage(Text.literal(
                                    "Mod enabled: " + CONFIG_COMPONENT.isEnabled() + "\n" +
                                            "Mode: " + CONFIG_COMPONENT.partialKeepinvMode().toString() + "\n\n" +
                                            "Inventory droprate: " + CONFIG_COMPONENT.inventoryDroprate() + "%\n" +
                                            "Common droprate: " + CONFIG_COMPONENT.getCommonDroprate() + "%\n" +
                                            "Uncommon droprate: " + CONFIG_COMPONENT.getUncommonDroprate() + "%\n" +
                                            "Rare droprate: " + CONFIG_COMPONENT.getRareDroprate() + "%\n" +
                                            "Epic droprate: " + CONFIG_COMPONENT.getEpicDroprate() + "%\n" +
                                            "Expression: \"" + CONFIG_COMPONENT.getExpression() + "\"\n" +
                                            ""));
                            return  1;
                        })
                )
                .then(literal("mode")
                        .then(literal("percentage")
                                .executes(ctx -> {
                                    CONFIG_COMPONENT.partialKeepinvMode(partialKeepinventory.KeepinvMode.PERCENTAGE);
                                    modeMessage(ctx);
                                    return 1;
                                })
                        )
                        .then(literal("rarity")
                                .executes(ctx -> {
                                    CONFIG_COMPONENT.partialKeepinvMode(partialKeepinventory.KeepinvMode.RARITY);
                                    modeMessage(ctx);
                                    return 1;
                                })
                        )
                        .then(literal("custom")
                                .executes(ctx -> {
                                    CONFIG_COMPONENT.partialKeepinvMode(partialKeepinventory.KeepinvMode.CUSTOM);
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
                        .then(literal("inventory")
                                .then(argument("percentage", IntegerArgumentType.integer(0, 100))
                                        .executes(ctx -> {
                                            final int x = IntegerArgumentType.getInteger(ctx, "percentage");
                                            ctx.getSource().sendMessage(Text.literal("Set inventory droprate to " + x + "%"));
                                            CONFIG_COMPONENT.inventoryDroprate(x);
                                            return 1;
                                        })
                                )
                                .executes(ctx -> {
                                    ctx.getSource().sendFeedback(Text.literal("Inventory droprate is set to " + CONFIG_COMPONENT.inventoryDroprate() + "%"), true);
                                    return 1;
                                })
                        )
                        .then(literal("common")
                                .then(argument("percentage", IntegerArgumentType.integer(0, 100))
                                        .executes(ctx -> {
                                            final int x = IntegerArgumentType.getInteger(ctx, "percentage");
                                            ctx.getSource().sendFeedback(Text.literal("Set common droprate to " + x + "%"), true);
                                            CONFIG_COMPONENT.setCommonDroprate(x);
                                            return 1;
                                        })
                                )
                                .executes(ctx -> {
                                    ctx.getSource().sendMessage(Text.literal("Common droprate is set to " + CONFIG_COMPONENT.getCommonDroprate() + "%"));
                                    return 1;
                                })
                        )
                        .then(literal("uncommon")
                                .then(argument("percentage", IntegerArgumentType.integer(0, 100))
                                        .executes(ctx -> {
                                            final int x = IntegerArgumentType.getInteger(ctx, "percentage");
                                            ctx.getSource().sendFeedback(Text.literal("Set uncommon droprate to " + x + "%"), true);
                                            CONFIG_COMPONENT.setUncommonDroprate(x);
                                            return 1;
                                        })
                                )
                                .executes(ctx -> {
                                    ctx.getSource().sendMessage(Text.literal("Uncommon droprate is set to " + CONFIG_COMPONENT.getUncommonDroprate() + "%"));
                                    return 1;
                                })
                        )
                        .then(literal("rare")
                                .then(argument("percentage", IntegerArgumentType.integer(0, 100))
                                        .executes(ctx -> {
                                            final int x = IntegerArgumentType.getInteger(ctx, "percentage");
                                            ctx.getSource().sendFeedback(Text.literal("Set rare droprate to " + x + "%"), true);
                                            CONFIG_COMPONENT.setRareDroprate(x);
                                            return 1;
                                        })
                                )
                                .executes(ctx -> {
                                    ctx.getSource().sendMessage(Text.literal("Rare droprate is set to " + CONFIG_COMPONENT.getRareDroprate() + "%"));
                                    return 1;
                                })
                        )
                        .then(literal("epic")
                                .then(argument("percentage", IntegerArgumentType.integer(0, 100))
                                        .executes(ctx -> {
                                            final int x = IntegerArgumentType.getInteger(ctx, "percentage");
                                            ctx.getSource().sendFeedback(Text.literal("Set epic droprate to " + x + "%"), true);
                                            CONFIG_COMPONENT.setEpicDroprate(x);
                                            return 1;
                                        })
                                )
                                .executes(ctx -> {
                                    ctx.getSource().sendMessage(Text.literal("Epic droprate is set to " + CONFIG_COMPONENT.getEpicDroprate() + "%"));
                                    return 1;
                                })
                        )
                )
                //TODO working list of saved players that isnt too bandwidth heavy
//                .then(literal("savedPlayers")
//                        .then(literal("list")
//                                .executes(ctx -> {
//                                    ctx.getSource().sendMessage(Text.literal("Players with regular keepinventory:"));
//                                    for(var name: CONFIG_COMPONENT.getPerPlayerKeepinventory() ){
//                                        ctx.getSource().sendMessage( Text.literal(" " + name));
//                                    }
//                                    return 1;
//                                })
//                        )
//                        .then(literal("add")
//                                .then(argument("players", EntityArgumentType.player())
//                                        .executes(ctx -> {
//                                            Collection<ServerPlayerEntity> players = EntityArgumentType.getPlayers(ctx, "players");
//                                            List<UUID> savedPlayers = CONFIG_COMPONENT.getPerPlayerKeepinventory();
//                                            String message = new String("");
//                                            for( var player: players ){
//                                                UUID uuid = player.getUuid();
//                                                if( !savedPlayers.contains(uuid) ){
//                                                    savedPlayers.add(uuid);
//                                                }
//                                                message += player.getEntityName() + ", "; //fancy formatting
//                                            };
//
//                                            ctx.getSource().sendFeedback(Text.literal("added " + message + "to the saved players"), true);
//                                            CONFIG_COMPONENT.setPerPlayerKeepinventory(savedPlayers);
//                                            return 1;
//                                        })
//                                )
//                        )
//                        .then(literal("remove")
//                                .then(argument("players", StringArgumentType.greedyString())
//                                        .executes(ctx -> {
//                                            Collection<ServerPlayerEntity> players = EntityArgumentType.getPlayers(ctx, "players");                                            List<UUID> savedPlayers = CONFIG_COMPONENT.getPerPlayerKeepinventory();
//                                            String message = new String("");
//
//                                            for( var player : players ) {
//                                                UUID uuid = player.getUuid();
//                                                String name = player.getEntityName();
//
//                                                if (!savedPlayers.contains(uuid)) {
//                                                    ctx.getSource().sendFeedback(Text.literal( name + " isn't in the list"), true);
//                                                    return 0;
//                                                }
//                                                savedPlayers.remove(uuid);
//                                                message += name + ", ";
//                                            }
//                                            ctx.getSource().sendFeedback(Text.literal("removed " + message + "from the saved players"), true);
//                                            CONFIG_COMPONENT.setPerPlayerKeepinventory(savedPlayers);
//                                            return 1;
//                                        })
//                                )
//                        )
//
//                )
                .then(literal("expression")
                        .then(literal("set")
                                .then(argument("expression", StringArgumentType.greedyString())
                                    .executes(ctx -> {
                                        String expression = StringArgumentType.getString(ctx, "expression");
                                        ctx.getSource().sendMessage(Text.literal("Saved the expression \"" + expression + "\""));
                                        CONFIG_COMPONENT.setExpression(expression);
//                                        CONFIG_COMPONENT.save();
                                        return 1;
                                    })
                                )
                        )
                        .then(literal("help")
                                .executes(ctx -> {
                                    ctx.getSource().sendMessage(Text.literal(StatementInterpreter.info));
                                    return 1;
                                })
                        )

                )//TODO: proper help commands for each part of the mod
                .executes(ctx -> {
                    ctx.getSource().sendMessage(Text.literal(
                            """
                                    Enable/ disable the mod:
                                    > /pki [enable|disable]
                                    
                                    Set the drop behaviour.
                                    percentage: all items are dropped equally
                                    rarity:     droprates are based off of the rarity of items.
                                    custom:     Use your own formula for determining how many items to drop.
                                    > /pki mode [percentage / rarity / custom]
                                    
                                    Set droprates
                                    > /pki droprate [inventory / common / uncommon / rare / epic] <percentage>
                                    
                                    Use your own formula for droprates.
                                    > /pki expression set [expression]
                                    
                                    Show variables you may use for your formulas.
                                    > /pki expression info
                                    
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
