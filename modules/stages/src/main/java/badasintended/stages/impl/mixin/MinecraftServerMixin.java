package badasintended.stages.impl.mixin;

import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin {

    /*
    @Shadow
    private PlayerManager playerManager;

    @Inject(method = "loadDataPacks", at = @At("HEAD"))
    private static void callRegistry(
        ResourcePackManager resourcePackManager, DataPackSettings dataPackSettings, boolean safeMode, CallbackInfoReturnable<DataPackSettings> cir
    ) {
        reload();
    }

    @Inject(method = "reloadResources", at = @At("HEAD"))
    private void reloadResources(Collection<String> datapacks, CallbackInfoReturnable<CompletableFuture<Void>> cir) {
        reload();
        this.playerManager.getPlayerList().forEach(StagesMod::sync);
        StageEvents.REGISTRY_RELOADED.invoker().onRegistryReloaded((MinecraftServer) (Object) this);
        StagesMod.LOGGER.info("[stages] Registry and config resynced");
    }

    @Unique
    private static void reload() {
        ConfigHolderImpl.CONFIGS.values().forEach(ConfigHolder::destroy);
        StageRegistryImpl.destroy();
        StageEvents.REGISTRY.invoker().onRegister(StageRegistryImpl.get());
        StagesMod.LOGGER.info("[stages] Config destroyed and registry reloaded");
    }
     */

}
