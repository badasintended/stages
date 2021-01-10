package badasintended.stages.api.config;

import net.minecraft.network.PacketByteBuf;

/**
 * A config that can be synced to client.
 */
public interface SyncedConfig {

    /**
     * Write config data to buffer. Used in S2C sync.
     */
    void toBuf(PacketByteBuf buf);

    /**
     * Read config data to buffer. Used in S2C sync.
     */
    void fromBuf(PacketByteBuf buf);

}
