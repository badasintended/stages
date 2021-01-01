package badasintended.stages.api.init;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * Convenient client side entrypoint.<br>
 * The JSON entry id is {@code stages:client}
 */
@Environment(EnvType.CLIENT)
public interface ClientStagesInit {

    void onStagesClientInit();

}
