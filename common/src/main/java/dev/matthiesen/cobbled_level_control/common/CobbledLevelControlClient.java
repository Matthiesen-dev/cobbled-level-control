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
    private static final int LINE_HEIGHT = 10;
    private static final int BOX_PADDING_X = 6;
    private static final int BOX_PADDING_Y = 4;
    private static final int BOX_ACCENT_WIDTH = 3;
    private static final int BOX_BACKGROUND_COLOR = 0xAA101018;
    private static final int BOX_BORDER_COLOR = 0xCC3A3F4A;
    private static final int BOX_ACCENT_COLOR = 0xFF4AA3FF;

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

        int contentHeight = lines.size() * LINE_HEIGHT;
        int boxHeight = contentHeight + (BOX_PADDING_Y * 2);
        int maxLineWidth = 0;
        for (Component line : lines) {
            maxLineWidth = Math.max(maxLineWidth, mc.font.width(line));
        }
        int boxWidth = maxLineWidth + (BOX_PADDING_X * 2) + BOX_ACCENT_WIDTH;
        int topY = guiGraphics.guiHeight() - offsetY - boxHeight;

        // Draw a styled panel inspired by modern status widgets.
        guiGraphics.fill(offsetX, topY, offsetX + boxWidth, topY + boxHeight, BOX_BACKGROUND_COLOR);
        guiGraphics.fill(offsetX, topY, offsetX + boxWidth, topY + 1, BOX_BORDER_COLOR);
        guiGraphics.fill(offsetX, topY + boxHeight - 1, offsetX + boxWidth, topY + boxHeight, BOX_BORDER_COLOR);
        guiGraphics.fill(offsetX, topY, offsetX + 1, topY + boxHeight, BOX_BORDER_COLOR);
        guiGraphics.fill(offsetX + boxWidth - 1, topY, offsetX + boxWidth, topY + boxHeight, BOX_BORDER_COLOR);
        guiGraphics.fill(offsetX, topY, offsetX + BOX_ACCENT_WIDTH, topY + boxHeight, BOX_ACCENT_COLOR);

        int y = topY + BOX_PADDING_Y;
        int textX = offsetX + BOX_ACCENT_WIDTH + BOX_PADDING_X;

        for (Component line : lines) {
            guiGraphics.drawString(mc.font, line, textX, y, 0xFFFFFF, true);
            y += LINE_HEIGHT;
        }
    }

    private List<Component> buildCompactLines(CompoundTag snapshot) {
        List<Component> lines = new ArrayList<>();

        int catchingCap = snapshot.getInt("catchingCap");
        int levelingCap = snapshot.getInt("levelingCap");

        lines.add(Component.translatable("cobbled_level_control.hud.compact.catchingCap", catchingCap)
                .withStyle(ChatFormatting.AQUA));
        lines.add(Component.translatable("cobbled_level_control.hud.compact.levelingCap", levelingCap)
                .withStyle(ChatFormatting.GREEN));

        if (CLCConfig.CLIENT_CONFIG.hud_showWarningLine.get()) {
            Component warning = buildWarningLine(snapshot);
            if (warning != null) {
                lines.add(warning);
            }
        }
        return lines;
    }

    private List<Component> buildDetailedLines(CompoundTag snapshot) {
        List<Component> lines = new ArrayList<>();
        lines.add(Component.translatable("cobbled_level_control.hud.title").withStyle(ChatFormatting.GOLD));
        lines.add(Component.translatable("cobbled_level_control.hud.section.progress").withStyle(ChatFormatting.DARK_AQUA));

        lines.add(Component.translatable("cobbled_level_control.hud.catching",
                snapshot.getInt("catchingTier"),
                snapshot.getInt("catchingCap")
        ).withStyle(ChatFormatting.AQUA));

        lines.add(Component.translatable("cobbled_level_control.hud.leveling",
                snapshot.getInt("levelingTier"),
                snapshot.getInt("levelingCap")
        ).withStyle(ChatFormatting.GREEN));

        lines.add(Component.translatable("cobbled_level_control.hud.section.restrictions").withStyle(ChatFormatting.DARK_AQUA));

        lines.add(buildStateLine(
                "cobbled_level_control.hud.restrictions.battle",
                snapshot.getBoolean("restrictBattles")
        ));

        lines.add(buildStateLine(
                "cobbled_level_control.hud.restrictions.catching",
                snapshot.getBoolean("restrictCatching")
        ));

        lines.add(buildStateLine(
                "cobbled_level_control.hud.restrictions.leveling",
                snapshot.getBoolean("restrictLeveling")
        ));

        lines.add(Component.translatable("cobbled_level_control.hud.section.status").withStyle(ChatFormatting.DARK_AQUA));

        lines.add(buildStateLine(
                "cobbled_level_control.hud.status.permissionLocks",
                snapshot.getBoolean("hasPermissionLocks")
        ));

        lines.add(buildStateLine(
                "cobbled_level_control.hud.status.capExceeded",
                snapshot.getBoolean("capExceeded")
        ));

        lines.add(buildStateLine(
                "cobbled_level_control.hud.status.dataAvailable",
                snapshot.getBoolean("dataAvailable")
        ));

        if (CLCConfig.CLIENT_CONFIG.hud_showWarningLine.get()) {
            Component warning = buildWarningLine(snapshot);
            if (warning != null) {
                lines.add(warning);
            }
        }

        return lines;
    }

    private Component humanFlag(boolean enabled) {
        return enabled
                ? Component.translatable("cobbled_level_control.hud.yes")
                : Component.translatable("cobbled_level_control.hud.no");
    }

    private Component buildStateLine(String labelKey, boolean enabled) {
        MutableComponent line = Component.translatable(labelKey).withStyle(ChatFormatting.GRAY)
                .append(Component.literal(": "));
        line.append(humanFlag(enabled).copy().withStyle(enabled ? ChatFormatting.GREEN : ChatFormatting.RED));
        return line;
    }

    private Component buildWarningLine(CompoundTag snapshot) {
        if (snapshot.getBoolean("hasPermissionLocks")) {
            return Component.translatable("cobbled_level_control.hud.warning.permission").withStyle(ChatFormatting.YELLOW);
        }
        if (snapshot.getBoolean("capExceeded")) {
            return Component.translatable("cobbled_level_control.hud.warning.capExceeded").withStyle(ChatFormatting.YELLOW);
        }
        if (!snapshot.getBoolean("dataAvailable")) {
            return Component.translatable("cobbled_level_control.hud.warning.dataUnavailable").withStyle(ChatFormatting.YELLOW);
        }
        return null;
    }
}
