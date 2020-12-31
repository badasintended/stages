package badasintended.stages.impl.item.mixin.client;

import java.util.ArrayList;
import java.util.List;

import badasintended.stages.impl.item.ItemStages;
import badasintended.stages.impl.item.ItemStagesConfig;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {

    @Unique
    private static final List<Text> LOCKED_ITEM = new ArrayList<>();

    static {
        LOCKED_ITEM.add(new TranslatableText("item.stages.unknown"));
        LOCKED_ITEM.add(new TranslatableText("item.stages.unknown.tooltip").formatted(Formatting.GRAY));
    }

    @Inject(method = "getTooltip", at = @At("HEAD"), cancellable = true)
    private void hideLockedTooltip(@Nullable PlayerEntity player, TooltipContext context, CallbackInfoReturnable<List<Text>> cir) {
        if (ItemStagesConfig.get().settings.isHideTooltip() && player != null && ItemStages.isLocked(player, (ItemStack) (Object) this)) {
            cir.setReturnValue(LOCKED_ITEM);
        }
    }

}
