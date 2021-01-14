package badasintended.stages.api;

import java.util.function.Consumer;

import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public final class StagesUtil {

    public static final String MOD_ID = "stages";

    public static Identifier id(String path) {
        return new Identifier(MOD_ID, path);
    }

    public static void s2c(PlayerEntity player, Identifier id, Consumer<PacketByteBuf> consumer) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        consumer.accept(buf);
        ServerPlayNetworking.send((ServerPlayerEntity) player, id, buf);
    }

    @Environment(EnvType.CLIENT)
    public static void c2s(Identifier id, Consumer<PacketByteBuf> consumer) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        consumer.accept(buf);
        ClientPlayNetworking.send(id, buf);
    }

    public static void registerC2S(Identifier id, ServerPlayNetworking.PlayChannelHandler handler) {
        ServerPlayNetworking.registerGlobalReceiver(id, handler);
    }

    @Environment(EnvType.CLIENT)
    public static void registerS2C(Identifier id, ClientPlayNetworking.PlayChannelHandler handler) {
        ClientPlayNetworking.registerGlobalReceiver(id, handler);
    }

    public static boolean hasKubeJS() {
        return FabricLoader.getInstance().isModLoaded("kubejs");
    }

}
