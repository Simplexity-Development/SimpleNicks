# Simple Nicks

A standalone nickname plugin that supports Mini-Message.

Mini-Message comes prepackaged with Paper and all forks of Paper support Mini-Message.

> **Warning**
> 
> This plugin is currently under development and is not for production use unless retrieved from [releases](https://github.com/ADHDMC/SimpleNicks/releases).

## Commands

`/nick set <nickname> [player]`

- Sets the nickname of the player.
- Supports Mini-Message Tags such as `<gray>` and `<gradient:red:green>`
- Allows admins to change the nickname of another player using the optional [player] argument.

`/nick reset [player]`

- Sets the nickname of the player back to the original username, unformatted.
- Allows admins to reset the nickname of another player using the optional [player] argument.

## Permissions

`simplenicks.admin`

- Admin Permission to change other player's nicknames.