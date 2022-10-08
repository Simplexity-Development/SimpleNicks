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

`/nick reload` (TODO)

- Reloads the configuration and locale.

`/nick help`

- Provides help text.

## Permissions

`simplenick.admin`

- Allows the player to change other player's nicknames.

`simplenicks.nick.<tag>` (TODO)

- Allows for the use of those Mini-Message tags.
- Valid Tags are: `color`, `gradient`, `rainbow`, `underline`, `italic`, `strikethrough`, `bold`, `obsfucated`.
- All Formatting Tags work using `simplenick.nick.format`.

`simplenicks.reload` (TODO)

- Allows the player to reload the plugin configuration and locale.

## TODO

- Provide Config Option for TabList Changing and implement logic for TabList Update.
- Create a `/nick whois` or similar command to determine "who is the player(s) with this nickname".
- Add the option to change the regex check for display names (with a "potentially unsafe" warning).
- Add the option to change the character limit on the fully parsed name.