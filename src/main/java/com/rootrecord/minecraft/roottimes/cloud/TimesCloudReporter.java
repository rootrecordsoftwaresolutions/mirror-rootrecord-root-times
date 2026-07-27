package com.rootrecord.minecraft.roottimes.cloud;

import com.rootrecord.minecraft.common.McDayClock;
import com.rootrecord.minecraft.common.config.RootRecordCloudConfig;
import com.rootrecord.minecraft.roottimes.RootTimesPlugin;
import com.rootrecord.minecraft.roottimes.api.McDaySnapshot;
import com.rootrecord.minecraft.roottimes.clock.ClockService;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;

/** Pushes live Times status to api.rootmc.net for per-server /s/{id}/ pages. */
public final class TimesCloudReporter {

    private final RootTimesPlugin plugin;
    private final HttpClient http =
            HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(12)).build();
    private BukkitTask task;
    private boolean loggedMissingCreds;
    private boolean loggedOk;
    private volatile String lastError = "";

    public TimesCloudReporter(RootTimesPlugin plugin) {
        this.plugin = plugin;
    }

    public void start() {
        stop();
        if (!plugin.timesConfig().cloudStatusEnabled()) {
            plugin.getLogger().info("Cloud status reporter disabled (cloud-status.enabled: false)");
            return;
        }
        int seconds = plugin.timesConfig().cloudStatusIntervalSeconds();
        long ticks = Math.max(20L, seconds * 20L);
        // Collect on main thread, POST off-thread
        task = Bukkit.getScheduler().runTaskTimer(plugin, this::tickCollect, ticks, ticks);
        plugin.getLogger().info("Cloud status reporter every " + seconds + "s → /api/rootmc/times/status");
    }

    public void stop() {
        if (task != null) {
            task.cancel();
            task = null;
        }
    }

    private void tickCollect() {
        RootRecordCloudConfig.CloudSettings cloud =
                RootRecordCloudConfig.resolve(plugin, plugin.yamlConfig().config());
        if (!cloud.hasServerCredentials()) {
            if (!loggedMissingCreds) {
                loggedMissingCreds = true;
                plugin.getLogger().warning(
                        "Cloud status skipped — set cloud.server-id / server-secret in plugins/RootMC/cloud.yml");
            }
            return;
        }

        McDaySnapshot snap = ClockService.snapshot();
        List<PlayerSnap> players = new ArrayList<>();
        int afkCount = 0;
        for (Player p : Bukkit.getOnlinePlayers()) {
            boolean afk = plugin.afkService() != null && plugin.afkService().isAfk(p.getUniqueId());
            players.add(new PlayerSnap(p.getName(), afk));
            if (afk) {
                afkCount++;
            }
        }
        int online = Bukkit.getOnlinePlayers().size();
        String pluginsJson = installedPluginsJson();
        String body = buildBody(snap, online, afkCount, players, pluginsJson);
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> post(cloud, body));
    }

    private void post(RootRecordCloudConfig.CloudSettings cloud, String body) {
        try {
            String url = cloud.apiBase() + "/api/rootmc/times/status";
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofSeconds(20))
                    .header("Content-Type", "application/json")
                    .header("X-RootStat-Server-Id", cloud.serverId())
                    .header("X-RootStat-Server-Secret", cloud.serverSecret())
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();
            HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() >= 400) {
                lastError = "HTTP " + response.statusCode();
                plugin.getLogger().warning("Cloud status push failed: " + lastError);
                return;
            }
            if (!loggedOk) {
                loggedOk = true;
                plugin.getLogger().info("Cloud status push OK → " + cloud.apiBase());
            }
            lastError = "";
        } catch (Exception ex) {
            lastError = ex.getMessage() != null ? ex.getMessage() : ex.getClass().getSimpleName();
            plugin.getLogger().log(Level.FINE, "Cloud status push failed: " + lastError, ex);
        }
    }

    private static String buildBody(
            McDaySnapshot snap, int online, int afk, List<PlayerSnap> players, String pluginsJson) {
        StringBuilder body = new StringBuilder(512);
        body.append('{');
        body.append("\"dayId\":").append(snap.dayId());
        body.append(",\"todTicks\":").append(snap.timeOfDayTicks());
        body.append(",\"fullTime\":").append(snap.fullTime());
        body.append(",\"phase\":\"").append(escape(snap.phase())).append('"');
        body.append(",\"lengthMinutes\":").append(snap.lengthMinutes());
        body.append(",\"online\":").append(online);
        body.append(",\"afk\":").append(afk);
        body.append(",\"timezone\":\"").append(escape(McDayClock.zone().getId())).append('"');
        body.append(",\"players\":[");
        for (int i = 0; i < players.size(); i++) {
            if (i > 0) {
                body.append(',');
            }
            PlayerSnap p = players.get(i);
            body.append("{\"name\":\"")
                    .append(escape(p.name()))
                    .append("\",\"afk\":")
                    .append(p.afk())
                    .append('}');
        }
        body.append("],\"plugins\":").append(pluginsJson != null ? pluginsJson : "[]");
        body.append('}');
        return body.toString();
    }

    private static String installedPluginsJson() {
        List<String> parts = new ArrayList<>();
        for (Plugin p : Bukkit.getPluginManager().getPlugins()) {
            String name = p.getName();
            if (name == null) {
                continue;
            }
            String lower = name.toLowerCase(Locale.ROOT);
            if (!(lower.startsWith("root") || lower.equals("rootmc") || lower.equals("roothelp"))) {
                continue;
            }
            String id = toManifestId(name);
            parts.add(
                    "{\"id\":\""
                            + escape(id)
                            + "\",\"version\":\""
                            + escape(p.getDescription().getVersion())
                            + "\"}");
        }
        return "[" + String.join(",", parts) + "]";
    }

    private static String toManifestId(String bukkitName) {
        return switch (bukkitName) {
            case "RootMC" -> "rootmc";
            case "RootMC-Shops" -> "rootmc-shops";
            case "RootHelp" -> "roothelp";
            case "Root-Core" -> "root-core";
            case "Root-Times" -> "root-times";
            case "Root-Perms" -> "root-perms";
            case "RootMC-Official" -> "rootmc-official";
            case "Root-Essentials" -> "root-essentials";
            case "Root-Rewards" -> "root-rewards";
            default -> bukkitName.toLowerCase(Locale.ROOT);
        };
    }

    private static String escape(String s) {
        if (s == null) {
            return "";
        }
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    private record PlayerSnap(String name, boolean afk) {}
}
