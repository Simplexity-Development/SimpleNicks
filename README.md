# Simple Nicks

A standalone nickname plugin that supports [MiniMessage](https://docs.advntr.dev/minimessage/)

MiniMessage comes prepackaged with Paper, and all forks of Paper support MiniMessage.

This plugin now uses **MySQL** or **SQLite** for saving nicknames, allowing faster indexing and improved nickname protection.


## Features

- Set and reset nicknames with MiniMessage formatting.
- Save nicknames for later use.
- SQL-based storage (MySQL or SQLite).
- Nickname protection system:
  - Protect nicknames of players who are online
  - Protect nicknames of players who are offline
  - Protect usernames of players from being used as nicknames
- Configurable options for if nickname functionality requires permissions

## Commands

### Player Commands

`/nick set <nickname>`
- Sets your nickname using MiniMessage tags (`<gray>`, `<gradient:red:blue>`, etc.).

`/nick save [nickname]`
- Saves your current nickname for later use by just doing `/nick save`
- You can also provide a nickname to save directly

`/nick delete <nickname>`
- Deletes one of your saved nicknames.

`/nick reset`
- Resets your nickname to your original username.

`/nick who <nickname>`
- Displays the username of the player with the supplied nickname.

`/nick help`
- Displays help text.

### Admin Commands

`/nick admin set <username> <nickname>`
- Sets another player’s nickname, using the **admin’s permissions** for formatting.

`/nick admin reset <username>`
- Resets another player’s nickname.

`/nick admin delete <username> <nickname>`
- Deletes a saved nickname from a player’s list.

`/nick admin lookup <username>`
- Displays a player’s current nickname and all saved nicknames.

`/nick reload`
- Reloads the configuration and locale.

## Permissions

### Player Permissions

| Permission               | Description                                      | Default |
|--------------------------|--------------------------------------------------|---------|
| `simplenick.nick.set`    | Set your own nickname.                           | `op`    |
| `simplenick.nick.reset`  | Reset your own nickname.                         | `op`    |
| `simplenick.nick.delete` | Delete one of your saved nicknames.              | `op`    |
| `simplenick.nick.who`    | Use `/nick who <nickname>` to look up usernames. | `true`  |

**Color Permissions**

| Permission                  | Description                                  | Default |
|-----------------------------|----------------------------------------------|---------|
| `simplenick.color.basic`    | Use color tags (`<red>`, `<color:#aabbcc>`). | `op`    |
| `simplenick.color.gradient` | Use gradient tags (`<gradient:red:blue>`).   | `op`    |
| `simplenick.color.rainbow`  | Use `<rainbow>`.                             | `op`    |
| `simplenick.color.reset`    | Use `<reset>`.                               | `op`    |

**Format Permissions**

| Permission                        | Description           | Default |
|-----------------------------------|-----------------------|---------|
| `simplenick.format.bold`          | Use `<b>`/`</b>`.     | `op`    |
| `simplenick.format.italic`        | Use `<i>`/`</i>`.     | `op`    |
| `simplenick.format.underline`     | Use `<u>`/`</u>`.     | `op`    |
| `simplenick.format.strikethrough` | Use `<st>`/`</st>`.   | `op`    |
| `simplenick.format.obfuscated`    | Use `<obf>`/`</obf>`. | `op`    |

### Admin Permissions

| Permission                | Description                                    | Default |
|---------------------------|------------------------------------------------|---------|
| `simplenick.admin.set`    | Set another player’s nickname.                 | `op`    |
| `simplenick.admin.reset`  | Reset another player’s nickname.               | `op`    |
| `simplenick.admin.delete` | Delete a saved nickname from another player.   | `op`    |
| `simplenick.admin.lookup` | Lookup a player’s current and saved nicknames. | `op`    |
| `simplenick.reload`       | Reload plugin configuration and locale.        | `op`    |

### Bypass Permissions

| Permission                          | Description                                          | Default |
|-------------------------------------|------------------------------------------------------|---------|
| `simplenick.bypass.username`        | Use another player’s username as a nickname.         | `false` |
| `simplenick.bypass.length`          | Set nicknames longer than the configured max length. | `false` |
| `simplenick.bypass.regex`           | Set nicknames that don’t match the configured regex. | `false` |
| `simplenick.bypass.nick-protection` | Bypass nickname protection (online/offline/expires). | `false` |

## PlaceholderAPI

All placeholders now start with **`simplenick`** for consistency.

| Placeholder                      | Description                                            |
|----------------------------------|--------------------------------------------------------|
| `%simplenick_nickname%`          | Player’s parsed nickname.                              |
| `%simplenick_mininick%`          | Player’s raw MiniMessage nickname (`<red>Name</red>`). |
| `%simplenick_prefixed-nickname%` | Nickname with configured prefix applied.               |
| `%simplenick_prefixed-mininick%` | Raw MiniMessage nickname with prefix.                  |

---

## Configuration

### MySQL
If you're using MySQL you know what to put here. Plugin uses SQLite by default, so you will need to enable MySQL if you are going to use it

### Nickname Length
This is the max length that someone's nickname can be *after* the colors and formats have been figured out. So, if you're doing a nickname like `<gradient:red:green:white:yellow>ThisIs</gradient><gradient:blue:white:green>AConvoluted</gradient><rainbow>Exam</color><b>ple</b>` the text that will be checked will be `ThisIsAConvolutedExample`. It's not recommended to set the value below 3, as there may be unintended side effects.

### Nickname Regex
This is the allowed characters in a nickname. The default allows only alphanumeric characters and underscores `[A-Za-z0-9_]+` - you can add additional characters into this, use a resource like https://regexr.com/ to make sure your regex properly parses

### Require Permission

These set whether or not specific functionalities require a permission. If you don't know what a permissions plugin is, and you just want the plugin to work, you can set the functions you want to 'false'. If you do know what permissions are and how to use them, you probably don't need this section.

**Nick**
Gives functionality for:
- `/nick set`
- `/nick reset`
- `/nick save`
  Allows a player to change the name they appear as.

**Color**
Gives functionality for color tags, like `<red>`, `<#aabbcc>`, `<rainbow>`, `<gradient:color1:color2>`, and `<reset>`
Allows a player to change the name they appear as, and the color of their name.

**Format**
Gives functionality for formatting tags, like `<u>` (underline), `<i>` (*italic*), `<b>` (**bold**), `<st>` (~~strikethrough~~), and `<obf>` (ō̵̬b̵̧̛f̷̩̋u̵̳͂ş̸̓c̷̥̈́a̵͇͂t̸̯͋e̸͎̚d̷̟͝)

**Who**
Gives access to `/nick who <name>` which shows which username is associated with a specific nickname.

### Nickname Protection

Allows protecting nicknames that are already in use. This does not protect saved nicknames or protect nicknames from being saved.

**Online Protection**
Only prevents a player from taking the nickname of someone who is currently online. Allows people to use nicknames of people who are offline.

**Offline Protection**
Prevents players from using nicknames that someone else is actively using, regardless of if they're online or offline, provided the nickname expiry has not been reached.

**Offline Protection Expires**
Time in days someone needs to be away before their nickname protection expires, set to `-1` if you do not want nickname protection to ever expire, but I wouldn't recommend that.

**Username Protection**
Prevents players from nicknaming themselves someone's username. Only checks against players who have actually logged into the server.

**Username Protection Expires**
This value controls the number of days someone needs to be offline before their username is considered allowed to be used as a nickname. Use `-1` if you do not want the protection ever to expire

### Max Saves
Controls how many nicknames players can save, there is not currently an override permission for this, it is applied for all players with save permissions

### Tablist Nick
Alters the tab list to use the current nicknames of players. If you have any plugin that already can handle this, I would suggest you use that, or use my other plugin [Simple Custom Tab List](https://modrinth.com/plugin/simple-custom-tablist) because this option is very limited and prone to issues with other plugins. If you have a very simple server setup and you just want nicknames to show on tab list this will work, but if you have geyser, or other plugins that might alter scoreboard and other random stuff, you're gonna want a separate plugin to handle the tablist

### Nickname Prefix
Sets what the prefix of nicknames should be, used in placeholder `%simplenick_prefixed-nickname%` and `%simplenick_prefixed-mininick%`, as well as the display name of the player.
### `config.yml`

```yaml
mysql:  
  enabled: false  
  ip: "localhost:3306"  
  name: simplenicks  
  username: username1  
  password: badpassword!  
  
# The max amount of characters a nickname should be, not including formatting  
# (so a name like <red>Billy<yellow>Bob would only count 'BillyBob' - and would be 8 characters)  
# Setting this number to any number below "3" could cause unintended side effects  
max-nickname-length: 30  
  
# The regex of valid final nickname characters.  
# Be warned that putting non-alphanumeric characters may result in issues with other plugins and other unintended side effects  
nickname-regex: "[A-Za-z0-9_]+"  
  
# What should require permission set by a permission plugin?  
require-permission:  
  nick: true  
  color: true  
  format: true  
  who: false  
  
# Blocks certain names from being used as nicknames  
# Expiration times are in days, setting to -1 will make it so it never expires  
nickname-protection:  
  username:  
    enabled: true  
    expires: 30  
  online:  
    enabled: false  
  offline:  
    enabled: false  
    expires: 30  
  
# How many nicknames can be saved?  
max-saves: 5  
  
# Should names be changed in tablist?  
# (Keep this false if you use any other tablist plugin, there are placeholder API placeholders to use on those)  
tablist-nick: false  
  
# What prefix should be given for players who have a nickname? put "" if you want no prefix  
nickname-prefix: ""  
  
# Development option, this will flood your logs, discouraged from enabling unless asked  
debug-mode: false
```

---

## Migration from Older Versions

If you are upgrading from a version of SimpleNicks **before SQL support**:

- **Data Storage:**
  - Old versions stored nicknames in YML or using the PersistentDataContainer (PDC).
  - New versions use **MySQL or SQLite**. Existing data will be migrated automatically.
- **Commands:**
  - Admin commands now live under `/nick admin` instead of sharing player command structure.
  - `/nick save` can now accept arguments (`/nick save <nickname>`).
- **Permissions:**
  - Color and format permissions were moved into their own nodes (`simplenick.color.*` and `simplenick.format.*`).
    - Color and format can now be allowed without permissions, check new config options
  - `simplenick.admin.restrictive`, `simplenick.admin.basic` and `simplenick.admin.full` were **removed.** The permission is now `simplenick.admin.set`
    - Admin nickname setting always uses **admin’s own permissions** now. (Previously 'admin basic' functionality)
  - `simplenick.admin.save` was **removed** - since this is now using brigadier, you can use
- **PlaceholderAPI:**
  - Placeholders were renamed for consistency.
    - `%simplenicks_mininick%` → `%simplenick_mininick%`
    - `%simplenicks_nickname%` → `%simplenick_nickname%`
    - New prefixed variants: `%simplenick_prefixed-nickname%`, `%simplenick_prefixed-mininick%`.
    - Previously the placeholder always included the prefix if you had one set, now only the prefixed variants will. This is useful if you don't want the prefix in some areas but want it in others.


## TODO

[ ] - Add a nickname blacklist
