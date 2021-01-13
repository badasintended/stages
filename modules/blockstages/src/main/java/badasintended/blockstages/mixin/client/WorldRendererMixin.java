package badasintended.blockstages.mixin.client;

import badasintended.blockstages.BlockStages;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.WorldRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin {

    @ModifyArg(
        method = "render", index = 0,
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/block/BlockRenderManager;renderDamage(Lnet/minecraft/block/BlockState;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/world/BlockRenderView;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;)V")
    )
    private BlockState renderDamage(BlockState actual) {
        return BlockStages.getFakeBlockState(actual);
    }

}
