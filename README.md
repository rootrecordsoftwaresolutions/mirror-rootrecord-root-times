# Root-Times

Minecraft-day clock, play/AFK/activity harvest, timezone peaks, join welcome, and local status web for RootMC.

| Field | Value |
|-------|-------|
| **Folder / artifact** | `root-times` |
| **Version** | `1.7.2` |
| **Bukkit name** | `Root-Times` |
| **Paper API** | `26.1` |
| **Author** | RootMC |
| **Website** | https://rootmc.net/plugins/root-times/ |
| **Main class** | `com.rootrecord.minecraft.roottimes.RootTimesPlugin` |

## Install

1. Install **[Root-Core](https://github.com/RootRecord/root-core)** first (license/cloud spine for the suite).
2. Download `root-times-1.7.2.jar` from [Releases](https://github.com/RootRecord/root-times/releases) or the [plugin catalog](https://rootmc.net/plugins/).
3. Remove any older `root-times-*.jar` from `plugins/`.
4. Drop the new jar into `plugins/` and restart (or use Root-Core suite updater when this plugin is on the public manifest).
5. Shared config and secrets live under `plugins/RootMC/` (not a per-plugin data folder unless documented otherwise).

### Dependencies

| Type | Plugins |
|------|---------|
| Hard depend | _none_ |
| Soft depend | Root-Core, PlaceholderAPI |

## Configuration

Most RootMC plugins store operator YAML under `plugins/RootMC/`. After first boot, check that folder for new keys. Never commit live `cloud.yml` / database passwords to git.

## Build (monorepo)

Primary compilation is the RootMC Gradle workspace (not this standalone repo alone):

```bat
cd "D:\.1 Work Stations\RootMC\Plugin Building\Minecraft"
.\build-with-server-jdk.bat :plugins:root-times:jar
```

This repository mirrors sources for GitHub browsing and release distribution. It depends on `rootrecord-common` inside the monorepo.

## Commands (summary)

| Command | Description |
|---------|-------------|
| `/times` | Root-Times status and admin |
| `/time` | New day countdown, uptime, sessions, top playtime, clocks |
| `/afk` | Toggle AFK |
| `/timezone` | Server busiest window in your local time, plus players per timezone |
| `/rootactivity` | Admin reload for Root-Activity (absorbed into Times) |

Full command and permission tables: [docs/COMMANDS.md](docs/COMMANDS.md).

## Links

| Resource | URL |
|----------|-----|
| Website | https://rootmc.net |
| Plugin catalog | https://rootmc.net/plugins/ |
| This plugin page | https://rootmc.net/plugins/root-times/ |
| Suite wiki | https://rootmc.net/wiki/plugins/ |
| Player wiki | https://rootmc.net/wiki/player/ |
| Constitution | https://rootmc.net/wiki/constitution/ |
| Economy guide | https://rootmc.net/wiki/economy/ |
| Developer keys | https://rootmc.net/developer/keys/ |
| Manifest | https://rootmc.net/plugins/manifest.json |
| Play | `play.rootmc.net` |
| Live map | https://map.rootmc.net |
| API | https://api.rootmc.net |
| Discord | https://discord.gg/rFFQYrNaqS |
| GitHub (this repo) | https://github.com/RootRecord/root-times |
| Releases | https://github.com/RootRecord/root-times/releases |

**Discord:** RootMC community - join for support, announcements, and governance: https://discord.gg/rFFQYrNaqS


## Documentation in this repo

- [docs/COMMANDS.md](docs/COMMANDS.md) - commands and permissions from `plugin.yml`
- [docs/LINKS.md](docs/LINKS.md) - canonical RootMC web and Discord links
- [CHANGELOG.md](CHANGELOG.md) - version history seed

## License

Copyright Root Record. All rights reserved. Source is published for transparency; no license to copy, modify, or redistribute is granted unless Root Record provides written permission.

