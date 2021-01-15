package badasintended.stages.impl.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.InvalidParameterException;
import java.util.Map;
import java.util.function.Consumer;

import badasintended.stages.api.config.ConfigHolder;
import badasintended.stages.api.config.SyncedConfig;
import com.google.gson.Gson;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.fabricmc.loader.api.FabricLoader;

public class ConfigHolderImpl<T> implements ConfigHolder<T> {

    public static final Map<String, ConfigHolderImpl<?>> CONFIGS = new Object2ObjectOpenHashMap<>();
    public static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("stages").toAbsolutePath();

    static {
        try {
            Files.createDirectories(ConfigHolderImpl.CONFIG_PATH);
        } catch (IOException e) {
            throw new RuntimeException("Failed to create config folder", e);
        }
    }


    private final Class<T> clazz;
    private final Path path;
    private final boolean synced;
    private final Gson gson;
    private final Consumer<T> transformer;

    private boolean transform = false;

    private T config = null;

    private ConfigHolderImpl(Class<T> clazz, String name, boolean synced, Gson gson, Consumer<T> transformer) {
        this.clazz = clazz;
        this.path = CONFIG_PATH.resolve(name + ".json").toAbsolutePath();
        this.synced = synced;
        this.gson = gson;
        this.transformer = transformer;
    }

    @SuppressWarnings("unchecked")
    public void set(Object config) {
        this.config = (T) config;
    }

    @Override
    public boolean isSynced() {
        return synced;
    }

    @Override
    public T get() {
        boolean transform = this.transform;
        this.transform = false;
        if (config == null) {
            try {
                if (Files.notExists(path)) {
                    config = clazz.getDeclaredConstructor().newInstance();
                } else {
                    config = gson.fromJson(String.join("\n", Files.readAllLines(path)), clazz);
                }
            } catch (Exception e) {
                throw new RuntimeException("Failed to get config " + path, e);
            }
            save();
        }
        if (transform) {
            transformer.accept(config);
        }
        return config;
    }

    @Override
    public void destroy() {
        config = null;
        transform = true;
    }

    @Override
    public void save() {
        try {
            Files.write(path, gson.toJson(get()).getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static class Builder<T> implements ConfigHolder.Builder<T> {

        private final Class<T> clazz;
        private final String name;

        private boolean synced = false;
        private Gson gson = ConfigHolder.GSON;
        private Consumer<T> transformer = t -> {};

        public Builder(Class<T> clazz, String name) {
            this.clazz = clazz;
            this.name = name;
        }

        @Override
        public ConfigHolder.Builder<T> synced() {
            if (SyncedConfig.class.isAssignableFrom(clazz)) {
                synced = true;
                return this;
            }
            throw new UnsupportedOperationException("Class " + clazz.getName() + " does not implement " + SyncedConfig.class.getName());
        }

        @Override
        public ConfigHolder.Builder<T> gson(Gson gson) {
            this.gson = gson;
            return this;
        }

        @Override
        public ConfigHolder.Builder<T> transformer(Consumer<T> transformer) {
            this.transformer = transformer;
            return this;
        }

        @Override
        public ConfigHolder<T> build() {
            if (ConfigHolderImpl.CONFIGS.containsKey(name)) {
                throw new InvalidParameterException("Config with name " + name + " is already present!");
            } else {
                ConfigHolderImpl<T> config = new ConfigHolderImpl<>(clazz, name, synced, gson, transformer);
                CONFIGS.put(name, config);
                return config;
            }
        }

    }

}
