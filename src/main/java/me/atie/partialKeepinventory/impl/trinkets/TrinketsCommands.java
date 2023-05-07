package me.atie.partialKeepinventory.impl.trinkets;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import me.atie.partialKeepinventory.settings.pkiSettings;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.util.function.Supplier;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public abstract class TrinketsCommands {

    private static int setTrinketMode(CommandContext<ServerCommandSource> ctx, KeepTrinketMode mode){
        TrinketsImpl.trinketSettings.setMode(mode);
        pkiSettings.updateServerConfig();
        ctx.getSource().sendMessage(Text.literal("Keep Trinkets mode was set to '" + mode.toString() + "'"));
        return 1;
    }

    private static int getDroprate(CommandContext<ServerCommandSource> ctx, Supplier<Integer> supplier, String name){
        ctx.getSource().sendMessage(
                Text.literal(name + " droprate was set to " + supplier.get() + "%")
        );
        return 1;
    }


    public static void initTrinketCommands(){
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
                dispatcher.register(literal("pki-trinkets").requires(source -> source.hasPermissionLevel(2) )
                .then(literal("overrideDropRule")
                        .then(literal("true")
                                .executes(ctx -> {
                                    TrinketsImpl.trinketSettings.setOverrideDropRule(true);
                                    pkiSettings.updateServerConfig();
                                    return 1;
                                })
                        )
                        .then(literal("false")
                                .executes(ctx -> {
                                    TrinketsImpl.trinketSettings.setOverrideDropRule(false);
                                    pkiSettings.updateServerConfig();
                                    return 1;
                                })
                        )
                        .executes(ctx -> {
                            ctx.getSource().sendMessage(Text.literal("Override Droprule was set to: " + TrinketsImpl.trinketSettings.OverrideDropRule()));
                            return 1;
                        })
                )
                .then(literal("overrideDropRate")
                        .then(literal("true")
                                .executes(ctx -> {
                                    TrinketsImpl.trinketSettings.setOverrideDropRate(true);
                                    pkiSettings.updateServerConfig();
                                    return 1;
                                })
                        )
                        .then(literal("false")
                                .executes(ctx -> {
                                    TrinketsImpl.trinketSettings.setOverrideDropRate(false);
                                    pkiSettings.updateServerConfig();
                                    return 1;
                                })
                        )
                        .executes(ctx -> {
                            ctx.getSource().sendMessage(Text.literal("Override Droprate was set to: " + TrinketsImpl.trinketSettings.OverrideDropRate()));
                            return 1;
                        })
                )
                .then(
                    literal("mode").then(
                            literal("set")
                                    .then(literal("default")
                                            .executes(ctx -> setTrinketMode(ctx, KeepTrinketMode.DEFAULT))
                                    )
                                    .then(literal("static")
                                            .executes(ctx -> setTrinketMode(ctx, KeepTrinketMode.STATIC))
                                    )
                                    .then(literal("chance")
                                            .executes(ctx -> setTrinketMode(ctx, KeepTrinketMode.CHANCE))
                                    )
                                    .then(literal("rarity")
                                            .executes(ctx -> setTrinketMode(ctx, KeepTrinketMode.RARITY))
                                    )
                    )
                )
                .then(
                    literal("droprate")
                            .then(literal("static")
                                    .then(argument("percent", IntegerArgumentType.integer(0, 100))
                                            .executes(ctx -> {
                                                TrinketsImpl.trinketSettings.setStaticDroprate(IntegerArgumentType.getInteger(ctx, "percent"));
                                                pkiSettings.updateServerConfig();
                                                getDroprate(ctx,  TrinketsImpl.trinketSettings::getStaticDroprate, "Static");
                                                return 1;
                                            })
                                    )
                                    .executes(ctx -> getDroprate(ctx, TrinketsImpl.trinketSettings::getStaticDroprate, "Static"))
                            )
                            .then(literal("common")
                                    .then(argument("percent", IntegerArgumentType.integer(0, 100))
                                            .executes(ctx -> {
                                                TrinketsImpl.trinketSettings.setCommonDroprate(IntegerArgumentType.getInteger(ctx, "percent"));
                                                pkiSettings.updateServerConfig();
                                                getDroprate(ctx,  TrinketsImpl.trinketSettings::getCommonDroprate, "Common");
                                                return 1;
                                            })
                                    )
                                    .executes(ctx -> getDroprate(ctx, TrinketsImpl.trinketSettings::getCommonDroprate, "Common"))
                            )
                            .then(literal("uncommon")
                                    .then(argument("percent", IntegerArgumentType.integer(0, 100))
                                            .executes(ctx -> {
                                                TrinketsImpl.trinketSettings.setUncommonDroprate(IntegerArgumentType.getInteger(ctx, "percent"));
                                                pkiSettings.updateServerConfig();
                                                getDroprate(ctx,  TrinketsImpl.trinketSettings::getUncommonDroprate, "Uncommon");
                                                return 1;
                                            })
                                    )
                                    .executes(ctx -> getDroprate(ctx, TrinketsImpl.trinketSettings::getUncommonDroprate, "Uncommon"))
                            )
                            .then(literal("rare")
                                    .then(argument("percent", IntegerArgumentType.integer(0, 100))
                                            .executes(ctx -> {
                                                TrinketsImpl.trinketSettings.setRareDroprate(IntegerArgumentType.getInteger(ctx, "percent"));
                                                pkiSettings.updateServerConfig();
                                                getDroprate(ctx,  TrinketsImpl.trinketSettings::getRareDroprate, "Rare");
                                                return 1;
                                            })
                                    )
                                    .executes(ctx -> getDroprate(ctx, TrinketsImpl.trinketSettings::getRareDroprate, "Rare"))
                            )
                            .then(literal("epic")
                                    .then(argument("percent", IntegerArgumentType.integer(0, 100))
                                            .executes(ctx -> {
                                                TrinketsImpl.trinketSettings.setEpicDroprate(IntegerArgumentType.getInteger(ctx, "percent"));
                                                pkiSettings.updateServerConfig();
                                                getDroprate(ctx,  TrinketsImpl.trinketSettings::getEpicDroprate, "Epic");
                                                return 1;
                                            })
                                    )
                                    .executes(ctx -> getDroprate(ctx, TrinketsImpl.trinketSettings::getEpicDroprate, "Epic"))
                            )
                )
        ));
    }

}
