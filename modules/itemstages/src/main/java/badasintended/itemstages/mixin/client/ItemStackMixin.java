package badasintended.itemstages.mixin.client;

import java.util.List;

import badasintended.itemstages.ItemStages;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {

    @Inject(method = "getTooltip", at = @At("HEAD"), cancellable = true)
    private void hideLockedTooltip(@Nullable PlayerEntity player, TooltipContext context, CallbackInfoReturnable<List<Text>> cir) {
        if (ItemStages.CONFIG.get().settings.hideTooltip && ItemStages.isLocked(player, (ItemStack) (Object) this)) {
            cir.setReturnValue(ItemStages.UNKNOWN_STACK.getTooltip(player, context));
        }
    }

    @Inject(method = "hasGlint", at = @At("HEAD"), cancellable = true)
    private void hasGlint(CallbackInfoReturnable<Boolean> cir) {
        if (ItemStages.CONFIG.get().settings.changeModel && ItemStages.isLocked(self())) {
            cir.setReturnValue(false);
        }
    }

    @Unique
    private ItemStack self() {
        return (ItemStack) (Object) this;
    }

}
