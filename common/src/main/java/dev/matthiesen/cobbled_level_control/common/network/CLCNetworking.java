package dev.matthiesen.cobbled_level_control.common.network;

import dev.matthiesen.matthiesen_core.common.api.platform.services.CommonLoaderNetworking;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.util.ServiceLoader;
import java.util.function.BiConsumer;

public final class CLCNetworking {
    private static final CommonLoaderNetworking NETWORKING = ServiceLoader.load(CommonLoaderNetworking.class)
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("Unable to locate CommonLoaderNetworking service"));

    private CLCNetworking() {}

    public static <T extends CustomPacketPayload> void registerOptionalS2C(
            CustomPacketPayload.Type<T> type,
            StreamCodec<RegistryFriendlyByteBuf, T> codec,
            BiConsumer<T, dev.matthiesen.matthiesen_core.common.core.network.PacketContext> handler
    ) {
        NETWORKING.registerOptionalS2C(type, codec, handler);
    }

    public static void sendToPlayer(ServerPlayer player, CustomPacketPayload payload) {
        NETWORKING.sendToPlayer(player, payload);
    }

    public static boolean canSendToPlayer(ServerPlayer player, ResourceLocation channelId) {
        return NETWORKING.canSendToPlayer(player, channelId);
    }
}

