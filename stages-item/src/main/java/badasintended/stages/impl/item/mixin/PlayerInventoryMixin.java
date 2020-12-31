package badasintended.stages.impl.item.mixin;

import badasintended.stages.impl.item.ItemStages;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerInventory.class)
public abstract class PlayerInventoryMixin {

    @Shadow
    @Final
    public PlayerEntity player;

    @Shadow
    private ItemStack cursorStack;

    @Inject(method = "setCursorStack", at = @At("HEAD"), cancellable = true)
    private void dropLockedItem(ItemStack stack, CallbackInfo ci) {
        if (ItemStages.CONFIG.get().settings.dropWhenOnCursor && ItemStages.isLocked(player, stack)) {
            player.dropItem(stack, false, true);
            cursorStack = ItemStack.EMPTY;
            ci.cancel();
        }
    }

    @Inject(method = "insertStack(ILnet/minecraft/item/ItemStack;)Z", at = @At("HEAD"), cancellable = true)
    private void preventLockedItem(int slot, ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (ItemStages.CONFIG.get().settings.preventToInventory && ItemStages.isLocked(player, stack)) {
            cir.setReturnValue(false);
        }
    }

}
