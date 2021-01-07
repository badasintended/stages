package badasintended.blockstages.mixin.client;

import badasintended.blockstages.BlockStages;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.BlockDustParticle;
import net.minecraft.client.particle.SpriteBillboardParticle;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockDustParticle.class)
public abstract class BlockDustParticleMixin extends SpriteBillboardParticle {

    @Shadow
    @Final
    @Mutable
    private BlockState blockState;

    private BlockDustParticleMixin() {
        super(null, 0, 0, 0);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void replaceBlockState(
        ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, BlockState blockState, CallbackInfo ci
    ) {
        this.blockState = BlockStages.getFakeBlockState(blockState);
        setSprite(MinecraftClient.getInstance().getBlockRenderManager().getModels().getSprite(this.blockState));
    }

}
