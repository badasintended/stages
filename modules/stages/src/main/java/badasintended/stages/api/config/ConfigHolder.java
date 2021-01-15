package badasintended.stages.api.config;

import java.util.function.Consumer;

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
     * @param name  config json can be located on {@code .minecraft/config/stages/<name>.json}
     */
    static <T> Builder<T> of(Class<T> clazz, String name) {
        return new ConfigHolderImpl.Builder<>(clazz, name);
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


    interface Builder<T> {

        /**
         * Make this config synced to client.<br>
         * <b>Config class must implements {@link SyncedConfig}!</b>
         *
         * @throws UnsupportedOperationException if class does not implement {@link SyncedConfig}
         */
        Builder<T> synced();

        /**
         * Use custom GSON for (de)serializing.
         */
        Builder<T> gson(Gson gson);

        Builder<T> transformer(Consumer<T> consumer);

        /**
         * Build the config holder.
         */
        ConfigHolder<T> build();

    }

}
