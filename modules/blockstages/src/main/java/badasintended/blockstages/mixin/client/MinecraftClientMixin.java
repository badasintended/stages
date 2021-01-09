package badasintended.blockstages.mixin.client;

import badasintended.blockstages.BlockStages;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {

    @ModifyVariable(
        method = "doItemPick",
        at = @At(value = "INVOKE_ASSIGN", ordinal = 0, target = "Lnet/minecraft/client/world/ClientWorld;getBlockState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/BlockState;")
    )
    private BlockState replaceState(BlockState state) {
        return BlockStages.getFakeBlockState(state);
    }

}
