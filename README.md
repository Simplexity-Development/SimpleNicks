# Simple Nicks

A standalone nickname plugin that supports Mini-Message.

Mini-Message comes prepackaged with Paper and all forks of Paper support Mini-Message.

## Commands



`/nick set <nickname> [player]`

- Sets the nickname of the player.
- Uses Mini-Message Tags such as `<gray>` and `<gradient:red:green>`
- Allows users with permission `simplenick.admin` to change the nickname of another player using the optional [player]
  argument.

`/nick save [player]`

- Saves the player's current nickname for future use

`/nick delete [player|nick] <nick>`

- Deletes the specified nickname from the player's saved names

`/nick reset [player]`

- Sets the nickname of the player back to the original username, unformatted.
- Allows users with permission `simplenick.admin` to reset the nickname of another player using the optional [player]
  argument.

`/nick reload`

- Reloads the configuration and locale.

`/nick help`

- Provides help text.

### Admin Permissions

| Permission                     | Admin permission to:                                                                                                                                                             |
|:-------------------------------|:---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `simplenick.bypass.`           | ░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░                                                                                                                              |
| `simplenick.bypass.username`   | Use another player's username as their own nickname on the server                                                                                                                |
| `simplenick.bypass.length`     | Set a nickname longer than the max character limit                                                                                                                               |
| `simplenick.bypass.regex`      | Set a nickname that doesn't match the configured regex                                                                                                                           |
| ▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓                | ▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓                                                                                                                              |
| `simplenick.reload`            | Reload the plugin configuration and locale                                                                                                                                       |
| ▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓                | ▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓                                                                                                                              |
| `simplenick.admin.`            | ░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░                                                                                                                              |
| `simplenick.admin.reset`       | Reset a target player's nickname                                                                                                                                                 |
| `simplenick.admin.save`        | Save a target player's nickname to **the target player's** saved nicknames                                                                                                       |
| `simplenick.admin.delete`      | Delete a target player's saved name from **the target player's** <br>list of saved nicknames                                                                                     |
| `simplenick.admin.restrictive` | Set a target player's nickname, only using the formats the target player has permissions for<br/>(This is a very niche permission, likely not what most servers are looking for) |
| `simplenick.admin.basic`       | Set a target player's nickname, using the formats the Admin has permission to use<br>(This is the permission most servers will need for admins)                                  |
| `simplenick.admin.full`        | Set a target player's nickname with zero regard for formatting, full permission for all formatting. <br>(Not recommended)                                                        |

### Player Permissions

| Permission                             | Permission to:                                           |
|:---------------------------------------|:---------------------------------------------------------|
| `simplenick.nick`                      | ░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░      |
| `simplenick.nick.set`                  | Set own nickname                                         |
| `simplenick.nick.reset`                | Reset own nickname                                       |
| `simplenick.nick.save`                 | Save current nickname for later use                      |
| `simplenick.nick.delete`               | Delete a previously saved nickname                       |
| `simplenick.nick.color`                | Use color formatting tags (`<color:#aabbcc>` or `<red>`) |
| `simplenick.nick.gradient`             | Use gradient formatting tags (`<gradient:red:blue>`)     |
| `simplenick.nick.rainbow`              | Use rainbow formatting tags (`<rainbow>`)                |
| `simplenick.nick.format.`              | ░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░      |
| `simplenick.nick.format.reset`         | Use the `<reset>` formatting tags                        |
| `simplenick.nick.format.underline`     | Use the `<u>`,`</u>` formatting tags                     |
| `simplenick.nick.format.strikethrough` | Use the `<st>`,`</st>` formatting tags                   |
| `simplenick.nick.format.bold`          | Use the `<b>`,`</b>` formatting tags                     |
| `simplenick.nick.format.obsfucated`    | Use the `<obf>`,`</obf>` formatting tags                 |


## PlaceholderAPI

- Download the `player` ecloud expansion for Placeholder API
- Use `%player_displayname%`

- If you have a specific setup that needs the minimessage formatting before it has been parsed, i.e. the
  `"<red>Nickname</red>"` version, you can use `%simplenicks_mininick%`
    - This won't be necessary for most users though, the previous one will work best.

## TODO

- Create a `/nick whois` or similar command to determine "who is the player(s) with this nickname".
