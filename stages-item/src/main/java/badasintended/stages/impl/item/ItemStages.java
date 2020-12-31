package badasintended.stages.impl.item;

import java.util.Map;
import java.util.Set;

import badasintended.stages.api.StagesUtil;
import badasintended.stages.api.data.Stages;
import badasintended.stages.api.event.StageEvents;
import badasintended.stages.api.init.StagesInit;
import io.netty.buffer.Unpooled;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ItemStages implements StagesInit {

    public static final Item UNKNOWN_ITEM = new Item(new Item.Settings());

    public static final Identifier
        SYNC_LOCKED_ITEM = StagesUtil.id("item/sync_locked_item"),
        SYNC_SETTINGS = StagesUtil.id("item/sync_settings");

    public static final CompoundTag EMPTY_TAG = new CompoundTag();

    public static boolean isLocked(PlayerEntity player, ItemStack stack) {
        if (!stack.isEmpty() && !player.isCreative()) {
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

    public static void editLockedItems(Stages stages, Identifier stage) {
        boolean unlock = stages.contains(stage);
        Map<Identifier, ItemStagesConfig.Entry> entries = ItemStagesConfig.get().entries;
        if (entries.containsKey(stage)) {
            PlayerEntity player = stages.getPlayer();
            Map<Item, Set<CompoundTag>> locked = ((ItemStageHolder) stages.getPlayer()).stages$getLockedItems();
            ItemStagesConfig.Entry entry = entries.get(stage);

            if (entry.tag != null) {
                for (Item item : entry.tag.values()) {
                    editLockedItem(player, item, entry.nbt, unlock);
                }
            } else {
                editLockedItem(player, entry.item, entry.nbt, unlock);
            }
        }
    }

    public static void editLockedItem(PlayerEntity player, Item item, CompoundTag nbt, boolean unlock) {
        if (item != Items.AIR) {
            Map<Item, Set<CompoundTag>> locked = ((ItemStageHolder) player).stages$getLockedItems();
            if (!unlock) {
                locked.computeIfAbsent(item, i -> new ObjectOpenHashSet<>())
                    .add(nbt);
            } else if (locked.containsKey(item)) {
                Set<CompoundTag> nbtSet = locked.get(item);
                nbtSet.remove(nbt);
                if (nbtSet.isEmpty()) {
                    locked.remove(item);
                }
            }
            if (player instanceof ServerPlayerEntity) {
                PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
                buf.writeVarInt(Registry.ITEM.getRawId(item));
                buf.writeCompoundTag(nbt);
                buf.writeBoolean(unlock);
                ServerPlayNetworking.send((ServerPlayerEntity) player, SYNC_LOCKED_ITEM, buf);
            }
        }
    }

    @Override
    public void onStagesInit() {
        Registry.register(Registry.ITEM, new Identifier("stages-item:unknown"), UNKNOWN_ITEM);

        ServerLifecycleEvents.SERVER_STARTING.register(server -> {
            ItemStagesConfig.destroy();
            ItemStagesConfig.get().entries.forEach((id, entry) -> {
                Stages.register(id);
            });
        });

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
            ItemStagesConfig.Settings settings = ItemStagesConfig.get().settings;
            buf.writeBoolean(settings.isDropWhenOnHand());
            buf.writeBoolean(settings.isDropWhenOnCursor());
            buf.writeBoolean(settings.isDropWhenPicked());
            buf.writeBoolean(settings.isChangeModel());
            buf.writeBoolean(settings.isHideTooltip());
            buf.writeBoolean(settings.isPreventToInventory());
            handler.sendPacket(sender.createPacket(SYNC_SETTINGS, buf));

            Map<Identifier, ItemStagesConfig.Entry> entries = ItemStagesConfig.get().entries;
            Stages stages = Stages.get(handler.player);
            entries.forEach((id, entry) -> {
                editLockedItems(stages, id);
            });
        });

        StageEvents.ADDED.register(ItemStages::editLockedItems);
        StageEvents.REMOVED.register(ItemStages::editLockedItems);
        StageEvents.CLEARED.register(stages -> {
            Map<Identifier, ItemStagesConfig.Entry> entries = ItemStagesConfig.get().entries;
            entries.forEach((id, entry) -> {
                editLockedItems(stages, id);
            });
        });
    }

}
