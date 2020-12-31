package badasintended.stages.impl.item.mixin.client;

import badasintended.stages.impl.item.ItemStages;
import badasintended.stages.impl.item.ItemStagesConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.item.ItemModels;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.entity.player.PlayerEntity;
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
        PlayerEntity player = MinecraftClient.getInstance().player;
        if (ItemStagesConfig.get().settings.isChangeModel() && player != null && ItemStages.isLocked(player, stack)) {
            cir.setReturnValue(getModel(ItemStages.UNKNOWN_ITEM));
        }
    }

}
