package badasintended.blockstages.mixin.client;

import badasintended.blockstages.BlockStages;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.sound.BlockSoundGroup;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractBlock.AbstractBlockState.class)
public abstract class AbstractBlockStateMixin {

    @Inject(method = "getSoundGroup", at = @At("RETURN"), cancellable = true)
    private void fakeSoundGroup(CallbackInfoReturnable<BlockSoundGroup> cir) {
        BlockState self = (BlockState) (Object) this;
        BlockState fake = BlockStages.getFakeBlockState(self);
        if (fake != self) {
            cir.setReturnValue(fake.getSoundGroup());
        }
    }

}
