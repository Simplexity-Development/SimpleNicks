# Simple Nicks

A standalone nickname plugin that supports Mini-Message.

Mini-Message comes prepackaged with Paper and all forks of Paper support Mini-Message.

## Commands

`/nick set <nickname> [player]`

- Sets the nickname of the player.
- Uses Mini-Message Tags such as `<gray>` and `<gradient:red:green>`
- Allows users with permission `simplenick.admin` to change the nickname of another player using the optional [player] argument.

`/nick save [player]`
- Saves the player's current nickname for future use

`/nick delete [player|nick] <nick>`
- Deletes the specified nickname from the player's saved names

`/nick reset [player]`

- Sets the nickname of the player back to the original username, unformatted.
- Allows users with permission `simplenick.admin` to reset the nickname of another player using the optional [player] argument.

`/nick reload` 

- Reloads the configuration and locale.

`/nick help` 

- Provides help text.

### Admin Permissions

- `simplenick.bypass.username`
  - Allows the player to use the username of another player on the server, as their own nickname
- `simplenick.bypass.length`
  - Allows the player to use a nickname longer than the configured max character
- `simplenick.bypass.regex`
  - Allows the player to use a nickname that doesn't match the configured regex
- `simplenick.reload`
  - Allows the player to reload the plugin configuration and locale.
- `simplenick.admin.reset`
  - Allows the player to reset another player's nickname
- `simplenick.admin.save`
  - Allows the player to save another player's nickname
- `simplenick.admin.delete`
  - Allows the player to delete another player's saved name
- `simplenick.admin.restrictive`
  - Allows the player to set another player's nickname, within the other player's formatting permissions
  - Does not override the other player's formatting permissions
  - Does override the other player's nickname permissions 
  - (if the other player cannot nickname themselves, they can still be admin nick'd with this permission)
- `simplenick.admin.basic`
  - Allows the player to set another player's nickname, within their own formatting permissions
  - Overrides the other player's formatting permissions
  - Only allows permissions that the admin has
- `simplenick.admin.full`
  - Allows the player to set another player's nickname, regardless of both of their formatting permissions.

> [!NOTE]
>
> This permission allows for all minimessage formatting types, not just the ones that are set in permissions here
>
> These types of formatting can include things like click action, hover text, and similar. 
>
> These formats can do really cool stuff, but be careful who you give it to lol

### Player Permissions

- `simplenicks.nick.set`
  - Allows the player to set their own nickname

- `simplenicks.nick.reset`
  - Allows the player to reset their own nickname

- `simplenicks.nick.save`
  - Allows saving the current nickname

- `simplenicks.nick.delete`
  - Allows deleting a specified saved nickname

- `simplenicks.nick.<tag>`
  - Allows for the use of those Mini-Message tags.
  - Valid Tags are: `color`, `gradient`, and `rainbow`.

- `simplenick.nick.format.<tag>`
  - Allows for the use of those formatting tags.
  - Valid Tags are: `reset`, `underline`, `italic`, `strikethrough`, `bold`, and `obsfucated`.

## PlaceholderAPI
- `%simplenicks_mininick%`
  - Nickname pre-parsed

## TODO
- Create a `/nick whois` or similar command to determine "who is the player(s) with this nickname".
