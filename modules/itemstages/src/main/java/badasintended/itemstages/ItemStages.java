package badasintended.itemstages;

import java.util.Map;
import java.util.Set;

import badasintended.stages.api.config.Config;
import badasintended.stages.api.data.Stages;
import badasintended.stages.api.event.StageEvents;
import badasintended.stages.api.init.StagesInit;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Nullable;

import static badasintended.stages.api.StagesUtil.id;
import static badasintended.stages.api.StagesUtil.s2c;

public class ItemStages implements StagesInit {

    public static final Item UNKNOWN_ITEM = new Item(new Item.Settings());

    public static final Config<ItemStagesConfig> CONFIG = Config.create(
        ItemStagesConfig.class, "item", true, gson -> gson
            .registerTypeAdapter(Identifier.class, new ItemStagesConfig.IdentifierAdapter())
            .registerTypeAdapter(ItemStagesConfig.Entry.class, new ItemStagesConfig.Entry.Adapter())
    );

    public static final CompoundTag EMPTY_TAG = new CompoundTag();

    public static final Identifier INITIALIZE = id("item/init");

    @Environment(EnvType.CLIENT)
    public static boolean isLocked(ItemStack stack) {
        return isLocked(MinecraftClient.getInstance().player, stack);
    }

    public static boolean isLocked(@Nullable PlayerEntity player, ItemStack stack) {
        if (!stack.isEmpty() && player != null && !player.isCreative()) {
            Map<Item, Set<CompoundTag>> locked = ((ItemStagesHolder) player).stages$getLockedItems();
            Item item = stack.getItem();
            if (locked.containsKey(item)) {
                Set<CompoundTag> nbtSet = locked.get(item);
                CompoundTag tag = stack.getTag();
                return nbtSet.contains(tag == null ? ItemStages.EMPTY_TAG : tag);
            }
        }
        return false;
    }

    public static void init(PlayerEntity player) {
        Map<Identifier, ItemStagesConfig.Entry> entries = CONFIG.get().entries;
        Map<Item, Set<CompoundTag>> locked = ((ItemStagesHolder) player).stages$getLockedItems();
        locked.clear();
        entries.forEach((id, entry) -> editLockedItems(Stages.get(player), id));
    }

    public static void editLockedItems(Stages stages, Identifier stage) {
        boolean unlock = stages.contains(stage);
        Map<Identifier, ItemStagesConfig.Entry> entries = CONFIG.get().entries;
        if (entries.containsKey(stage)) {
            PlayerEntity player = stages.getPlayer();
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
            Map<Item, Set<CompoundTag>> locked = ((ItemStagesHolder) player).stages$getLockedItems();
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
        }
    }

    @Override
    public void onStagesInit() {
        Registry.register(Registry.ITEM, new Identifier("itemstages:unknown"), UNKNOWN_ITEM);

        StageEvents.REGISTRY.register(registry ->
            registry.register(CONFIG.get().entries.keySet())
        );

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            init(handler.player);
            s2c(handler.player, INITIALIZE, buf -> {});
        });

        StageEvents.ADDED.register(ItemStages::editLockedItems);
        StageEvents.REMOVED.register(ItemStages::editLockedItems);
        StageEvents.REGISTRY_RELOADED.register(server -> server.getPlayerManager().getPlayerList().forEach(player -> {
            init(player);
            s2c(player, INITIALIZE, buf -> {});
        }));
    }

}
