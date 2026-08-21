package dev.matthiesen.cobbled_level_control.common.network;

import dev.matthiesen.cobbled_level_control.common.CobbledLevelControl;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record CLCStatusHudSyncS2CPacket(CompoundTag snapshot) implements CustomPacketPayload {
    public static final ResourceLocation CHANNEL_ID = ResourceLocation.fromNamespaceAndPath(CobbledLevelControl.MOD_ID, "status_hud_sync");
    public static final Type<CLCStatusHudSyncS2CPacket> TYPE = new Type<>(CHANNEL_ID);
    public static final StreamCodec<RegistryFriendlyByteBuf, CLCStatusHudSyncS2CPacket> CODEC = new StreamCodec<>() {
        @Override
        public @NotNull CLCStatusHudSyncS2CPacket decode(RegistryFriendlyByteBuf buf) {
            CompoundTag tag = buf.readNbt();
            return new CLCStatusHudSyncS2CPacket(tag == null ? new CompoundTag() : tag);
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buf, CLCStatusHudSyncS2CPacket value) {
            buf.writeNbt(value.snapshot());
        }
    };

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

