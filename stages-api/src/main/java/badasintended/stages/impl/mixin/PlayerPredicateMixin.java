package badasintended.stages.impl.mixin;

import java.util.Set;

import badasintended.stages.api.data.Stages;
import badasintended.stages.impl.predicate.StagePredicateHolder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.entity.Entity;
import net.minecraft.predicate.PlayerPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(PlayerPredicate.class)
public abstract class PlayerPredicateMixin implements StagePredicateHolder {

    @Shadow
    @Final
    public static PlayerPredicate ANY;

    @Unique
    private final Set<Identifier> must = new ObjectOpenHashSet<>();

    @Unique
    private final Set<Identifier> mustNot = new ObjectOpenHashSet<>();

    @Inject(
        method = "test",
        at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/server/network/ServerPlayerEntity;getStatHandler()Lnet/minecraft/stat/ServerStatHandler;"),
        locals = LocalCapture.CAPTURE_FAILHARD,
        cancellable = true
    )
    private void checkStages(Entity entity, CallbackInfoReturnable<Boolean> cir, ServerPlayerEntity serverPlayerEntity) {
        if (!must.isEmpty()) {
            Stages stages = Stages.get(serverPlayerEntity);
            if (!stages.containsAll(must) || stages.containsAny(mustNot)) {
                cir.setReturnValue(false);
            }
        }
    }

    @Inject(method = "fromJson", at = @At("RETURN"))
    private static void addStagePredicate(@Nullable JsonElement json, CallbackInfoReturnable<PlayerPredicate> cir) {
        PlayerPredicate predicate = cir.getReturnValue();
        if (predicate != ANY && json != null && !json.isJsonNull()) {
            Set<Identifier> must = ((StagePredicateHolder) predicate).stages$getMust();
            Set<Identifier> mustNot = ((StagePredicateHolder) predicate).stages$getMustNot();

            JsonObject jsonObject = JsonHelper.asObject(json, "player");
            JsonObject jsonStages = JsonHelper.getObject(jsonObject, "stages", null);
            if (jsonStages != null) {
                jsonStages.entrySet().forEach(entry -> {
                    Identifier stage = new Identifier(entry.getKey());
                    if (!Stages.isRegistered(stage)) {
                        throw new JsonParseException("Unregistered stage " + stage);
                    }
                    if (JsonHelper.asBoolean(entry.getValue(), "stage")) {
                        must.add(stage);
                    } else {
                        mustNot.add(stage);
                    }
                });
            }
        }
    }

    @SuppressWarnings("ConstantConditions")
    @Inject(method = "toJson", at = @At("RETURN"))
    private void addStagesToJson(CallbackInfoReturnable<JsonElement> cir) {
        JsonElement json = cir.getReturnValue();
        if ((Object) this != ANY && json.isJsonObject()) {
            JsonObject jsonObject = json.getAsJsonObject();
            if (!(must.isEmpty() && mustNot.isEmpty())) {
                JsonObject stages = new JsonObject();
                must.forEach(id -> stages.addProperty(id.toString(), true));
                mustNot.forEach(id -> stages.addProperty(id.toString(), false));
                jsonObject.add("stages", stages);
            }
        }
    }

    @Override
    public Set<Identifier> stages$getMust() {
        return must;
    }

    @Override
    public Set<Identifier> stages$getMustNot() {
        return mustNot;
    }

}
