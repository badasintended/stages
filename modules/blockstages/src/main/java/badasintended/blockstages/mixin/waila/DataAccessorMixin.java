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
public abstract class DataAccessorMixin {

    @Shadow
    public BlockState state;

    @Shadow
    public Block block;

    @Inject(
        method = "set(Lnet/minecraft/world/World;Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/hit/HitResult;Lnet/minecraft/entity/Entity;D)V",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getBlockEntity(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/entity/BlockEntity;"),
        remap = false
    )
    private void replaceBlockState(World world, PlayerEntity player, HitResult hit, Entity viewEntity, double partialTicks, CallbackInfo ci) {
        state = BlockStages.getFakeBlockState(player, state);
        block = state.getBlock();
    }

}
