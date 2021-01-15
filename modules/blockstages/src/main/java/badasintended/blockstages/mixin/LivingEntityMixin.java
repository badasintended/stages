package badasintended.blockstages.mixin;

import badasintended.blockstages.BlockStages;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {

    @Unique
    private BlockPos cache = BlockPos.ORIGIN;

    public LivingEntityMixin() {
        super(null, null);
    }

    @Inject(
        method = "travel",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getBlockState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/BlockState;"),
        locals = LocalCapture.CAPTURE_FAILHARD
    )
    private void travel(Vec3d movementInput, CallbackInfo ci, double d, BlockPos blockPos) {
        cache = blockPos;
    }

    @ModifyVariable(method = "travel", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/block/Block;getSlipperiness()F"))
    private float getSlipperiness(float truth) {
        if ((Object) this instanceof PlayerEntity) {
            return BlockStages.getFakeBlockState((PlayerEntity) (Object) this, world.getBlockState(cache)).getBlock().getSlipperiness();
        }
        return truth;
    }

}
