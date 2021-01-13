package badasintended.blockstages.mixin;

import badasintended.blockstages.duck.EntityShapeContextDuck;
import net.minecraft.block.EntityShapeContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityShapeContext.class)
public abstract class EntityShapeContextMixin implements EntityShapeContextDuck {

    @Unique
    private PlayerEntity player = null;

    @Inject(method = "<init>(Lnet/minecraft/entity/Entity;)V", at = @At("TAIL"))
    private void init(Entity entity, CallbackInfo ci) {
        if (entity instanceof PlayerEntity) {
            this.player = (PlayerEntity) entity;
        }
    }

    @Override
    public PlayerEntity stages$getPlayer() {
        return player;
    }

}
