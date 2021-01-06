package badasintended.itemstages;

import java.util.Map;
import java.util.Set;

import net.minecraft.item.Item;
import net.minecraft.nbt.CompoundTag;

public interface ItemStagesHolder {

    Map<Item, Set<CompoundTag>> stages$getLockedItems();

}
