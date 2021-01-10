package badasintended.itemstages.mixin.rei;

import badasintended.itemstages.ItemStages;
import me.shedaniel.rei.api.EntryStack;
import me.shedaniel.rei.gui.widget.EntryListWidget;
import me.shedaniel.rei.gui.widget.WidgetWithBounds;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(value = EntryListWidget.class, remap = false)
public abstract class EntryListWidgetMixin extends WidgetWithBounds {

    @Inject(method = "canLastSearchTermsBeAppliedTo", at = @At("HEAD"), cancellable = true)
    private void hideLockedStack(EntryStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (
            ItemStages.CONFIG.get().settings.hideFromRei
                && stack.getType() == EntryStack.Type.ITEM
                && ItemStages.isLocked(minecraft.player, stack.getItemStack())
        ) {
            cir.setReturnValue(false);
        }
    }

}
