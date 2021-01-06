package badasintended.itemstages.mixin;

import java.util.Map;
import java.util.Set;

import badasintended.itemstages.ItemStages;
import badasintended.itemstages.ItemStagesHolder;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin implements ItemStagesHolder {

    @Shadow
    private ItemStack selectedItem;

    @Shadow
    public abstract boolean dropSelectedItem(boolean dropEntireStack);

    @Unique
    private final Map<Item, Set<CompoundTag>> lockedItems = new Object2ObjectOpenHashMap<>();

    @Override
    public Map<Item, Set<CompoundTag>> stages$getLockedItems() {
        return lockedItems;
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void dropLockedItem(CallbackInfo ci) {
        if (ItemStages.CONFIG.get().settings.dropWhenOnHand && ItemStages.isLocked((PlayerEntity) (Object) this, selectedItem)) {
            dropSelectedItem(true);
        }
    }

}
