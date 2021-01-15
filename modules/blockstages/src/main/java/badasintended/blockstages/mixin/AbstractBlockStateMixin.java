package badasintended.blockstages.mixin;

import badasintended.blockstages.BlockStages;
import badasintended.blockstages.duck.EntityShapeContextDuck;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractBlock.AbstractBlockState.class)
public abstract class AbstractBlockStateMixin {

    @Unique
    private final BlockState self = (BlockState) (Object) this;

    @Inject(method = "onEntityCollision", at = @At("HEAD"), cancellable = true)
    private void onEntityCollision(World world, BlockPos pos, Entity entity, CallbackInfo ci) {
        if (entity instanceof PlayerEntity) {
            BlockState fake = BlockStages.getFakeBlockState((PlayerEntity) entity, self);
            if (fake != self) {
                fake.getBlock().onEntityCollision(fake, world, pos, entity);
                ci.cancel();
            }
        }
    }

    @Inject(method = "calcBlockBreakingDelta", at = @At("HEAD"), cancellable = true)
    private void calcBlockBreakingDelta(PlayerEntity player, BlockView world, BlockPos pos, CallbackInfoReturnable<Float> cir) {
        BlockState fake = BlockStages.getFakeBlockState(player, self);
        if (fake != self) {
            cir.setReturnValue(fake.calcBlockBreakingDelta(player, world, pos));
        }
    }

    @Inject(method = "onUse", at = @At("HEAD"), cancellable = true)
    private void onUse(World world, PlayerEntity player, Hand hand, BlockHitResult hit, CallbackInfoReturnable<ActionResult> cir) {
        BlockState fake = BlockStages.getFakeBlockState(player, self);
        if (fake != self) {
            cir.setReturnValue(ActionResult.PASS);
        }
    }

    @Inject(
        method = "getOutlineShape(Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/ShapeContext;)Lnet/minecraft/util/shape/VoxelShape;",
        at = @At("HEAD"), cancellable = true
    )
    private void getOutlineShape(BlockView world, BlockPos pos, ShapeContext context, CallbackInfoReturnable<VoxelShape> cir) {
        BlockState fake = BlockStages.getFakeBlockState(EntityShapeContextDuck.getPlayer(context), self);
        if (fake != self) {
            cir.setReturnValue(fake.getOutlineShape(world, pos, context));
        }
    }

    @Inject(
        method = "getCollisionShape(Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/ShapeContext;)Lnet/minecraft/util/shape/VoxelShape;",
        at = @At("HEAD"), cancellable = true
    )
    private void getCollisionShape(BlockView world, BlockPos pos, ShapeContext context, CallbackInfoReturnable<VoxelShape> cir) {
        BlockState fake = BlockStages.getFakeBlockState(EntityShapeContextDuck.getPlayer(context), self);
        if (fake != self) {
            cir.setReturnValue(fake.getCollisionShape(world, pos, context));
        }
    }

    @Inject(method = "getVisualShape", at = @At("HEAD"), cancellable = true)
    private void getVisualShape(BlockView world, BlockPos pos, ShapeContext context, CallbackInfoReturnable<VoxelShape> cir) {
        BlockState fake = BlockStages.getFakeBlockState(EntityShapeContextDuck.getPlayer(context), self);
        if (fake != self) {
            cir.setReturnValue(fake.getVisualShape(world, pos, context));
        }
    }

}
