package badasintended.stages.api.config;

import java.util.function.Consumer;

import badasintended.stages.impl.config.ConfigHolderImpl;
import com.google.gson.GsonBuilder;

/**
 * Config holder with optional s2c syncing
 * <ul>
 *     <li>Config will be {@link #destroy() destroyed} on datapack load/reload</li>
 *     <li>Config Gson's {@link GsonBuilder#enableComplexMapKeySerialization() complexMapKeySerialization} is enabled</li>
 * </ul>
 */
public interface ConfigHolder<T> {

    /**
     * Create new config with default gson<br>
     *
     * @param configClass must have a constructor with empty parameter
     * @param name        Config json can be located on {@code .minecraft/config/stages/<name>.json}
     * @param synced      whether this config should be synced to client
     */
    static <T> ConfigHolder<T> create(Class<T> configClass, String name, boolean synced) {
        return create(configClass, name, synced, gsonBuilder -> {});
    }

    /**
     * Create new config with custom gson<br>
     *
     * @param configClass must have a constructor with empty parameter
     * @param name        Config json can be located on {@code .minecraft/config/stages/<name>.json}
     * @param synced      whether this config should be synced to client
     * @param gson        <b>do not call {@link GsonBuilder#setPrettyPrinting()}</b>
     */
    static <T> ConfigHolder<T> create(Class<T> configClass, String name, boolean synced, Consumer<GsonBuilder> gson) {
        return ConfigHolderImpl.create(configClass, name, synced, gson);
    }


    /**
     * @return whether this config should be synced to client
     */
    boolean isSynced();

    /**
     * @return the config object
     */
    T get();

    /**
     * Invalidate config and force reload on next {@link #get()}.<br>
     * All changes to config will be lost.
     */
    void destroy();

    /**
     * Try to save config to json file
     */
    void save();

    String toJson();

    String toPrettyJson();

    void fromJson(String json);

}
