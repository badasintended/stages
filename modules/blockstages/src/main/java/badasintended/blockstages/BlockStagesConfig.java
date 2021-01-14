package badasintended.blockstages;

import java.io.IOException;

import badasintended.stages.api.config.SyncedConfig;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.fabricmc.fabric.api.tag.TagRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class BlockStagesConfig implements SyncedConfig {

    public final Object2ObjectOpenHashMap<Identifier, Entry> entries = new Object2ObjectOpenHashMap<>();

    @Override
    public void toBuf(PacketByteBuf buf) {
        buf.writeVarInt(entries.size());
        entries.forEach((key, val) -> {
            buf.writeIdentifier(key);
            val.toBuf(buf);
        });
    }

    @Override
    public void fromBuf(PacketByteBuf buf) {
        entries.clear();
        int size = buf.readVarInt();
        for (int i = 0; i < size; i++) {
            Entry entry = new Entry();
            entries.put(buf.readIdentifier(), entry);
            entry.fromBuf(buf);
        }
    }

    public static class Entry implements SyncedConfig {

        public Block block = Blocks.AIR;
        public Tag.Identified<Block> tag = null;
        public Block as = Blocks.AIR;

        @Override
        public void toBuf(PacketByteBuf buf) {
            buf.writeVarInt(Registry.BLOCK.getRawId(block));
            buf.writeBoolean(tag != null);
            if (tag != null) {
                buf.writeIdentifier(tag.getId());
            }
            buf.writeVarInt(Registry.BLOCK.getRawId(as));
        }

        @Override
        public void fromBuf(PacketByteBuf buf) {
            block = Registry.BLOCK.get(buf.readVarInt());
            tag = buf.readBoolean() ? (Tag.Identified<Block>) TagRegistry.block(buf.readIdentifier()) : null;
            as = Registry.BLOCK.get(buf.readVarInt());
        }

        public static class Adapter extends TypeAdapter<Entry> {

            @Override
            public Entry read(JsonReader in) throws IOException {
                Block block = Blocks.AIR;
                Tag.Identified<Block> tag = null;
                Block as = Blocks.AIR;

                in.beginObject();
                while (in.hasNext() && in.peek() != JsonToken.END_OBJECT) {
                    String name = in.nextName();
                    if (name.equals("target")) {
                        String id = in.nextString();
                        if (id.startsWith("#")) {
                            tag = (Tag.Identified<Block>) TagRegistry.block(new Identifier(id.substring(1)));
                        } else {
                            block = Registry.BLOCK.get(new Identifier(id));
                        }
                    } else if (name.equals("as")) {
                        as = Registry.BLOCK.get(new Identifier(in.nextString()));
                    }
                }
                in.endObject();

                Entry entry = new Entry();
                entry.block = block;
                entry.tag = tag;
                entry.as = as;

                return entry;
            }

            @Override
            public void write(JsonWriter out, Entry value) throws IOException {
                out.beginObject();
                out.name("target");
                if (value.tag != null) {
                    out.value("#" + value.tag.getId());
                } else {
                    out.value(Registry.BLOCK.getId(value.block).toString());
                }
                out.name("as");
                out.value(Registry.BLOCK.getId(value.as).toString());
                out.endObject();
            }

        }

    }

    public static class IdentifierAdapter extends TypeAdapter<Identifier> {

        @Override
        public Identifier read(JsonReader in) throws IOException {
            return BlockStages.id(in.nextString());
        }

        @Override
        public void write(JsonWriter out, Identifier value) throws IOException {
            out.value(value.toString());
        }

    }

}
