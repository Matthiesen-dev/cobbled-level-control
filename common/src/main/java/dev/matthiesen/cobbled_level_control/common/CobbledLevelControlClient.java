package dev.matthiesen.cobbled_level_control.common;

import dev.matthiesen.cobbled_level_control.common.client.HudClientState;
import dev.matthiesen.cobbled_level_control.common.config.CLCConfig;
import dev.matthiesen.matthiesen_core.common.api.client.keybinds.KeybindMapping;
import dev.matthiesen.matthiesen_core.common.api.events.PlatformClientEvents;
import dev.matthiesen.matthiesen_core.common.api.platform.loader.ModConfigType;
import dev.matthiesen.matthiesen_core.common.AbstractCommonClientMod;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public final class CobbledLevelControlClient extends AbstractCommonClientMod {
    public static final CobbledLevelControlClient INSTANCE = new CobbledLevelControlClient();
    private static final ResourceLocation HUD_LAYER_ID = ResourceLocation.fromNamespaceAndPath(CobbledLevelControl.MOD_ID, "player_status_hud");
    private static final String KEY_CATEGORY = "key.categories.cobbled_level_control";
    private static final String KEY_TOGGLE = "key.cobbled_level_control.toggle_hud";

    public CobbledLevelControlClient() {
        super(CobbledLevelControl.INSTANCE);
    }

    @Override
    public void initialize() {
        registerModConfig(CobbledLevelControl.MOD_ID, ModConfigType.CLIENT, CLCConfig.CLIENT_SPEC, "cobbled_level_control/client.toml");

        PlatformClientEvents.registerHudLayer(HUD_LAYER_ID, this::renderHudLayer);

        var toggleHudKey = new KeyMapping(KEY_TOGGLE, GLFW.GLFW_KEY_H, KEY_CATEGORY);
        getKeybindingsManager().registerKeybind("toggle_hud", new KeybindMapping() {
            @Override
            public KeyMapping getKeybind() {
                return toggleHudKey;
            }

            @Override
            public void onClientTick() {
                if (!toggleHudKey.consumeClick()) {
                    return;
                }
                boolean nextEnabled = !CLCConfig.CLIENT_CONFIG.hud_enabled.get();
                CLCConfig.CLIENT_CONFIG.hud_enabled.set(nextEnabled);
                CLCConfig.CLIENT_CONFIG.hud_enabled.save();

                var player = Minecraft.getInstance().player;
                if (player != null) {
                    Component msg = Component.translatable(nextEnabled
                            ? "cobbled_level_control.hud.enabled"
                            : "cobbled_level_control.hud.disabled");
                    player.displayClientMessage(msg, true);
                }
            }
        });
    }

    private void renderHudLayer(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        if (!CLCConfig.CLIENT_CONFIG.hud_enabled.get()) {
            return;
        }

        CompoundTag snapshot = HudClientState.getSnapshot();
        if (snapshot == null) {
            return;
        }

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.options.hideGui) {
            return;
        }

        List<Component> lines = CLCConfig.CLIENT_CONFIG.isDetailedMode()
                ? buildDetailedLines(snapshot)
                : buildCompactLines(snapshot);

        if (lines.isEmpty()) {
            return;
        }

        double guiScale = mc.getWindow().getGuiScale();
        int offsetX = (int) Math.round(CLCConfig.CLIENT_CONFIG.hud_offsetX.get() * guiScale);
        int offsetY = (int) Math.round(CLCConfig.CLIENT_CONFIG.hud_offsetY.get() * guiScale);

        int x = offsetX;
        int y = guiGraphics.guiHeight() - offsetY - ((lines.size() - 1) * 10);

        for (Component line : lines) {
            guiGraphics.drawString(mc.font, line, x, y, 0xFFFFFF, true);
            y += 10;
        }
    }

    private List<Component> buildCompactLines(CompoundTag snapshot) {
        List<Component> lines = new ArrayList<>();

        int catchingTier = snapshot.getInt("catchingTier");
        int catchingCap = snapshot.getInt("catchingCap");
        int levelingTier = snapshot.getInt("levelingTier");
        int levelingCap = snapshot.getInt("levelingCap");

        MutableComponent main = Component.literal("CLC ")
                .withStyle(ChatFormatting.GOLD)
                .append(Component.literal("C:" + catchingTier + " (" + catchingCap + ")").withStyle(ChatFormatting.AQUA))
                .append(Component.literal("  L:" + levelingTier + " (" + levelingCap + ")").withStyle(ChatFormatting.GREEN));
        lines.add(main);

        if (CLCConfig.CLIENT_CONFIG.hud_showWarningLine.get()) {
            String warning = snapshot.getString("warning");
            if (!warning.isEmpty()) {
                lines.add(Component.literal(warning).withStyle(ChatFormatting.YELLOW));
            }
        }
        return lines;
    }

    private List<Component> buildDetailedLines(CompoundTag snapshot) {
        List<Component> lines = new ArrayList<>();
        lines.add(Component.translatable("cobbled_level_control.hud.title").withStyle(ChatFormatting.GOLD));

        lines.add(Component.translatable(
                "cobbled_level_control.hud.catching",
                snapshot.getInt("catchingTier"),
                snapshot.getInt("catchingCap"),
                snapshot.getInt("catchingNextCap")
        ).withStyle(ChatFormatting.AQUA));

        lines.add(Component.translatable(
                "cobbled_level_control.hud.leveling",
                snapshot.getInt("levelingTier"),
                snapshot.getInt("levelingCap"),
                snapshot.getInt("levelingNextCap")
        ).withStyle(ChatFormatting.GREEN));

        lines.add(Component.translatable(
                "cobbled_level_control.hud.restrictions",
                humanFlag(snapshot.getBoolean("restrictBattles")),
                humanFlag(snapshot.getBoolean("restrictCatching")),
                humanFlag(snapshot.getBoolean("restrictLeveling"))
        ).withStyle(ChatFormatting.GRAY));

        lines.add(Component.translatable(
                "cobbled_level_control.hud.status",
                humanFlag(snapshot.getBoolean("hasPermissionLocks")),
                humanFlag(snapshot.getBoolean("capExceeded")),
                humanFlag(snapshot.getBoolean("dataAvailable"))
        ).withStyle(ChatFormatting.GRAY));

        if (CLCConfig.CLIENT_CONFIG.hud_showWarningLine.get()) {
            String warning = snapshot.getString("warning");
            if (!warning.isEmpty()) {
                lines.add(Component.literal(warning).withStyle(ChatFormatting.YELLOW));
            }
        }

        return lines;
    }

    private Component humanFlag(boolean enabled) {
        return enabled
                ? Component.translatable("cobbled_level_control.hud.yes")
                : Component.translatable("cobbled_level_control.hud.no");
    }
}
