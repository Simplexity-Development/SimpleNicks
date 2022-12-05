# Simple Nicks

A standalone nickname plugin that supports Mini-Message.

Mini-Message comes prepackaged with Paper and all forks of Paper support Mini-Message.

> **Warning**
> 
> This plugin is currently under development and is not for production use unless retrieved from [releases](https://github.com/ADHDMC/SimpleNicks/releases).

## Commands

`/nick set <nickname> [player]`

- Sets the nickname of the player.
- Uses Mini-Message Tags such as `<gray>` and `<gradient:red:green>`
- Allows users with permission `simplenick.admin` to change the nickname of another player using the optional [player] argument.

`/nick reset [player]`

- Sets the nickname of the player back to the original username, unformatted.
- Allows users with permission `simplenick.admin` to reset the nickname of another player using the optional [player] argument.

`/nick reload` 

- Reloads the configuration and locale.

`/nick help` (TODO)

- Provides help text.

### Admin Permissions

`simplenick.admin`

- Allows the player to change other player's nicknames.

`simplenick.usernamebypass`

- Allows the player to use the username of another player on the server, as their own nickname

`simplenicks.reload`

- Allows the player to reload the plugin configuration and locale.

### Player Permissions

`simplenicks.nick.set`

- Allows the player to set their own nickname

`simplenicks.nick.reset`

- Allows the player to reset their own nickname

`simplenicks.nick.<tag>`

- Allows for the use of those Mini-Message tags.
- Valid Tags are: `color`, `gradient`, and `rainbow`. 

`simplenick.nick.format.<tag>`

- Allows for the use of those formatting tags.
- Valid Tags are: `underline`, `italic`, `strikethrough`, `bold`, and `obsfucated`.

## TODO

- Provide Config Option for TabList Changing and implement logic for TabList Update.
- Create a `/nick whois` or similar command to determine "who is the player(s) with this nickname".
- Add the option to change the regex check for display names (with a "potentially unsafe" warning).
- Add the option to change the character limit on the fully parsed name.