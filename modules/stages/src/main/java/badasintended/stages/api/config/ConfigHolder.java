package badasintended.stages.api.config;

import badasintended.stages.impl.config.ConfigHolderImpl;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Config holder with optional s2c syncing.<br>
 * Config will be {@link #destroy() destroyed} on datapack load/reload.
 *
 * @see SyncedConfig
 */
public interface ConfigHolder<T> {

    /**
     * A fairly basic GSON.
     */
    Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    /**
     * Create new config holder with default gson.
     *
     * @param clazz must have a constructor with empty parameter.
     *              <b>implement {@link SyncedConfig} to make it synced to client</b>
     * @param name  config json can be located on {@code .minecraft/config/stages/<name>.json}
     */
    static <T> ConfigHolder<T> of(Class<T> clazz, String name) {
        return of(clazz, name, GSON);
    }

    /**
     * Create new config holder with custom gson.
     *
     * @param clazz must have a constructor with empty parameter.
     *              <b>implement {@link SyncedConfig} to make it synced to client</b>
     * @param name  config json can be located on {@code .minecraft/config/stages/<name>.json}
     * @param gson  custom gson that will be used for (de)serializing
     */
    static <T> ConfigHolder<T> of(Class<T> clazz, String name, Gson gson) {
        return ConfigHolderImpl.create(clazz, name, gson);
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
     * Try to save config to json file.
     */
    void save();

}
