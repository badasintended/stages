package badasintended.stages.api;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;

public final class StagesUtil {

    public static final String MOD_ID = "stages";

    public static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("stages");

    static {
        try {
            Files.createDirectories(CONFIG_PATH);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Identifier id(String path) {
        return new Identifier(MOD_ID, path);
    }

}
