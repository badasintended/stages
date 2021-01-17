package badasintended.stages.impl.mixin;

import java.util.Set;

import badasintended.stages.api.data.Stages;
import badasintended.stages.impl.advancement.StageAdvancementRewardHolder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.advancement.AdvancementRewards;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(AdvancementRewards.class)
public abstract class AdvancementRewardsMixin implements StageAdvancementRewardHolder {

    @Unique
    private final Set<Identifier> stageRewards = new ObjectOpenHashSet<>();

    @Inject(method = "apply", at = @At("HEAD"))
    private void applyStageRewards(ServerPlayerEntity player, CallbackInfo ci) {
        Stages.get(player).addAll(stageRewards);
    }

    @Inject(method = "fromJson", at = @At("RETURN"))
    private static void addStageRewardsFromJson(JsonObject json, CallbackInfoReturnable<AdvancementRewards> cir) {
        JsonArray array = JsonHelper.getArray(json, "stages", null);
        if (array != null) {
            Set<Identifier> stageRewards = ((StageAdvancementRewardHolder) cir.getReturnValue()).stages$getReward();
            for (int i = 0; i < array.size(); i++) {
                Identifier stage = new Identifier(JsonHelper.asString(array.get(i), "stages[" + i + ']'));
                stageRewards.add(stage);
            }
        }
    }

    @Inject(method = "toJson", at = @At(value = "RETURN", ordinal = 1), locals = LocalCapture.CAPTURE_FAILHARD)
    private void addStageRewardsToJson(CallbackInfoReturnable<JsonElement> cir, JsonObject jsonObject) {
        if (!stageRewards.isEmpty()) {
            JsonArray array = new JsonArray();
            stageRewards.forEach(it -> array.add(it.toString()));
            jsonObject.add("stages", array);
        }
    }

    @Inject(method = "toString", at = @At("RETURN"), cancellable = true)
    private void addStageRewardsToString(CallbackInfoReturnable<String> cir) {
        String result = cir.getReturnValue();
        result = result.substring(0, result.length() - 1) + ", stages=" + stageRewards + '}';
        cir.setReturnValue(result);
    }

    @Override
    public Set<Identifier> stages$getReward() {
        return stageRewards;
    }

}
