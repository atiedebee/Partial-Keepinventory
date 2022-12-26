package me.atie.partialKeepinventory.commands;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import me.atie.partialKeepinventory.StatementInterpreter;
import me.atie.partialKeepinventory.partialKeepinventory;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.util.List;

import static me.atie.partialKeepinventory.partialKeepinventory.CONFIG;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class pkiCommandRegistration {

    private static void modeMessage(CommandContext<ServerCommandSource> ctx){
        ctx.getSource().sendFeedback( Text.literal(
                        "Keepinventory mode is set to " + CONFIG.partialKeepinvMode().toString().toLowerCase()
                ), true);

    }

    private static boolean isValidUsername(String name) {
        return name.matches("^\\w{3,16}$");
    }

    public static void registerCommands() {

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
        dispatcher.register(literal("partialKeepinventory").requires(source -> source.hasPermissionLevel(4))
                .then( literal("enable").executes(
                        ctx -> {
                            CONFIG.enableMod(true);
                            ctx.getSource().sendFeedback(Text.literal("Enabled partial keepinventory"), true);
                            return 1;
                        })
                )
                .then( literal("disable").executes(
                        ctx -> {
                            CONFIG.enableMod(false);
                            ctx.getSource().sendFeedback(Text.literal("Disabled partial keepinventory"), true);
                            return 1;
                        })
                )
                .then(literal("info").executes(
                        ctx -> {
                            ctx.getSource().sendMessage(Text.literal("Please submit any issues or requests you have on my github.\n" +
                                    "github: https://github.com/atiedebee/Partial-Keepinventory"));
                            return  1;
                        })
                )
                .then(literal("mode")
                        .then(literal("percentage")
                                .executes(ctx -> {
                                    CONFIG.partialKeepinvMode(partialKeepinventory.KeepinvMode.PERCENTAGE);
                                    modeMessage(ctx);
                                    return 1;
                                })
                        )
                        .then(literal("rarity")
                                .executes(ctx -> {
                                    CONFIG.partialKeepinvMode(partialKeepinventory.KeepinvMode.RARITY);
                                    modeMessage(ctx);
                                    return 1;
                                })
                        )
                        .then(literal("custom")
                                .executes(ctx -> {
                                    CONFIG.partialKeepinvMode(partialKeepinventory.KeepinvMode.CUSTOM);
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
                                            CONFIG.inventoryDroprate(x);
                                            return 1;
                                        })
                                )
                                .executes(ctx -> {
                                    ctx.getSource().sendFeedback(Text.literal("Inventory droprate is set to " + CONFIG.inventoryDroprate() + "%"), true);
                                    return 1;
                                })
                        )
                        .then(literal("common")
                                .then(argument("percentage", IntegerArgumentType.integer(0, 100))
                                        .executes(ctx -> {
                                            final int x = IntegerArgumentType.getInteger(ctx, "percentage");
                                            ctx.getSource().sendFeedback(Text.literal("Set common droprate to " + x + "%"), true);
                                            CONFIG.commonDroprate(x);
                                            return 1;
                                        })
                                )
                                .executes(ctx -> {
                                    ctx.getSource().sendMessage(Text.literal("Common droprate is set to " + CONFIG.commonDroprate() + "%"));
                                    return 1;
                                })
                        )
                        .then(literal("uncommon")
                                .then(argument("percentage", IntegerArgumentType.integer(0, 100))
                                        .executes(ctx -> {
                                            final int x = IntegerArgumentType.getInteger(ctx, "percentage");
                                            ctx.getSource().sendFeedback(Text.literal("Set uncommon droprate to " + x + "%"), true);
                                            CONFIG.uncommonDroprate(x);
                                            return 1;
                                        })
                                )
                                .executes(ctx -> {
                                    ctx.getSource().sendMessage(Text.literal("Uncommon droprate is set to " + CONFIG.uncommonDroprate() + "%"));
                                    return 1;
                                })
                        )
                        .then(literal("rare")
                                .then(argument("percentage", IntegerArgumentType.integer(0, 100))
                                        .executes(ctx -> {
                                            final int x = IntegerArgumentType.getInteger(ctx, "percentage");
                                            ctx.getSource().sendFeedback(Text.literal("Set rare droprate to " + x + "%"), true);
                                            CONFIG.rareDroprate(x);
                                            return 1;
                                        })
                                )
                                .executes(ctx -> {
                                    ctx.getSource().sendMessage(Text.literal("Rare droprate is set to " + CONFIG.rareDroprate() + "%"));
                                    return 1;
                                })
                        )
                        .then(literal("epic")
                                .then(argument("percentage", IntegerArgumentType.integer(0, 100))
                                        .executes(ctx -> {
                                            final int x = IntegerArgumentType.getInteger(ctx, "percentage");
                                            ctx.getSource().sendFeedback(Text.literal("Set epic droprate to " + x + "%"), true);
                                            CONFIG.epicDroprate(x);
                                            return 1;
                                        })
                                )
                                .executes(ctx -> {
                                    ctx.getSource().sendMessage(Text.literal("Epic droprate is set to " + CONFIG.epicDroprate() + "%"));
                                    return 1;
                                })
                        )
                )
                .then(literal("savedPlayers")
                        .then(literal("list")
                                .executes(ctx -> {
                                    ctx.getSource().sendMessage(Text.literal("Players with regular keepinventory:"));
                                    for(var name: CONFIG.perPlayerKeepinventory() ){
                                        ctx.getSource().sendMessage( Text.literal(" " + name));
                                    }
                                    return 1;
                                })
                        )
                        .then(literal("add")
                                .then(argument("name", StringArgumentType.greedyString())
                                        .executes(ctx -> {
                                            String name = StringArgumentType.getString(ctx, "name");
                                            List<String> savedPlayers = CONFIG.perPlayerKeepinventory();

                                            if( !isValidUsername(name)){
                                                ctx.getSource().sendFeedback(Text.literal(name + " isn't a valid username"), false);
                                                return 0;
                                            }

                                            if( savedPlayers.contains(name) ){
                                                ctx.getSource().sendFeedback(Text.literal(name + " is already in the list"), false);
                                                return 0;
                                            }
                                            savedPlayers.add(name);
                                            ctx.getSource().sendFeedback(Text.literal("added " + name + " to the saved players"), true);
                                            CONFIG.perPlayerKeepinventory(savedPlayers);
                                            CONFIG.save();
                                            return 1;
                                        })
                                )
                        )
                        .then(literal("remove")
                                .then(argument("name", StringArgumentType.greedyString())
                                        .executes(ctx -> {
                                            String name = StringArgumentType.getString(ctx, "name");
                                            List<String> savedPlayers = CONFIG.perPlayerKeepinventory();

                                            if( !savedPlayers.contains(name) ){
                                                ctx.getSource().sendFeedback(Text.literal(name + " isn't in the list"), true);
                                                return 0;
                                            }
                                            savedPlayers.remove(name);
                                            ctx.getSource().sendFeedback(Text.literal("removed " + name + " from the saved players"), true);
                                            CONFIG.perPlayerKeepinventory(savedPlayers);
                                            CONFIG.save();
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
                                        CONFIG.expression(expression);
                                        CONFIG.save();
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

                )
                .executes(ctx -> {
                    ctx.getSource().sendMessage(Text.literal(
                            """
                                    > /partialKeepinventory [enable|disable]
                                    Set the drop behaviour
                                    > /partialKeepinventory mode [percentage|rarity]
                                    > /partialKeepinventory droprate [inventory|common|uncommon|rare|epic] <percentage>
                                    """
                    ));
                    return 1;
                })
            )
        );

    }
}
