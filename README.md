<div align="center">
  <table>
    <tr>
      <td valign="middle">
        <img
          width="120"
          height="120"
          alt=""
          src="https://github.com/user-attachments/assets/52f8139b-754f-4e9a-821a-fd09bb6b57cc"
        />
      </td>
      <td valign="middle">
        <h2>SimpleNicks</h2>
        A standalone nickname plugin so you don't need to get a plugin with tons of other stuff.<br>
        Just want nicknames? Go with <b>SimpleNicks</b>!
        <div style="line-height:20px;">&nbsp;</div>
      </td>
    </tr>
  </table>
</div>

## Features

<table>
  <tr>
    <td valign="middle">
      <strong>Set Nicknames With <a href="https://docs.advntr.dev/minimessage/">MiniMessage</a></strong><br>
      Use MiniMessage formatting in nicknames.
    </td>
    <td>
      <img width="430" height="117" alt="" src="https://github.com/user-attachments/assets/cbb35336-8cc6-4297-9c45-d160a342a1db" />
    </td>
  </tr>
  <tr>
    <td valign="middle">
      <strong>Allow Nicknames in Tab List</strong><br>
      Display player nicknames in the tab list.
    </td>
    <td>
      <img width="210" height="45" alt="" src="https://github.com/user-attachments/assets/91a8af30-6224-47b7-a70a-f5fd9bc9f594" />
    </td>
  </tr>
  <tr>
    <td valign="middle">
      <strong>Save Nicknames</strong><br>
      Nicknames can be saved for future use, like in roleplaying.
    </td>
    <td>
      <img width="599" height="137" alt="" src="https://github.com/user-attachments/assets/594d2bd2-777c-436c-a41b-6c1647b0f7b6" />
    </td>
  </tr>
  <tr>
    <td valign="middle">
      <strong>Nickname Protection</strong><br>
      Prevent confusion with username / nickname copying.
    </td>
    <td>
      <img width="665" height="119" alt="" src="https://github.com/user-attachments/assets/e78e8615-f173-4adf-8160-b726e48fb5e7" />
    </td>
  </tr>
  <tr>
    <td valign="middle">
      <strong>"Who Is That?"</strong><br>
      Find out a player's secret identity.
    </td>
    <td>
      <img width="367" height="155" alt="" src="https://github.com/user-attachments/assets/f93ad1a5-ffcc-4bbf-9562-aa223385fd57" />
    </td>
  </tr>
</table>

#### And More

- Permissions support, use something like LuckPerms.
- SQL-based storage, choose between MySQL (Server) or SQLite (Local, default).
    - _No, you do not need any SQL setup to run this plugin._
- Control whether specific nickname functionality requires permissions.
- Control how long a username or nickname is protected for.
- Nickname prefixing, so you know who is hiding their identity.
- PlaceholderAPI expansion, so you can put the nicknames wherever you need to.
- MiniPlaceholders expansion, for Adventure-native placeholder support.

## Commands

### Player Commands

| Command                   | Description                                                                                     |
|---------------------------|-------------------------------------------------------------------------------------------------|
| `/nick help`              | Displays help text.                                                                             |
| `/nick set <nickname>`    | Sets your nickname.<br>Supports [MiniMessage](https://docs.advntr.dev/minimessage/) formatting. |
| `/nick save [nickname]`   | Saves your current nickname for later use.<br>You can also provide a nickname to save directly. |
| `/nick delete <nickname>` | Deletes one of your saved nicknames.                                                            |
| `/nick reset`             | Resets your nickname to your original username.                                                 |
| `/nick who <nickname>`    | Displays the username of the player with the supplied nickname.                                 |

### Admin Commands

| Command                                    | Description                                                                         |
|--------------------------------------------|-------------------------------------------------------------------------------------|
| `/nick admin set <username> <nickname>`    | Sets another player’s nickname.<br>Uses the **admin’s permissions** for formatting. |
| `/nick admin reset <username>`             | Resets another player’s nickname.                                                   |
| `/nick admin delete <username> <nickname>` | Deletes a saved nickname from a player’s list.                                      |
| `/nick admin lookup <username>`            | Displays a player’s current nickname and all saved nicknames.                       |
| `/nick reload`                             | Reloads the configuration and locale.                                               |

## Permissions

### Player Permissions

| Permission               | Description                                      | Default |
|--------------------------|--------------------------------------------------|---------|
| `simplenick.nick.set`    | Set your own nickname.                           | `op`    |
| `simplenick.nick.reset`  | Reset your own nickname.                         | `op`    |
| `simplenick.nick.delete` | Delete one of your saved nicknames.              | `op`    |
| `simplenick.nick.who`    | Use `/nick who <nickname>` to look up usernames. | `true`  |
| `simplenick.nick.help`   | Use `/nick help` to view the help description    | `true`  |

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

| Placeholder                      | Description                                                      | Example                                                                                                                           |
|----------------------------------|------------------------------------------------------------------|-----------------------------------------------------------------------------------------------------------------------------------|
| `%simplenick_nickname%`          | Player’s parsed nickname.                                        | <img width="167" height="20" alt="" src="https://github.com/user-attachments/assets/08cee7cf-8cbd-4ee9-9453-e7db16ec1b17" />      |
| `%simplenick_mininick%`          | Player’s raw MiniMessage nickname.                               | <img width="264" height="21" alt="" src="https://github.com/user-attachments/assets/350ed8a8-da73-4c10-8332-cff65d1c3f05" />      |
| `%simplenick_stripped%`          | Player's tagless and formatless nickname.                        | <img width="156" height="16" alt="image" src="https://github.com/user-attachments/assets/f5844049-ca20-4194-93c5-268e7b1dda16" /> |
| `%simplenick_prefixed-nickname%` | Nickname with configured prefix applied.                         | <img width="167" height="20" alt="" src="https://github.com/user-attachments/assets/bb591810-9c60-42fd-9005-0d68b1408f3e" />      |
| `%simplenick_prefixed-mininick%` | Raw MiniMessage nickname with prefix.                            | <img width="264" height="21" alt="" src="https://github.com/user-attachments/assets/70785e7c-94e7-4bcc-9128-a7db9293554b" />      |
| `%simplenick_prefixed-stripped%` | Player's tagless and formatless nickname with prefix.            | <img width="163" height="20" alt="image" src="https://github.com/user-attachments/assets/467f4b8d-3488-464e-a42e-b5bbeb2426ba" /> |
| `%simplenick_normalized%`        | Player's normalized nickname, used for "name taken" comparisons. | <img width="152" height="16" alt="image" src="https://github.com/user-attachments/assets/5ca78e29-e971-41e8-bbe2-8fc67fc06f23" /> |

## MiniPlaceholders

[MiniPlaceholders](https://github.com/MiniPlaceholders/MiniPlaceholders) is an Adventure-native placeholder library.
Unlike PlaceholderAPI, these tags return fully rendered Adventure components, so colors and formatting are preserved
without any legacy serialization step.

All tags are audience-scoped and only resolve for **online players**.

| Tag                              | Description                                                           |
|----------------------------------|-----------------------------------------------------------------------|
| `<simplenick_nick>`              | Player's nickname rendered as a full Adventure component.             |
| `<simplenick_prefixed_nick>`     | Configured prefix + rendered nickname component.                      |
| `<simplenick_stripped>`          | Player's nickname with all MiniMessage tags stripped.                 |
| `<simplenick_prefixed_stripped>` | Configured prefix + stripped nickname.                                |
| `<simplenick_normalized>`        | Normalized nickname (lowercase, tags stripped). Empty if no nick set. |

When no nickname is set, `nick`, `prefixed_nick`, `stripped`, and `prefixed_stripped` fall back to the player's
username.

## Configuration

### MySQL

Plugin uses a local .db SQLite by default. This requires no additional setup but cannot be shared between a network of
servers.

If you would like to use MySQL, you can do so by enabling it and filling out the information in the configuration.

```yaml
mysql:
  enabled: false
  ip: "localhost:3306"
  name: simplenicks
  username: username1
  password: badpassword!
```

### Nickname Length

This is the max length that someone's nickname can be *after* the colors and formats have been figured out.

So, if you're doing a nickname like

&nbsp;&nbsp;&nbsp;&nbsp;`<blue>ThisIs</blue><green>AConvoluted</green><rainbow>Exam</rainbow><b>ple</b>`

the text that will be checked will be `ThisIsAConvolutedExample`.

> [!Warning]
> It's not recommended to set the value below 3, as there may be unintended side effects.

```yaml
# The max amount of characters a nickname should be, not including formatting
# (so a name like <red>Billy<yellow>Bob would only count 'BillyBob' - and would be 8 characters)
# Setting this number to any number below "3" could cause unintended side effects
max-nickname-length: 30
```

### Nickname Regex

This is the allowed characters in a nickname. The default allows only alphanumeric characters and underscores
`[A-Za-z0-9_]+` - you can add additional characters into this, use a resource like https://regexr.com/ to make sure your
regex properly parses.

> [!Warning]
> Using non-alphanumeric characters is generally unsupported and can have unintended side effects.

```yaml
# The regex of valid final nickname characters.
# Be warned that putting non-alphanumeric characters may result in issues with other plugins and other unintended side effects
nickname-regex: "[A-Za-z0-9_]+"
```

### Require Permission

These set whether or not specific functionalities require a permission. If you don't know what a permissions plugin is,
and you just want the plugin to work, you can set the functions you want to 'false'. If you do know what permissions are
and how to use them, you probably don't need this section.

#### Nick

Gives functionality for:

- `/nick set`
- `/nick reset`
- `/nick save`
  Allows a player to change the name they appear as.

#### Color

Gives functionality for color tags, like `<red>`, `<#aabbcc>`, `<rainbow>`, `<gradient:color1:color2>`, and `<reset>`
Allows a player to change the name they appear as, and the color of their name.

#### Format

Gives functionality for formatting tags, like `<u>` (underline), `<i>` (*italic*), `<b>` (**bold**), `<st>` (~~
strikethrough~~), and `<obf>` (ō̵̬b̵̧̛f̷̩̋u̵̳͂ş̸̓c̷̥̈́a̵͇͂t̸̯͋e̸͎̚d̷̟͝)

#### Who

Gives access to `/nick who <name>` which shows which username is associated with a specific nickname.

```yaml
# What should require permission set by a permission plugin?
require-permission:
  nick: true
  color: true
  format: true
  who: false
```

### Nickname Protection

Allows protecting nicknames that are already in use. This does not protect saved nicknames or protect nicknames from
being saved.

#### Online Protection

Only prevents a player from taking the nickname of someone who is currently online. Allows people to use nicknames of
people who are offline.

#### Offline Protection

Prevents players from using nicknames that someone else is actively using, regardless of if they're online or offline,
provided the nickname expiry has not been reached.

#### Offline Protection Expires

Time in days someone needs to be away before their nickname protection expires, set to `-1` if you do not want nickname
protection to ever expire, but I wouldn't recommend that.

#### Username Protection

Prevents players from nicknaming themselves someone's username. Only checks against players who have actually logged
into the server.

#### Username Protection Expires

This value controls the number of days someone needs to be offline before their username is considered allowed to be
used as a nickname. Use `-1` if you do not want the protection ever to expire

```yaml
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
```

### Max Saves

Controls how many nicknames players can save, there is not currently an override permission for this, it is applied for
all players with save permissions

```yaml
# How many nicknames can be saved?
max-saves: 5
```

### Tablist Nick

Alters the tab list to use the current nicknames of players. If you have any plugin that already can handle this, I
would suggest you use that, or use my other
plugin [Simple Custom Tab List](https://modrinth.com/plugin/simple-custom-tablist) because this option is very limited
and prone to issues with other plugins. If you have a very simple server setup and you just want nicknames to show on
tab list this will work, but if you have geyser, or other plugins that might alter scoreboard and other random stuff,
you're gonna want a separate plugin to handle the tablist

```yaml
# Should names be changed in tablist?
# (Keep this false if you use any other tablist plugin, there are placeholder API placeholders to use on those)
tablist-nick: false
```

### Nickname Prefix

Sets what the prefix of nicknames should be.

Used in the display name of the player and placeholders `%simplenick_prefixed-nickname%`,
`%simplenick_prefixed-mininick%`, `<simplenick_prefixed_nick>`, and `<simplenick_prefixed_stripped>`.

```yaml
# What prefix should be given for players who have a nickname? put "" if you want no prefix
nickname-prefix: ""
```

### Debug Mode

Enables development debug logging for this plugin.

> [!Warning]
> This will flood your logs and can make it virtually unreadable.
>
> Do not enable unless advised to.

```yaml
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
    - `simplenick.admin.restrictive`, `simplenick.admin.basic` and `simplenick.admin.full` were **removed.** The
      permission is now `simplenick.admin.set`
        - Admin nickname setting always uses **admin’s own permissions** now. (Previously 'admin basic' functionality)
    - `simplenick.admin.save` was **removed** - since this is now using brigadier, you can use
- **PlaceholderAPI:**
    - Placeholders were renamed for consistency.
        - `%simplenicks_mininick%` → `%simplenick_mininick%`
        - `%simplenicks_nickname%` → `%simplenick_nickname%`
        - New prefixed variants: `%simplenick_prefixed-nickname%`, `%simplenick_prefixed-mininick%`.
        - Previously the placeholder always included the prefix if you had one set, now only the prefixed variants will.
          This is useful if you don't want the prefix in some areas but want it in others.
