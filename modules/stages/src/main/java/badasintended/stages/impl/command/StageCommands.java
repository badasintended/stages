package badasintended.stages.impl.command;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import badasintended.stages.api.StagesUtil;
import badasintended.stages.api.data.StageRegistry;
import badasintended.stages.api.data.Stages;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.ArgumentTypes;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.command.argument.serialize.ConstantArgumentSerializer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class StageCommands {

    // @formatter:off
    private static final ArgumentType<?>
        STAGE  = new StageArgumentType(),
        PLAYER = EntityArgumentType.players(),
        BOOL   = BoolArgumentType.bool();
    // @formatter:on

    public static void register() {
        ArgumentTypes.register(StagesUtil.MOD_ID + ":arg", StageArgumentType.class, new ConstantArgumentSerializer<>(StageArgumentType::new));
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> dispatcher.register(
            literal("stage")
                .then(silent("add", 2, (context, silent) -> edit(context, silent, true)))
                .then(silent("remove", 2, (context, silent) -> edit(context, silent, false)))
                .then(player("info", 0, StageCommands::info))
                .then(player("clear", 2, StageCommands::clear))
                .then(player("all", 2, StageCommands::all))
                .then(literal("check")
                    .requires(sender -> sender.hasPermissionLevel(2))
                    .then(argument("stage", STAGE)
                        .executes(context -> check(context, false)))
                    .then(argument("targets", PLAYER)
                        .then(argument("stage", STAGE)
                            .executes(context -> check(context, true)))))
        ));
    }

    private static LiteralArgumentBuilder<ServerCommandSource> silent(String key, int perm, CommandConsumer consumer) {
        return literal(key)
            .requires(s -> s.hasPermissionLevel(perm))
            .then(argument("targets", PLAYER)
                .then(argument("stage", STAGE)
                    .executes(context -> consumer.apply(context, false))
                    .then(argument("silent", BOOL)
                        .executes(context -> consumer.apply(context, true)))));
    }

    private static LiteralArgumentBuilder<ServerCommandSource> player(String key, int perm, CommandConsumer consumer) {
        return literal(key)
            .requires(s -> s.hasPermissionLevel(perm))
            .executes(context -> consumer.apply(context, false))
            .then(argument("targets", PLAYER)
                .executes(context -> consumer.apply(context, true)));
    }

    private static int info(CommandContext<ServerCommandSource> context, boolean target) throws CommandSyntaxException {
        if (target) {
            for (ServerPlayerEntity player : EntityArgumentType.getPlayers(context, "targets")) {
                info(context, player);
            }
        } else {
            info(context, context.getSource().getPlayer());
        }
        return 0;
    }

    private static void info(CommandContext<ServerCommandSource> context, ServerPlayerEntity player) {
        ServerCommandSource source = context.getSource();
        List<Identifier> stages = Stages.get(player).values().stream().sorted(Comparator.comparing(Identifier::toString)).collect(Collectors.toList());
        Text playerName = player.getDisplayName();
        Text feedback;
        if (stages.size() == 0) {
            feedback = new TranslatableText("command.stages.info.empty", playerName);
        } else if (stages.size() == 1) {
            feedback = new TranslatableText("command.stages.info.one", playerName, stages.get(0));
        } else if (stages.size() == 2) {
            feedback = new TranslatableText("command.stages.info.two", playerName, stages.get(0), stages.get(1));
        } else {
            String first = stages.subList(0, stages.size() - 1).stream().map(Identifier::toString).collect(Collectors.joining("§r, §d"));
            feedback = new TranslatableText("command.stages.info.many", first, stages.get(stages.size() - 1));
        }
        source.sendFeedback(feedback, false);
    }

    private static int edit(CommandContext<ServerCommandSource> context, boolean silent, boolean add) throws CommandSyntaxException {
        Identifier stage = StageArgumentType.getIdentifier(context, "stage");
        ServerCommandSource source = context.getSource();
        PlayerEntity sender = source.getPlayer();
        for (ServerPlayerEntity player : EntityArgumentType.getPlayers(context, "targets")) {
            Stages stages = Stages.get(player);
            if (add) {
                stages.add(stage);
            } else {
                stages.remove(stage);
            }
            if (!silent || BoolArgumentType.getBool(context, "silent")) {
                source.sendFeedback(new TranslatableText("command.stages.edit.target." + add, stage), true);
                if (player != sender) {
                    source.sendFeedback(new TranslatableText("command.stages.edit.sender." + add, stage, player.getDisplayName()), true);
                }
            }
        }
        return 0;
    }

    private static int clear(CommandContext<ServerCommandSource> context, boolean target) throws CommandSyntaxException {
        if (target) {
            for (ServerPlayerEntity player : EntityArgumentType.getPlayers(context, "targets")) {
                clear(context, player);
            }
        } else {
            clear(context, context.getSource().getPlayer());
        }
        return 0;
    }

    private static void clear(CommandContext<ServerCommandSource> context, ServerPlayerEntity player) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();
        PlayerEntity sender = source.getPlayer();

        Stages stages = Stages.get(player);
        int size = stages.values().size();
        stages.clear();
        source.sendFeedback(new TranslatableText("command.stages.clear.target", size), true);
        if (player != sender) {
            source.sendFeedback(new TranslatableText("command.stages.clear.sender", size, player.getDisplayName()), true);
        }
    }

    private static int all(CommandContext<ServerCommandSource> context, boolean target) throws CommandSyntaxException {
        if (target) {
            for (ServerPlayerEntity player : EntityArgumentType.getPlayers(context, "targets")) {
                all(context, player);
            }
        } else {
            all(context, context.getSource().getPlayer());
        }
        return 0;
    }

    private static void all(CommandContext<ServerCommandSource> context, ServerPlayerEntity player) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();
        PlayerEntity sender = source.getPlayer();

        Stages stages = Stages.get(player);
        int before = stages.values().size();
        stages.addAll(StageRegistry.allStages());
        int after = stages.values().size();
        source.sendFeedback(new TranslatableText("command.stages.all.target", after - before), true);
        if (player != sender) {
            source.sendFeedback(new TranslatableText("command.stages.all.sender", after - before, sender.getDisplayName()), true);
        }
    }

    private static int check(CommandContext<ServerCommandSource> context, boolean target) throws CommandSyntaxException {
        if (target) {
            for (ServerPlayerEntity player : EntityArgumentType.getPlayers(context, "targets")) {
                check(context, player);
            }
        } else {
            check(context, context.getSource().getPlayer());
        }
        return 0;
    }

    private static void check(CommandContext<ServerCommandSource> context, ServerPlayerEntity player) {
        ServerCommandSource source = context.getSource();

        Identifier stage = StageArgumentType.getIdentifier(context, "stage");
        boolean contains = Stages.get(player).contains(stage);
        source.sendFeedback(new TranslatableText("command.stages.check." + contains, player.getDisplayName(), stage), true);
    }

    public static class StageArgumentType extends IdentifierArgumentType {

        @Override
        public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
            return CommandSource.suggestIdentifiers(StageRegistry.allStages(), builder);
        }

    }

    @FunctionalInterface
    public interface CommandConsumer {

        int apply(CommandContext<ServerCommandSource> context, boolean b) throws CommandSyntaxException;

    }

}
