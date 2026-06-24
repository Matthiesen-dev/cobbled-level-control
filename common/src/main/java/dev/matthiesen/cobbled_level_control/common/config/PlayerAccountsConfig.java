package dev.matthiesen.cobbled_level_control.common.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import dev.matthiesen.cobbled_level_control.common.runtime.PlayerAccountRecord;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class PlayerAccountsConfig {
    @SerializedName("accounts")
    public Map<UUID, PlayerAccountRecord> accounts = new HashMap<>();

    @SuppressWarnings("unused")
    public static final Gson GSON = new GsonBuilder()
            .disableHtmlEscaping()
            .setPrettyPrinting()
            .create();
}
