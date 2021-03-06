package badasintended.itemstages.mixin.client;

import badasintended.itemstages.ItemStages;
import net.minecraft.client.render.item.ItemModels;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemModels.class)
public abstract class ItemModelsMixin {

    @Shadow
    public @Nullable
    abstract BakedModel getModel(Item item);

    @Inject(method = "getModel(Lnet/minecraft/item/ItemStack;)Lnet/minecraft/client/render/model/BakedModel;", at = @At("HEAD"), cancellable = true)
    private void replaceLockedItemModel(ItemStack stack, CallbackInfoReturnable<BakedModel> cir) {
        if (ItemStages.CONFIG.get().settings.changeModel && ItemStages.isLocked(stack)) {
            cir.setReturnValue(getModel(ItemStages.UNKNOWN_ITEM));
        }
    }

}
