package badasintended.stages.impl.item.mixin.rei;

import badasintended.stages.impl.item.ItemStages;
import badasintended.stages.impl.item.ItemStagesConfig;
import me.shedaniel.rei.api.EntryStack;
import me.shedaniel.rei.gui.widget.EntryListWidget;
import me.shedaniel.rei.gui.widget.WidgetWithBounds;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = EntryListWidget.class, remap = false)
public abstract class EntryListWidgetMixin extends WidgetWithBounds {

    @Inject(method = "canLastSearchTermsBeAppliedTo", at = @At("HEAD"), cancellable = true)
    private void hideLockedStack(EntryStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (
            ItemStagesConfig.get().settings.isHideFromRei()
                && stack.getType() == EntryStack.Type.ITEM
                && ItemStages.isLocked(minecraft.player, stack.getItemStack())
        ) {
            cir.setReturnValue(false);
        }
    }

}
