package badasintended.stages.impl.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.InvalidParameterException;
import java.util.Map;

import badasintended.stages.api.config.ConfigHolder;
import badasintended.stages.api.config.SyncedConfig;
import com.google.gson.Gson;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.fabricmc.loader.api.FabricLoader;

public class ConfigHolderImpl<T> implements ConfigHolder<T> {

    public static final Map<String, ConfigHolder<?>> CONFIGS = new Object2ObjectOpenHashMap<>();
    public static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("stages").toAbsolutePath();

    public static <T> ConfigHolder<T> create(Class<T> configClass, String name, Gson gson) {
        if (ConfigHolderImpl.CONFIGS.containsKey(name)) {
            throw new InvalidParameterException("Config with name " + name + " is already present!");
        } else {
            ConfigHolder<T> config = new ConfigHolderImpl<>(configClass, name, gson);
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


    private final Class<T> clazz;
    private final Path path;
    private final Gson gson;
    private final boolean synced;

    private T config = null;

    private ConfigHolderImpl(Class<T> clazz, String name, Gson gson) {
        this.clazz = clazz;
        this.path = CONFIG_PATH.resolve(name + ".json").toAbsolutePath();
        this.gson = gson;
        this.synced = SyncedConfig.class.isAssignableFrom(clazz);
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
        return config;
    }

    @Override
    public void destroy() {
        config = null;
    }

    @Override
    public void save() {
        try {
            Files.write(path, gson.toJson(get()).getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
