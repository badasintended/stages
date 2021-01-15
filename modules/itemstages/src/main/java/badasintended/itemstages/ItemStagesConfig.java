package badasintended.itemstages;

import java.io.IOException;
import java.util.Map;

import badasintended.stages.api.config.SyncedConfig;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.fabricmc.fabric.api.tag.TagRegistry;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ItemStagesConfig implements SyncedConfig {

    public final Settings settings = new Settings();
    public final Map<Identifier, Entry> entries = new Object2ObjectOpenHashMap<>();

    @Override
    public void toBuf(PacketByteBuf buf) {
        settings.toBuf(buf);
        buf.writeVarInt(entries.size());
        entries.forEach((key, value) -> {
            buf.writeIdentifier(key);
            value.toBuf(buf);
        });
    }

    @Override
    public void fromBuf(PacketByteBuf buf) {
        settings.fromBuf(buf);
        entries.clear();
        int size = buf.readVarInt();
        for (int i = 0; i < size; i++) {
            Entry entry = new Entry();
            entries.put(buf.readIdentifier(), entry);
            entry.fromBuf(buf);
        }
    }

    public static class Settings implements SyncedConfig {

        // @formatter:off
        public boolean
            dropWhenOnHand     = true,
            dropWhenOnCursor   = true,
            changeModel        = true,
            hideTooltip        = true,
            preventToInventory = true,
            hideFromRei        = true;
        // @formatter:on

        @Override
        public void toBuf(PacketByteBuf buf) {
            buf.writeBoolean(dropWhenOnHand);
            buf.writeBoolean(dropWhenOnCursor);
            buf.writeBoolean(changeModel);
            buf.writeBoolean(hideTooltip);
            buf.writeBoolean(preventToInventory);
            buf.writeBoolean(hideFromRei);
        }

        @Override
        public void fromBuf(PacketByteBuf buf) {
            dropWhenOnHand = buf.readBoolean();
            dropWhenOnCursor = buf.readBoolean();
            changeModel = buf.readBoolean();
            hideTooltip = buf.readBoolean();
            preventToInventory = buf.readBoolean();
            hideFromRei = buf.readBoolean();
        }

    }

    public static class Entry implements SyncedConfig {

        public Item item = Items.AIR;
        public Tag.Identified<Item> tag = null;
        public CompoundTag nbt = ItemStages.EMPTY_TAG;

        @Override
        public void toBuf(PacketByteBuf buf) {
            buf.writeVarInt(Registry.ITEM.getRawId(item));
            buf.writeBoolean(tag != null);
            if (tag != null) {
                buf.writeIdentifier(tag.getId());
            }
            buf.writeCompoundTag(nbt);
        }

        @Override
        public void fromBuf(PacketByteBuf buf) {
            item = Registry.ITEM.get(buf.readVarInt());
            if (buf.readBoolean()) {
                tag = (Tag.Identified<Item>) TagRegistry.item(buf.readIdentifier());
            } else {
                tag = null;
            }
            nbt = buf.readCompoundTag();
        }

        public static class Adapter extends TypeAdapter<Entry> {

            @Override
            public Entry read(JsonReader in) throws IOException {
                Item item = Items.AIR;
                Tag.Identified<Item> tag = null;
                CompoundTag nbt = ItemStages.EMPTY_TAG;

                String id = "";
                if (in.peek() == JsonToken.STRING) {
                    id = in.nextString();
                } else {
                    in.beginObject();
                    while (in.hasNext() && in.peek() != JsonToken.END_OBJECT) {
                        String propName = in.nextName();
                        switch (propName) {
                            case "target":
                                id = in.nextString();
                                break;
                            case "nbt":
                                try {
                                    nbt = StringNbtReader.parse(in.nextString());
                                } catch (CommandSyntaxException e) {
                                    throw new IOException(e);
                                }
                                break;
                        }
                    }
                    in.endObject();
                }

                if (id.isEmpty()) {
                    throw new IOException("Needs item/tag id");
                }

                if (id.startsWith("#")) {
                    tag = (Tag.Identified<Item>) TagRegistry.item(new Identifier(id.substring(1)));
                } else {
                    item = Registry.ITEM.get(new Identifier(id));
                }

                Entry entry = new Entry();
                entry.item = item;
                entry.tag = tag;
                entry.nbt = nbt;

                return entry;
            }

            @Override
            public void write(JsonWriter out, Entry value) throws IOException {
                if (value.nbt.isEmpty()) {
                    out.value(value.tag == null ? Registry.ITEM.getId(value.item).toString() : "#" + value.tag.getId().toString());
                } else {
                    out.beginObject();
                    out.name("target");
                    if (value.tag != null) {
                        out.value("#" + value.tag.getId().toString());
                    } else {
                        out.value(Registry.ITEM.getId(value.item).toString());
                    }
                    if (!value.nbt.isEmpty()) {
                        out.name("nbt");
                        out.value(value.nbt.toString());
                    }
                    out.endObject();
                }
            }

        }

    }

    public static class IdentifierAdapter extends TypeAdapter<Identifier> {

        @Override
        public Identifier read(JsonReader in) throws IOException {
            return ItemStages.id(in.nextString());
        }

        @Override
        public void write(JsonWriter out, Identifier value) throws IOException {
            out.value(value.toString());
        }

    }

}
