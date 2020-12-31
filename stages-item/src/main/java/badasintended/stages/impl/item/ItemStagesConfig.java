package badasintended.stages.impl.item;


import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import badasintended.stages.api.StageConstants;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.fabricmc.fabric.api.tag.TagRegistry;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ItemStagesConfig {

    private static final Path CONFIG_PATH = StageConstants.CONFIG_PATH.resolve("items.json");

    public static final Gson GSON = new GsonBuilder()
        .setPrettyPrinting()
        .registerTypeAdapter(Identifier.class, new IdSerializer())
        .registerTypeAdapter(Entry.class, new Entry.Serializer())
        .create();

    private static ItemStagesConfig config = null;

    public static ItemStagesConfig get() {
        if (config == null) {
            try {
                if (Files.notExists(CONFIG_PATH)) {
                    config = new ItemStagesConfig();
                    Files.write(CONFIG_PATH, GSON.toJson(config).getBytes());
                } else {
                    config = GSON.fromJson(String.join("\n", Files.readAllLines(CONFIG_PATH)), ItemStagesConfig.class);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return config;
    }

    public static void destroy() {
        config = null;
    }

    public final Map<Identifier, Entry> entries = new Object2ObjectOpenHashMap<>();

    public static class Entry {

        public final Item item;
        public final Tag.Identified<Item> tag;
        public final CompoundTag nbt;
        public final String name;

        public Entry(Item item, Tag.Identified<Item> tag, CompoundTag nbt, String name) {
            this.item = item;
            this.tag = tag;
            this.nbt = nbt;
            this.name = name;
        }

        public static class Serializer implements JsonSerializer<Entry>, JsonDeserializer<Entry> {

            @Override
            public Entry deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                Item item = Items.AIR;
                Tag.Identified<Item> tag = null;
                CompoundTag nbt = ItemStages.EMPTY_TAG;
                String name = "";

                String id;
                if (json.isJsonPrimitive()) {
                    id = json.getAsString();
                } else {
                    JsonObject obj = json.getAsJsonObject();
                    id = obj.getAsJsonPrimitive("id").getAsString();

                    if (obj.has("tag")) {
                        try {
                            nbt = StringNbtReader.parse(obj.get("tag").getAsString());
                        } catch (CommandSyntaxException e) {
                            throw new JsonParseException("Failed to parse NBT", e);
                        }
                    }

                    if (obj.has("name")) {
                        name = obj.get("name").getAsString();
                    }
                }

                if (id.startsWith("#")) {
                    tag = (Tag.Identified<Item>) TagRegistry.item(new Identifier(id.substring(1)));
                } else {
                    item = Registry.ITEM.get(new Identifier(id));
                }

                return new Entry(item, tag, nbt, name);
            }

            @Override
            public JsonElement serialize(Entry src, Type typeOfSrc, JsonSerializationContext context) {
                if (src.name.isEmpty() && src.nbt == null) {
                    return new JsonPrimitive(src.tag == null ? Registry.ITEM.getId(src.item).toString() : "#" + src.tag.getId().toString());
                }

                JsonObject obj = new JsonObject();

                if (src.tag != null) {
                    obj.addProperty("id", "#" + src.tag.getId().toString());
                } else {
                    obj.addProperty("id", Registry.ITEM.getId(src.item).toString());
                }
                if (src.nbt != ItemStages.EMPTY_TAG) {
                    obj.addProperty("tag", src.nbt.toString());
                }
                if (!src.name.isEmpty()) {
                    obj.addProperty("name", src.name);
                }

                return obj;
            }

        }

    }

    public static class IdSerializer implements JsonSerializer<Identifier>, JsonDeserializer<Identifier> {

        @Override
        public Identifier deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return new Identifier("item", json.getAsString());
        }

        @Override
        public JsonElement serialize(Identifier src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.getPath());
        }

    }

}
