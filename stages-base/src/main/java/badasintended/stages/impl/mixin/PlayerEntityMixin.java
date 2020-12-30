package badasintended.stages.impl.mixin;

import java.util.Collection;

import badasintended.stages.api.data.Stages;
import badasintended.stages.impl.StagesMod;
import badasintended.stages.impl.data.StageHolder;
import badasintended.stages.impl.data.StagesImpl;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin implements StageHolder {

    @Unique
    protected final Stages stages = new StagesImpl((PlayerEntity) (Object) this);

    @Unique
    protected boolean sync = false;

    @Inject(method = "readCustomDataFromTag", at = @At("HEAD"))
    private void readStageData(CompoundTag tag, CallbackInfo ci) {
        stages.fromTag(tag);
        if (!stages.isClient()) {
            sync = true;
        }
    }

    @Inject(method = "writeCustomDataToTag", at = @At("HEAD"))
    private void writeStageData(CompoundTag tag, CallbackInfo ci) {
        stages.toTag(tag);
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void syncStageData(CallbackInfo ci) {
        if (sync) {
            PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
            if (!stages.isClient()) {
                Collection<Identifier> stages = this.stages.values();

                buf.writeVarInt(stages.size());
                stages.forEach(s -> buf.writeVarInt(Stages.getRawId(s)));

                ServerPlayNetworking.send((ServerPlayerEntity) this.stages.getPlayer(), StagesMod.SYNC_STAGES, buf);
            } else {
                ClientPlayNetworking.send(StagesMod.REQUEST_SYNC, buf);
            }
        }
        sync = false;
    }

    @Override
    public Stages stages$getStages() {
        return stages;
    }

    @Override
    public void stages$scheduleSync() {
        sync = true;
    }

}
