package badasintended.blockstages.mixin.client;

import badasintended.blockstages.BlockStages;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.chunk.ChunkRendererRegion;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChunkRendererRegion.class)
public abstract class ChunkRendererRegionMixin {

    @Inject(method = "getBlockState", at = @At("RETURN"), cancellable = true)
    private void replaceBlockState(BlockPos pos, CallbackInfoReturnable<BlockState> cir) {
        cir.setReturnValue(BlockStages.getFakeBlockState(cir.getReturnValue()));
    }

}
