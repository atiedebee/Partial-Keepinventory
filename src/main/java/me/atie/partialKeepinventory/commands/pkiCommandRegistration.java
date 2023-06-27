package me.atie.partialKeepinventory.commands;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.sun.jdi.connect.Connector;
import me.atie.partialKeepinventory.KeepXPMode;
import me.atie.partialKeepinventory.KeepinvMode;
import me.atie.partialKeepinventory.PartialKeepInventory;
import me.atie.partialKeepinventory.formula.InventoryDroprateFormula;
import me.atie.partialKeepinventory.formula.XpDroprateFormula;
import me.atie.partialKeepinventory.rules.*;
import me.atie.partialKeepinventory.settings.pkiSettings;
import me.atie.partialKeepinventory.util.InventoryUtil;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.argument.GameProfileArgumentType;
import net.minecraft.command.suggestion.SuggestionProviders;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;

import java.util.Collection;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static me.atie.partialKeepinventory.PartialKeepInventory.CONFIG;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class pkiCommandRegistration {

    private static void modeMessage(CommandContext<ServerCommandSource> ctx) {
        ctx.getSource().sendFeedback(() -> Text.literal(
                "Keepinventory mode is set to " + CONFIG.getPartialKeepinvMode().toString()
        ), true);

    }

    private static void percentMessage(CommandContext<ServerCommandSource> ctx, String valName, int val) {
        ctx.getSource().sendFeedback(() -> Text.literal(
                valName + " is set to " + val + "%"
        ), true);
    }

    private static void syncSettings(CommandManager.RegistrationEnvironment environment){
//        if( environment.dedicated ){

            pkiSettings.updateServerConfig();
//        }
    }


    private static int sendHelpMessage(CommandContext<ServerCommandSource> ctx, Text text){
        ctx.getSource().sendMessage(Text.literal("=== Partial Keepinventory ===").setStyle(Style.EMPTY.withBold(true).withColor(0xFF4444)));
        ctx.getSource().sendMessage(text);
        return 1;
    }

    public static void registerCommands() {
        final Text xpDroprateTextHelp = Text.literal(
        """
        Xp droprates show how much experience you drop and lose when dying:
        == Drop ==
        This is how much XP is DROPPED when dying.
        == Loss ==
        This is the percentage of your DROPPED XP that disappears on death.
        """
        );
        final Text xpModeTextHelp = Text.literal(
        """
        These settings change how xp is dropped:
        == Levels ==
        Each level is graded the same.
        == Points ==
        Use XP points as the metric for how much to drop.
        
        Each of these can be used with custom expressions as well.
        """
        );
        final Text invDroprateTextHelp = Text.literal(
        """
        Droprates dictate how likely certain items are to be dropped:
        == Common, Uncommon, Rare, Epic ==
        These are Minecraft's 4 rarities. Each indicated by the color of an item's name.
        == Inventory ==
        The base droprate when using "static" as your droprate.
        """
        );
        final Text modeTextHelp = Text.literal(
        """
        Modes dictate how items are dropped:
        == Static ==
        All items are dropped equally. The percentage used is the "Inventory Droprate".
        == Vanilla ==
        Items are dropped like they are in vanilla.
        == Rarity ==
        Items are dropped based off of their rarity.
        == Custom ==
        Item drops are calculated using an expression.
        """
        );

        final Text invExpressionTextHelp = Text.literal(
        """
         Expressions give more control over how to drop items. These are simply an equation using certain variables.
         The expression will return a number between 0.0 - 1.0, and is clamped when higher.
         0.0 == 0% dropped
         1.0 == 100% dropped
         Variables and their meaning can be seen using the command
         """
        ).append(Text.literal(
                "/pki help expression-vars"
        ).setStyle(Style.EMPTY.withBold(true).withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/pki help expression-vars")))
        );

        final Text expressionVarsTextHelp = Text.literal(
        """
                === General ===
                = spawnDistance
                 The distance from the player to their spawnpoint.
                = spawnX, spawnY, spawnZ
                 The player's spawn position
                = playerX, playerY, playerZ
                 The player's position
                
                === Inventory ===
                = rarityPercent
                 The configured droprate of the item's rarity.
                = isCommon, isUncommon, isRare, isEpic
                 Whether an item has a certain rarity, These are 1.0 when true and 0.0 when false.
                = dropPercent
                 The static inventory droprate.
                
                === XP ===
                = xpPoints
                 Amount of XP points
                = xpLevel
                 Amount of XP levels
                """
        );


        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
                dispatcher.register(literal("pki").requires(source -> source.hasPermissionLevel(2) )
                        .then(literal("enable").executes(
                                ctx -> {
                                    CONFIG.setEnableMod(true);
                                    syncSettings(environment);
                                    ctx.getSource().sendFeedback(() -> Text.literal("Enabled partial keepinventory"), true);
                                    return 1;
                                })
                        )
                        .then(literal("disable").executes(
                                ctx -> {
                                    CONFIG.setEnableMod(false);
                                    syncSettings(environment);
                                    ctx.getSource().sendFeedback(() -> Text.literal("Disabled partial keepinventory"), true);
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
                                        case CUSTOM -> ">Expression: \"" + CONFIG.getInvExpression() + "\"\n\n" +
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
                                                                ctx.getSource().sendFeedback(() -> Text.literal("Added " + message + "to the saved players"), true);
                                                            }
                                                            if( !notAdded.isEmpty()) {
                                                                ctx.getSource().sendFeedback(() -> Text.literal(notAdded + " were ignored, for they are in the list already."), false);
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
                                                                ctx.getSource().sendFeedback(() -> Text.literal("Removed " + message + "from the saved players."), true);
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
                                                                CONFIG.setInvExpression(expression);
                                                                syncSettings(environment);
                                                            }catch(Exception e){
                                                                PartialKeepInventory.LOGGER.error("Invalid expression: " + e.getMessage());
                                                                ctx.getSource().sendMessage(Text.literal("Invalid expression: " + e.getMessage()).setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAA0000))));
                                                            }

                                                            return 1;
                                                        })
                                                )
                                        )
                                        .executes(ctx -> {
                                            ctx.getSource().sendMessage(Text.literal("The custom droprate formula is set to \"" + CONFIG.getInvExpression() + "\""));
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
                        .then(literal("rule")
                                .then(literal("group")
                                        .then(literal("create")// Add a rule group with the the drop action and modifier
                                                .then(argument("name", StringArgumentType.word())
                                                        .then(argument("modifier", IntegerArgumentType.integer(0))
                                                                .then(argument("drop action", StringArgumentType.word())
                                                                    .suggests(new DropActionArgumentSuggestionProvider())
                                                                        .executes(pkiCommandRegistration::addRuleGroup)
                                                                )
                                                        )
                                                )
                                        )
                                        .then(literal("remove")// remove a rule group
                                                .then(argument("name", StringArgumentType.word())
                                                    .suggests(new GroupArgumentSuggestionProvider())
                                                        .executes(pkiCommandRegistration::removeRuleGroup)
                                                )
                                        )
                                        .then(literal("edit")
                                                .then(argument("group name", StringArgumentType.word())
                                                    .suggests(new GroupArgumentSuggestionProvider())
                                                        .then(argument("new name", StringArgumentType.word())
                                                                .then(argument("modifier", IntegerArgumentType.integer(0))
                                                                    .then(argument("drop action", StringArgumentType.word())
                                                                        .suggests(new DropActionArgumentSuggestionProvider())
                                                                            .executes(ctx -> {
                                                                                String groupName = StringArgumentType.getString(ctx, "group name");
                                                                                String newGroupName = StringArgumentType.getString(ctx, "new name");
                                                                                RuleGroup group = CONFIG.ruleGroups.get(groupName);

                                                                                int modifier = IntegerArgumentType.getInteger(ctx, "modifier");

                                                                                String dropActionName = StringArgumentType.getString(ctx, "drop action");
                                                                                Optional<InventoryUtil.DropAction> dropAction = InventoryUtil.DropAction.fromString(dropActionName);

                                                                                if( group == null ){
                                                                                    ctx.getSource().sendMessage(Text.literal("Couldn't modify group: group '" + groupName + "' doesn't exist").setStyle(Style.EMPTY.withColor(0xAA0000)));
                                                                                    return 0;
                                                                                }
                                                                                if( !newGroupName.equals(groupName) && CONFIG.ruleGroups.containsKey(newGroupName) ){
                                                                                    ctx.getSource().sendMessage(Text.literal("Couldn't modify group: group name '" + newGroupName + "' already exists").setStyle(Style.EMPTY.withColor(0xAA0000)));
                                                                                    return 0;
                                                                                }

                                                                                if( dropAction.isEmpty() ){
                                                                                    ctx.getSource().sendMessage(Text.literal("Couldn't modify group: '" + dropActionName + "' isn't a valid drop action").setStyle(Style.EMPTY.withColor(0xAA0000)));
                                                                                    return 0;
                                                                                }

                                                                                group.dropAction = dropAction.get();
                                                                                group.modifier = modifier/100.0f;
                                                                                group.name = groupName;

                                                                                return 1;
                                                                            })
                                                                    )
                                                                )
                                                        )
                                                )
                                        )
                                        .then(literal("list")
                                                .then(argument("group name", StringArgumentType.word())
                                                    .suggests(new GroupArgumentSuggestionProvider())
                                                        .executes( ctx -> {
                                                            String group = StringArgumentType.getString(ctx, "group name");
                                                            RuleGroup ruleGroup = CONFIG.ruleGroups.get(group);
                                                            ctx.getSource().sendMessage(Text.literal("  " + ruleGroup.name));
                                                            for( var rule: ruleGroup.rules ){
                                                                ctx.getSource().sendMessage(Text.literal("| " + rule.left.name + " " + rule.comparison.toString() + " " + rule.right.toString()));
                                                            }
                                                            ctx.getSource().sendMessage(Text.literal("\\_>   " + ruleGroup.dropAction.toString().toLowerCase(Locale.ROOT) + " " + ruleGroup.modifier));

                                                            return 1;
                                                        })
                                                )
                                        )
                                )
                                .then(literal("add")// Add a rule to an existing group
                                        .then(argument("group name", StringArgumentType.word())
                                            .suggests(new GroupArgumentSuggestionProvider())
                                                .then(argument("variable", StringArgumentType.word())
                                                    .suggests(new VariableArgumentSuggestionProvider())
                                                        .then(argument("comparison", StringArgumentType.word())
                                                            .suggests(new ComparisonArgumentSuggestionProvider())
                                                                .then(argument("value", StringArgumentType.word())
                                                                    .suggests(new ValueArgumentSuggestionProvider())
                                                                        .executes(pkiCommandRegistration::addRule)
                                                                )
                                                        )
                                                )
                                        )
                                )
                                .then(literal("remove")// remove a rule from an existing group
                                        .then(argument("group name", StringArgumentType.word())
                                                .suggests(new GroupArgumentSuggestionProvider())
                                                        .then(argument("rule number", IntegerArgumentType.integer(1))
                                                                .executes(ctx -> {
                                                                    int number = IntegerArgumentType.getInteger(ctx, "rule number");
                                                                    String groupString =  StringArgumentType.getString(ctx, "group name");
                                                                    RuleGroup group = CONFIG.ruleGroups.get(groupString);
                                                                    if( group == null ){
                                                                        String error = "Failed removing rule from group '" + groupString + "': group doesn't exist";
                                                                        ctx.getSource().sendMessage(Text.literal(error));
                                                                        PartialKeepInventory.LOGGER.error(error);
                                                                        return 0;
                                                                    }

                                                                    if( group.rules.size() < number ){
                                                                        String error = "Failed removing rule from group '" + groupString + "': index out of bounds";
                                                                        ctx.getSource().sendMessage(Text.literal(error));
                                                                        PartialKeepInventory.LOGGER.error(error);
                                                                        return 0;
                                                                    }

                                                                    group.rules.remove(number - 1);
                                                                    return 1;
                                                                })
                                                        )
                                        )
                                )
                        )
                        .then(literal("help")
                                .then(literal("inv-mode")
                                        .executes(ctx -> sendHelpMessage(ctx, modeTextHelp))
                                )
                                .then(literal("inv-expression")
                                        .executes(ctx -> sendHelpMessage(ctx, invExpressionTextHelp))
                                )
                                .then(literal("inv-droprate")
                                        .executes(ctx -> sendHelpMessage(ctx, invDroprateTextHelp))
                                )
                                .then(literal("expression-vars")
                                        .executes(ctx -> sendHelpMessage(ctx, expressionVarsTextHelp))
                                )
                                .then(literal("xp-mode")
                                        .executes(ctx -> sendHelpMessage(ctx, xpModeTextHelp))
                                )
                                .then(literal("xp-droprate")
                                        .executes(ctx -> sendHelpMessage(ctx, xpDroprateTextHelp))
                                )
                        )
                        .executes(ctx -> {
                            ctx.getSource().sendMessage(Text.literal(
                                    """
                                            Get help on a subject:
                                            > /pki help <subject>
                                            
                                            Show settings
                                            > /pki info
                                            """
                            ));
                            return 1;
                        })

                )
        );

    }


    private static int removeRuleGroup(CommandContext<ServerCommandSource> ctx) {
        String group = StringArgumentType.getString(ctx, "name");

        if( CONFIG.ruleGroups.remove(group) == null ){
            String error = "Failed removing group: group doesn't exist.";
            ctx.getSource().sendMessage(Text.literal(error));
            PartialKeepInventory.LOGGER.error(error);
            return 0;
        }
        String success = "Successfully removed group '" + group + "'.";
        ctx.getSource().sendMessage(Text.literal(success));
        PartialKeepInventory.LOGGER.info(success);

        return 1;
    }
    private static int addRuleGroup(CommandContext<ServerCommandSource> ctx) {
        String group = StringArgumentType.getString(ctx, "name");
        int modifier = IntegerArgumentType.getInteger(ctx, "modifier");
        String action = StringArgumentType.getString(ctx, "drop action");

        PartialKeepInventory.LOGGER.info("Checking presence");
        if( CONFIG.ruleGroups.containsKey(group) ){
            String error = "Failed adding group: group already exists. Consider removing it with '/pki rulegroup remove <name>'";
            ctx.getSource().sendMessage(Text.literal(error));
            PartialKeepInventory.LOGGER.error(error);
            return 0;
        }

        PartialKeepInventory.LOGGER.info("Parsing drop action");
        Optional<InventoryUtil.DropAction> dropActionOptional = InventoryUtil.DropAction.fromString(action);
        if( dropActionOptional.isEmpty() ){
            String error = "Failed adding group: invalid drop action. Expected one of 'keep', 'drop', 'destroy', 'none'";
            ctx.getSource().sendMessage(Text.literal(error));
            PartialKeepInventory.LOGGER.error(error);
            return 0;
        }

        PartialKeepInventory.LOGGER.info("adding rulegroup");
        CONFIG.ruleGroups.put(group, new RuleGroup(group, dropActionOptional.get(), modifier/100.0f) );
        String success = "Successfully added group '" + group + "'";
        ctx.getSource().sendMessage(Text.literal(success));
        PartialKeepInventory.LOGGER.info(success);
        return 1;
    }

    private static int addRule(CommandContext<ServerCommandSource> ctx) {
        String group = StringArgumentType.getString(ctx, "group name");
        String variable = StringArgumentType.getString(ctx, "variable");
        String comparison = StringArgumentType.getString(ctx, "comparison");
        String value = StringArgumentType.getString(ctx, "value");

        RuleGroup ruleGroup = CONFIG.ruleGroups.get(group);
        if( ruleGroup == null ){
            String error = "Failed adding rule: group '" + group + "' doesn't exist.";
            PartialKeepInventory.LOGGER.error(error);
            ctx.getSource().sendMessage(Text.literal(error));
            return 0;
        }

        RuleVariable ruleVariable = RuleVariables.variables.get(variable);
        if( ruleVariable == null ){
            String error = "Failed adding rule: '" + variable +"' is not a valid variable.";
            PartialKeepInventory.LOGGER.error(error);
            ctx.getSource().sendMessage(Text.literal(error));
            return 0;
        }

        Optional<RuleComparison> ruleComparisonOptional = RuleComparison.fromString(comparison);
        if( ruleComparisonOptional.isEmpty() ){
            String error = "Failed adding rule: '" + comparison +"' is not a valid operator.";
            PartialKeepInventory.LOGGER.error(error);
            ctx.getSource().sendMessage(Text.literal(error));
            return 0;
        }
        RuleComparison ruleComparison = ruleComparisonOptional.get();

        RuleType ruleType = ruleVariable.type;
        if( !ruleType.canBeComparedUsing(ruleComparison) ){
            String error = "Failed adding rule: cannot compare variable with type '" + ruleType.name() + "' using " + comparison;
            PartialKeepInventory.LOGGER.error(error);
            ctx.getSource().sendMessage(Text.literal(error));
            return 0;
        }

//        To whoever is reading this: I'm sorry
        RuleType valueType;
        Object ruleValue;
        if( value.matches("^\\d+%?$") ){
            if( value.matches("^\\d+\\.\\d+$")){
                ruleValue = Float.parseFloat(value);
                valueType = RuleType.Float;
            }else {

                if( value.lastIndexOf('%') > 0 ){
                    ruleValue = Integer.parseInt(value);
                    valueType = RuleType.Percentage;
                }else {
                    ruleValue = Integer.parseInt(value);
                    valueType = RuleType.Number;
                }
            }
        }else if( value.equals("true") || value.equals("false") ){
            ruleValue = value.charAt(0) == 't';
            valueType = RuleType.Boolean;
        }else{
            ruleValue = value;
            valueType = RuleType.String;
        }

        if( !valueType.equals(ruleVariable.type) ){
            String error = "Failed adding rule: type of value " + value + "(" + valueType.name() + ") is not the same as the variable type '" + ruleVariable.type.name() + "'";
            PartialKeepInventory.LOGGER.error(error);
            ctx.getSource().sendMessage(Text.literal(error));
            return 0;
        }

        DropRule rule = new DropRule(ruleVariable, ruleComparison, ruleValue);
        ruleGroup.addRule(rule);
        ctx.getSource().sendMessage(Text.literal("Added rule " + ruleVariable.name + " " + ruleComparison.toString() + " " + ruleValue.toString() + " to group " + group ));
        return 1;
    }
}


class GroupArgumentSuggestionProvider implements SuggestionProvider<ServerCommandSource> {
    @Override
    public CompletableFuture<Suggestions> getSuggestions(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder) throws CommandSyntaxException {
        for( var e: CONFIG.ruleGroups.entrySet() ){
            builder.suggest(e.getKey());
        }

        return builder.buildFuture();
    }
}

class VariableArgumentSuggestionProvider implements SuggestionProvider<ServerCommandSource> {
    @Override
    public CompletableFuture<Suggestions> getSuggestions(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder) throws CommandSyntaxException {
        for( var e: RuleVariables.variables.keySet() ){
            builder.suggest(e);
        }
        return builder.buildFuture();
    }
}

class DropActionArgumentSuggestionProvider implements SuggestionProvider<ServerCommandSource> {
    @Override
    public CompletableFuture<Suggestions> getSuggestions(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder) throws CommandSyntaxException {
        for( var e: InventoryUtil.DropAction.values()){
            builder.suggest(e.toString().toLowerCase(Locale.ROOT));
        }
        return builder.buildFuture();
    }
}

class ComparisonArgumentSuggestionProvider implements SuggestionProvider<ServerCommandSource> {
    @Override
    public CompletableFuture<Suggestions> getSuggestions(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder) throws CommandSyntaxException {
        String variableString = StringArgumentType.getString(context, "variable");
        RuleVariable variable = RuleVariables.variables.get(variableString);
        if(variable != null){
            for( var comparison: RuleComparison.values() ) {
                if( variable.type.canBeComparedUsing(comparison) ) {
                    builder.suggest(comparison.toArgumentString());
                }
            }
        }
        return builder.buildFuture();
    }
}

class ValueArgumentSuggestionProvider implements SuggestionProvider<ServerCommandSource> {
    @Override
    public CompletableFuture<Suggestions> getSuggestions(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder) throws CommandSyntaxException {
        String variableString = StringArgumentType.getString(context, "variable");
        RuleVariable variable = RuleVariables.variables.get(variableString);
        if( variable.type == RuleType.Boolean ) {
            builder.suggest("true");
            builder.suggest("false");
        }
        return builder.buildFuture();
    }
}