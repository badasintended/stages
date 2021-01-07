package badasintended.blockstages;

import java.util.Map;

import badasintended.stages.api.config.ConfigHolder;
import badasintended.stages.api.data.Stages;
import badasintended.stages.api.event.StageEvents;
import badasintended.stages.api.init.StagesInit;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import static badasintended.stages.api.StagesUtil.id;
import static badasintended.stages.api.StagesUtil.s2c;

public class BlockStages implements StagesInit {

    public static final Identifier INITIALIZE = id("block/init");

    public static final ConfigHolder<BlockStagesConfig> CONFIG = ConfigHolder.create(
        BlockStagesConfig.class, "block", true, gson -> gson
            .registerTypeAdapter(Identifier.class, new BlockStagesConfig.IdentifierAdapter())
            .registerTypeAdapter(BlockStagesConfig.Entry.class, new BlockStagesConfig.Entry.Adapter())
    );

    public static BlockState getFakeBlockState(@Nullable PlayerEntity player, BlockState state) {
        if (player != null && !player.isCreative()) {
            Map<Block, Block> locked = ((BlockStagesHolder) player).stages$getLockedBlocks();
            Block block = state.getBlock();
            if (locked.containsKey(block)) {
                return locked.get(block).getDefaultState();
            }
        }
        return state;
    }

    @Environment(EnvType.CLIENT)
    public static BlockState getFakeBlockState(BlockState state) {
        return getFakeBlockState(MinecraftClient.getInstance().player, state);
    }

    public static void init(PlayerEntity player) {
        Map<Identifier, BlockStagesConfig.Entry> entries = CONFIG.get().entries;
        Map<Block, Block> lockedBlocks = ((BlockStagesHolder) player).stages$getLockedBlocks();
        lockedBlocks.clear();
        entries.forEach((id, entry) -> editLockedBlock(Stages.get(player), id));
    }

    private static void editLockedBlock(Stages stages, Identifier stage) {
        boolean unlock = stages.contains(stage);
        Map<Identifier, BlockStagesConfig.Entry> entries = CONFIG.get().entries;
        if (entries.containsKey(stage)) {
            BlockStagesHolder holder = (BlockStagesHolder) stages.getPlayer();
            Map<Block, Block> lockedBlocks = holder.stages$getLockedBlocks();
            BlockStagesConfig.Entry entry = entries.get(stage);
            if (entry.tag != null) {
                for (Block block : entry.tag.values()) {
                    if (unlock) {
                        lockedBlocks.remove(block);
                    } else {
                        lockedBlocks.putIfAbsent(block, entry.as);
                    }
                }
            } else if (entry.block != Blocks.AIR) {
                if (!unlock) {
                    lockedBlocks.putIfAbsent(entry.block, entry.as);
                } else {
                    lockedBlocks.remove(entry.block);
                }
            }
            if (stages.isClient()) {
                holder.stages$setReload(true);
            }
        }
    }

    @Override
    public void onStagesInit() {
        StageEvents.REGISTRY.register(registry ->
            registry.register(CONFIG.get().entries.keySet())
        );

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            init(handler.player);
            s2c(handler.player, INITIALIZE, buf -> {});
        });

        StageEvents.ADDED.register(BlockStages::editLockedBlock);
        StageEvents.REMOVED.register(BlockStages::editLockedBlock);
        StageEvents.REGISTRY_RELOADED.register(server -> server.getPlayerManager().getPlayerList().forEach(player -> {
            init(player);
            s2c(player, INITIALIZE, buf -> {});
        }));
    }

}
