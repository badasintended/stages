package badasintended.blockstages.mixin.waila;

import badasintended.blockstages.BlockStages;
import mcp.mobius.waila.overlay.RayTracing;
import net.minecraft.block.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Pseudo
@Mixin(RayTracing.class)
public abstract class RayTracingMixin {

    @ModifyVariable(
        method = "getIdentifierItems",
        at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/World;getBlockState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/BlockState;")
    )
    private BlockState replaceBlockState(BlockState state) {
        return BlockStages.getFakeBlockState(state);
    }

}
