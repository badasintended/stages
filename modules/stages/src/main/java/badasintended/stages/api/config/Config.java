package badasintended.stages.api.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.InvalidParameterException;
import java.util.Map;
import java.util.function.Consumer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.fabricmc.loader.api.FabricLoader;

/**
 * Config holder with optional s2c syncing
 * <ul>
 *     <li>Config will be {@link #destroy() destroyed} on datapack load/reload</li>
 *     <li>Config Gson's {@link GsonBuilder#enableComplexMapKeySerialization() complexMapKeySerialization} is enabled</li>
 * </ul>
 */
public final class Config<T> {

    public static final Map<String, Config<?>> CONFIGS = new Object2ObjectOpenHashMap<>();

    public static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("stages").toAbsolutePath();

    /**
     * Create new config with default gson<br>
     * Config json can be located on {@code .minecraft/config/stages/<name>.json}
     *
     * @param configClass must have a constructor with empty parameter
     * @param synced      whether this config should be synced to client
     */
    public static <T> Config<T> create(Class<T> configClass, String name, boolean synced) {
        return create(configClass, name, synced, gsonBuilder -> {});
    }

    /**
     * Create new config with custom gson<br>
     * Config json can be located on {@code .minecraft/config/stages/<name>.json}
     *
     * @param configClass must have a constructor with empty parameter
     * @param synced      whether this config should be synced to client
     * @param gson        <b>do not call {@link GsonBuilder#setPrettyPrinting()}</b>
     */
    public static <T> Config<T> create(Class<T> configClass, String name, boolean synced, Consumer<GsonBuilder> gson) {
        if (CONFIGS.containsKey(name)) {
            throw new InvalidParameterException("Config with name " + name + " is already present!");
        } else {
            Config<T> config = new Config<>(configClass, name, synced, gson);
            CONFIGS.put(name, config);
            return config;
        }
    }

    private static GsonBuilder createGsonBuilder() {
        return new GsonBuilder()
            .enableComplexMapKeySerialization();
    }

    static {
        try {
            Files.createDirectories(Config.CONFIG_PATH);
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

    private Config(Class<T> configClass, String name, boolean synced, Consumer<GsonBuilder> gson) {
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

    /**
     * @return whether this config should be synced to client
     */
    public boolean isSynced() {
        return synced;
    }

    /**
     * @return the config object
     */
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

    /**
     * Invalidate config and force reload on next {@link #get()}.<br>
     * All changes to config will be lost.
     */
    public void destroy() {
        config = null;
    }

    /**
     * Try to save config to json file
     */
    public void save() {
        try {
            Files.write(path, toPrettyJson().getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String toJson() {
        return uglyGson.toJson(get());
    }

    public String toPrettyJson() {
        return prettyGson.toJson(get());
    }

    public void fromJson(String json) {
        config = prettyGson.fromJson(json, configClass);
    }

}
