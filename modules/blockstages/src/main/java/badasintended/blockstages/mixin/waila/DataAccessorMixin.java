package badasintended.blockstages.mixin.waila;

import badasintended.blockstages.BlockStages;
import mcp.mobius.waila.api.impl.DataAccessor;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(DataAccessor.class)
@SuppressWarnings("UnresolvedMixinReference")
public abstract class DataAccessorMixin {

    @Shadow
    public BlockState state;

    @Shadow
    public Block block;

    // loom doesn't seem to remap mod method descriptions so it needs intermediary name, great
    @Inject(
        method = "set(Lnet/minecraft/class_1937;Lnet/minecraft/class_1657;Lnet/minecraft/class_239;Lnet/minecraft/class_1297;D)V",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getBlockEntity(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/entity/BlockEntity;")
    )
    private void replaceBlockState(World world, PlayerEntity player, HitResult hit, Entity viewEntity, double partialTicks, CallbackInfo ci) {
        state = BlockStages.getFakeBlockState(player, state);
        block = state.getBlock();
    }

}
