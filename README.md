# Simple Nicks

A standalone nickname plugin that supports Mini-Message.

Mini-Message comes prepackaged with Paper and all forks of Paper support Mini-Message.

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

`/nick help` 

- Provides help text.

### Admin Permissions

`simplenick.usernamebypass`

- Allows the player to use the username of another player on the server, as their own nickname

`simplenick.reload`

- Allows the player to reload the plugin configuration and locale.

`simplenick.admin.reset`

- Allows the player to reset another player's nickname

`simplenick.admin.restrictive`

- Allows the player to set another player's nickname, within the other player's formatting permissions
- Does not override the other player's formatting permissions
- Does override the other player's nickname permissions 
  - (if the other player cannot nickname themselves, they can still be admin nick'd with this permission)

`simplenick.admin.basic`

- Allows the player to set another player's nickname, within their own formatting permissions
- Overrides the other player's formatting permissions
- Only allows permissions that the admin has

`simplenick.admin.full`

- Allows the player to set another player's nickname, regardless of both of their formatting permissions.

> [!NOTE]
>
> This permission allows for all minimessage formatting types, not just the ones that are set in permissions here
>
> These types of formatting can include things like click action, hover text, and similar. 
>
> These formats can do really cool stuff, but be careful who you give it to lol

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
- Valid Tags are: `reset`, `underline`, `italic`, `strikethrough`, `bold`, and `obsfucated`.

## TODO

- Provide Config Option for TabList Changing and implement logic for TabList Update.
- Create a `/nick whois` or similar command to determine "who is the player(s) with this nickname".
- Make the code less of a mess
- Allow for nicknames to be set by non-players
- Make a placeholderAPI extension
