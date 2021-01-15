package badasintended.blockstages.mixin;

import badasintended.blockstages.BlockStages;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity {

    public ServerPlayerEntityMixin() {
        super(null, null, 0, null);
    }

    @ModifyArg(
        method = "handleFall",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;fall(DZLnet/minecraft/block/BlockState;Lnet/minecraft/util/math/BlockPos;)V")
    )
    private BlockState fall(BlockState truth) {
        return BlockStages.getFakeBlockState(this, truth);
    }

}
