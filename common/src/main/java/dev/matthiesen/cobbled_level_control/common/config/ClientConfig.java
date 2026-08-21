package dev.matthiesen.cobbled_level_control.common.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class ClientConfig {
    public ModConfigSpec.BooleanValue hud_enabled;
    public ModConfigSpec.EnumValue<HudMode> hud_mode;
    public ModConfigSpec.BooleanValue hud_showWarningLine;
    public ModConfigSpec.IntValue hud_offsetX;
    public ModConfigSpec.IntValue hud_offsetY;

    public ClientConfig(ModConfigSpec.Builder builder) {
        builder.comment("Client Configuration")
                .translation("cobbled_level_control.configuration.client")
                .push("client");

        builder.comment("HUD Configuration")
                .translation("cobbled_level_control.configuration.client.hud")
                .push("hud");

        hud_enabled = builder.comment("Whether to render the account status HUD")
                .translation("cobbled_level_control.configuration.client.hud.enabled")
                .define("enabled", true);

        hud_mode = builder.comment("HUD display mode: compact or detailed")
                .translation("cobbled_level_control.configuration.client.hud.mode")
                .defineEnum("mode", HudMode.COMPACT);

        hud_showWarningLine = builder.comment("Whether warning messages are shown on the HUD")
                .translation("cobbled_level_control.configuration.client.hud.showWarningLine")
                .define("showWarningLine", true);

        hud_offsetX = builder.comment("Bottom-left X offset in pixels at GUI scale 1")
                .translation("cobbled_level_control.configuration.client.hud.offsetX")
                .defineInRange("offsetX", 6, 0, 500);

        hud_offsetY = builder.comment("Bottom-left Y offset in pixels at GUI scale 1")
                .translation("cobbled_level_control.configuration.client.hud.offsetY")
                .defineInRange("offsetY", 6, 0, 500);

        builder.pop(); // client.hud
        builder.pop(); // client
    }

    public enum HudMode {
        COMPACT,
        DETAILED
    }

    public boolean isDetailedMode() {
        return hud_mode.get() == HudMode.DETAILED;
    }
}

