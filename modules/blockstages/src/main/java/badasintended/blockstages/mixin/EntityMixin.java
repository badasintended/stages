package badasintended.blockstages.mixin;

import badasintended.blockstages.BlockStages;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(Entity.class)
public abstract class EntityMixin {

    @ModifyVariable(
        method = "move",
        at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/World;getBlockState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/BlockState;")
    )
    private BlockState getBlockState(BlockState truth) {
        if ((Object) this instanceof PlayerEntity) {
            return BlockStages.getFakeBlockState((PlayerEntity) (Object) this, truth);
        }
        return truth;
    }

}
