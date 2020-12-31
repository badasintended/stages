package badasintended.stages.impl.item;

import java.util.Map;
import java.util.Set;

import badasintended.stages.api.data.Stages;
import badasintended.stages.api.event.StageEvents;
import badasintended.stages.api.init.StagesInit;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;

public class ItemStages implements StagesInit {

    public static final CompoundTag EMPTY_TAG = new CompoundTag();

    public static boolean isLocked(PlayerEntity player, ItemStack stack) {
        if (!stack.isEmpty()) {
            Map<Item, Set<CompoundTag>> locked = ((ItemStageHolder) player).stages$getLockedItems();
            Item item = stack.getItem();
            if (locked.containsKey(item)) {
                Set<CompoundTag> nbtSet = locked.get(item);
                CompoundTag tag = stack.getTag();
                return nbtSet.contains(tag == null ? ItemStages.EMPTY_TAG : tag);
            }
        }
        return false;
    }

    public static void editLockedItems(Stages stages, Identifier stage, boolean unlock) {
        Map<Identifier, ItemStagesConfig.Entry> entries = ItemStagesConfig.get().entries;
        if (entries.containsKey(stage)) {
            Map<Item, Set<CompoundTag>> locked = ((ItemStageHolder) stages.getPlayer()).stages$getLockedItems();
            ItemStagesConfig.Entry entry = entries.get(stage);

            if (entry.tag != null) {
                for (Item item : entry.tag.values()) {
                    if (!unlock) {
                        locked.computeIfAbsent(item, i -> new ObjectOpenHashSet<>())
                            .add(entry.nbt);
                    } else if (locked.containsKey(item)) {
                        Set<CompoundTag> nbt = locked.get(item);
                        nbt.remove(entry.nbt);
                        if (nbt.isEmpty()) {
                            locked.remove(item);
                        }
                    }
                }
            } else if (entry.item != Items.AIR) {
                if (!unlock) {
                    locked.computeIfAbsent(entry.item, i -> new ObjectOpenHashSet<>())
                        .add(entry.nbt);
                } else if (locked.containsKey(entry.item)) {
                    Set<CompoundTag> nbt = locked.get(entry.item);
                    nbt.remove(entry.nbt);
                    if (nbt.isEmpty()) {
                        locked.remove(entry.item);
                    }
                }
            }
        }

    }

    @Override
    public void onStagesInit() {
        ServerLifecycleEvents.SERVER_STARTING.register(server -> {
            ItemStagesConfig.destroy();
            ItemStagesConfig.get().entries.forEach((id, entry) -> {
                Stages.register(id);
            });
        });

        ServerPlayConnectionEvents.INIT.register((handler, server) -> {
            Map<Identifier, ItemStagesConfig.Entry> entries = ItemStagesConfig.get().entries;
            Stages stages = Stages.get(handler.player);
            entries.forEach((id, entry) -> {
                editLockedItems(stages, id, false);
            });
        });

        StageEvents.ADDED.register((stages, stage) -> {
            editLockedItems(stages, stage, true);
        });

        StageEvents.REMOVED.register(((stages, stage) -> {
            editLockedItems(stages, stage, false);
        }));
    }

}
