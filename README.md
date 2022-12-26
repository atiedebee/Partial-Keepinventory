# Partial Keepinventory
A fabric mod for changing the behaviour of keep inventory. \
*Requires keepinventory to be ON for this mod to work.*

Take this fancy todo list

| Feature                         | State                    |
|---------------------------------|--------------------------|
| Per player keepinventory        | Done                     |
| Percentage based droprates      | Done                     |
| Rarity based droprates          | Done                     |
| Custom formulas for droprates   | Working, still worked on |
| Per-item configurable droprates | Planned                  |
 

## Commands

Enable/disable the mod
> /partialKeepinventory [enable|disable]

Get info about the mod
> /partialKeepinventory info

Set the drop behaviour
> /partialKeepinventory mode [percentage|rarity|custom]


Set droprate for the inventory/rarities (0-100)
> /partialKeepinventory droprate [inventory|common|uncommon|rare|epic] <percentage>

List players who will get the normal keepinventory behaviour
> /partialKeepinventory savedPlayers list

Remove/add players who get normal keepinventory behaviour
> /partialKeepinventory savedPlayers [add|remove] <name>

Set the custom expression
> /partialKeepinventory expression set [expression]

Get information on variables for custom expressions
> /partialKeepinventory expression help

### Expression presets:

#### Distance falloff
Change DISTANCE_IN_BLOCKS to the distance from spawn where you stop dropping items (the droprate decreases linearly).
> max(0.0, min(1-(spawnDistance/DISTANCE_IN_BLOCKS), 1.0))