package badasintended.stages.impl.mixin;

import badasintended.stages.impl.predicate.StagePredicate;
import badasintended.stages.impl.predicate.StagePredicateHolder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.entity.Entity;
import net.minecraft.predicate.PlayerPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(PlayerPredicate.class)
public abstract class PlayerPredicateMixin implements StagePredicateHolder {

    @Unique
    private StagePredicate stagePredicate = StagePredicate.EMPTY;

    @Inject(
        method = "test",
        at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/server/network/ServerPlayerEntity;getStatHandler()Lnet/minecraft/stat/ServerStatHandler;"),
        locals = LocalCapture.CAPTURE_FAILHARD,
        cancellable = true
    )
    private void checkStages(Entity entity, CallbackInfoReturnable<Boolean> cir, ServerPlayerEntity serverPlayerEntity) {
        if (!stagePredicate.test(serverPlayerEntity)) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "fromJson", at = @At(value = "RETURN", ordinal = 1), locals = LocalCapture.CAPTURE_FAILHARD)
    private static void addStagePredicateFromJson(@Nullable JsonElement json, CallbackInfoReturnable<PlayerPredicate> cir, JsonObject jsonObject) {
        StagePredicate stagePredicate = StagePredicate.fromJson(jsonObject.get("stages"));
        ((StagePredicateHolder) cir.getReturnValue()).stages$setPredicate(stagePredicate);
    }

    @Inject(
        method = "toJson",
        at = @At(value = "INVOKE", ordinal = 0, target = "Lcom/google/gson/JsonObject;add(Ljava/lang/String;Lcom/google/gson/JsonElement;)V", remap = false),
        locals = LocalCapture.CAPTURE_FAILHARD
    )
    private void addStagePredicateToJson(CallbackInfoReturnable<JsonElement> cir, JsonObject jsonObject) {
        jsonObject.add("stages", stagePredicate.toJson());
    }

    @Override
    public void stages$setPredicate(StagePredicate predicate) {
        this.stagePredicate = predicate;
    }

}
