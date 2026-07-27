# Commands and permissions

## Commands

| Command | Description | Permission | Usage |
|---------|-------------|------------|-------|
| `/times` | Root-Times status and admin | `roottimes.admin` | `/times [status/reload/web]` |
| `/time` | New day countdown, uptime, sessions, top playtime, clocks | `roottimes.time` | `/time` |
| `/afk` | Toggle AFK | `roottimes.afk` | `/afk` |
| `/timezone` | Server busiest window in your local time, plus players per timezone | `` | `/<command>` |
| `/rootactivity` | Admin reload for Root-Activity (absorbed into Times) | `rootactivity.reload` | `/<command> reload` |

## Permissions

| Permission | Description | Default |
|------------|-------------|---------|
| `roottimes.admin` | Administer Root-Times | `op` |
| `roottimes.time` | View /time clock and playtime summary | `true` |
| `roottimes.afk` | Toggle AFK | `true` |
| `roottimes.bypass` | Bypass idle AFK detection | `false` |
| `rootactivity.use` | View /timezone activity report | `true` |
| `rootactivity.reload` | Reload root-activity.yml | `op` |

