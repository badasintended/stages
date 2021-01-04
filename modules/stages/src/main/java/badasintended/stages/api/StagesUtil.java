package badasintended.stages.api;

import net.minecraft.util.Identifier;

public final class StagesUtil {

    public static final String MOD_ID = "stages";

    public static Identifier id(String path) {
        return new Identifier(MOD_ID, path);
    }

}
