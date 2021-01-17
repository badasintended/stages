package badasintended.stages.impl.mixin;

import java.util.Set;
import java.util.function.Predicate;

import badasintended.stages.api.data.StageRegistry;
import badasintended.stages.api.data.Stages;
import badasintended.stages.impl.command.StageCommands;
import badasintended.stages.impl.command.StageSelectorHolder;
import com.mojang.brigadier.StringReader;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.command.EntitySelectorOptions;
import net.minecraft.command.EntitySelectorReader;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntitySelectorOptions.class)
public abstract class EntitySelectorOptionsMixin {

    @Shadow
    private static void putOption(String id, EntitySelectorOptions.SelectorHandler handler, Predicate<EntitySelectorReader> condition, Text description) {
    }

    @Inject(
        method = "register",
        at = @At(ordinal = 0, value = "INVOKE", target = "Lnet/minecraft/command/EntitySelectorOptions;putOption(Ljava/lang/String;Lnet/minecraft/command/EntitySelectorOptions$SelectorHandler;Ljava/util/function/Predicate;Lnet/minecraft/text/Text;)V")
    )
    private static void addStagesOption(CallbackInfo ci) {
        putOption(
            "stages", selector -> {
                StringReader string = selector.getReader();

                string.expect('{');
                string.skipWhitespace();

                Set<Identifier> must = new ObjectOpenHashSet<>();
                Set<Identifier> mustNot = new ObjectOpenHashSet<>();

                while (string.canRead() && string.peek() != '}') {
                    string.skipWhitespace();

                    boolean not = selector.readNegationCharacter();

                    Identifier stageId = Identifier.fromCommandInput(string);
                    if (!StageRegistry.isRegistered(stageId)) {
                        throw StageCommands.UNREGISTERED_STAGE.createWithContext(string);
                    }

                    if (not) {
                        mustNot.add(stageId);
                    } else {
                        must.add(stageId);
                    }

                    string.skipWhitespace();

                    if (string.canRead() && string.peek() == ',') {
                        string.skip();
                    }
                }

                string.skipWhitespace();
                string.expect('}');

                if (!(must.isEmpty() && mustNot.isEmpty())) {
                    selector.setPredicate(entity -> {
                        if (entity instanceof ServerPlayerEntity) {
                            Stages stages = Stages.get((ServerPlayerEntity) entity);
                            return stages.containsAll(must) && !stages.containsAny(mustNot);
                        }
                        return false;
                    });
                    selector.setIncludesNonPlayers(false);
                }
                ((StageSelectorHolder) selector).stages$setSelectsStages(true);
            },
            selector -> !((StageSelectorHolder) selector).stages$selectsStages(),
            new TranslatableText("argument.stages.description")
        );
    }

}
