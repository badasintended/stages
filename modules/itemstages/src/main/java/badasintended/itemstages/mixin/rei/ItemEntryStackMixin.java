package badasintended.itemstages.mixin.rei;

import badasintended.itemstages.ItemStages;
import me.shedaniel.rei.api.EntryStack;
import me.shedaniel.rei.impl.ItemEntryStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Pseudo
@Mixin(value = ItemEntryStack.class, remap = false)
public abstract class ItemEntryStackMixin implements EntryStack {

    @ModifyArg(
        method = "getTooltip",
        at = @At(value = "INVOKE", target = "Lme/shedaniel/rei/api/ClientHelper;appendModIdToTooltips(Ljava/util/List;Ljava/lang/String;)Ljava/util/List;")
    )
    private String appendModIdToTooltips(String truth) {
        return ItemStages.CONFIG.get().settings.hideTooltip && ItemStages.isLocked(getItemStack())
            ? ItemStages.MOD_ID
            : truth;
    }

}
