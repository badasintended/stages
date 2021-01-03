package badasintended.stages.impl.mixin;

import badasintended.stages.api.data.Stages;
import badasintended.stages.impl.data.StageHolder;
import badasintended.stages.impl.data.StagesImpl;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin implements StageHolder {

    @Unique
    protected final StagesImpl stages = new StagesImpl((PlayerEntity) (Object) this);

    @Inject(method = "readCustomDataFromTag", at = @At("HEAD"))
    private void readStageData(CompoundTag tag, CallbackInfo ci) {
        stages.fromTag(tag);
    }

    @Inject(method = "writeCustomDataToTag", at = @At("HEAD"))
    private void writeStageData(CompoundTag tag, CallbackInfo ci) {
        stages.toTag(tag);
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void syncStageData(CallbackInfo ci) {
        stages.tick();
    }

    @Override
    public Stages stages$getStages() {
        return stages;
    }

}
