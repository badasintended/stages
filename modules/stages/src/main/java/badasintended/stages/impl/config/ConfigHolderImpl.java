package badasintended.stages.impl.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.InvalidParameterException;
import java.util.Map;
import java.util.function.Consumer;

import badasintended.stages.api.config.ConfigHolder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.fabricmc.loader.api.FabricLoader;

public class ConfigHolderImpl<T> implements ConfigHolder<T> {

    public static final Map<String, ConfigHolder<?>> CONFIGS = new Object2ObjectOpenHashMap<>();
    public static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("stages").toAbsolutePath();

    private static GsonBuilder createGsonBuilder() {
        return new GsonBuilder()
            .enableComplexMapKeySerialization();
    }

    public static <T> ConfigHolder<T> create(Class<T> configClass, String name, boolean synced, Consumer<GsonBuilder> gson) {
        if (ConfigHolderImpl.CONFIGS.containsKey(name)) {
            throw new InvalidParameterException("Config with name " + name + " is already present!");
        } else {
            ConfigHolder<T> config = new ConfigHolderImpl<>(configClass, name, synced, gson);
            CONFIGS.put(name, config);
            return config;
        }
    }

    static {
        try {
            Files.createDirectories(ConfigHolderImpl.CONFIG_PATH);
        } catch (IOException e) {
            throw new RuntimeException("Failed to create config folder", e);
        }
    }


    private final Class<T> configClass;
    private final Path path;
    private final boolean synced;
    private final Gson uglyGson;
    private final Gson prettyGson;

    private T config = null;

    public ConfigHolderImpl(Class<T> configClass, String name, boolean synced, Consumer<GsonBuilder> gson) {
        this.configClass = configClass;
        this.path = CONFIG_PATH.resolve(name + ".json").toAbsolutePath();
        this.synced = synced;
        GsonBuilder ugly = createGsonBuilder();
        GsonBuilder pretty = createGsonBuilder().setPrettyPrinting();
        gson.accept(ugly);
        gson.accept(pretty);
        this.uglyGson = ugly.create();
        this.prettyGson = pretty.create();
    }

    @Override
    public boolean isSynced() {
        return synced;
    }

    @Override
    public T get() {
        if (config == null) {
            try {
                if (Files.notExists(path)) {
                    config = configClass.getDeclaredConstructor().newInstance();
                } else {
                    config = prettyGson.fromJson(String.join("\n", Files.readAllLines(path)), configClass);
                }
            } catch (Exception e) {
                throw new RuntimeException("Failed to get config " + path, e);
            }
            save();
        }
        return config;
    }

    @Override
    public void destroy() {
        config = null;
    }

    @Override
    public void save() {
        try {
            Files.write(path, toPrettyJson().getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toJson() {
        return uglyGson.toJson(get());
    }

    @Override
    public String toPrettyJson() {
        return prettyGson.toJson(get());
    }

    @Override
    public void fromJson(String json) {
        config = prettyGson.fromJson(json, configClass);
    }

}
