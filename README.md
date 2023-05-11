# Partial Keepinventory
### <span style="color:red">NOTE: This mod is still in alpha, and I am just one person. Bugs are bound to happen.</span>
A fabric mod for changing the behaviour of keep inventory.

For more information on what I'm planning and working on see [the Roadmap](https://github.com/atiedebee/Partial-Keepinventory/wiki/Roadmap).

 
## GUI
Open the GUI using ``/pki-gui``. This requires you to have the mod installed on the client side.

## Expressions
Expressions are a way to dynamically change how items are dropped. This can be done with a simple formula that uses given variables. The value returned by the invExpression is clamped between 0 and 1.
 A list of variables can be found in the provided table:

### Base

| Variable                             | Meaning                                                                           |
|--------------------------------------|-----------------------------------------------------------------------------------|
| spawnDistance                        | The distance from the player to their spawnpoint                                  |
| spawnX, spawnY, spawnZ               | The player's spawn position                                                       |
| playerX, playerY, playerZ            | The player's position                                                             |

### Inventory

| Variable                             | Meaning                                                                           |
|--------------------------------------|-----------------------------------------------------------------------------------|
| isCommon, isUncommon, isRare, isEpic | Whether an item has a certain rarity, These are 1.0 when true and 0.0 when false. |
| rarityPercent                        | The configured droprate of the item's rarity.                                     |
| dropPercent                          | The static inventory droprate                                                     |

### Experience

| Variable    | Meaning                   |
|-------------|---------------------------|
| xpPoints    | Player's XP point amount. |
| xpLevel     | Player's XP level.        |


### Expression presets:

#### Distance falloff
Change DISTANCE_IN_BLOCKS to the distance from spawn where you stop dropping items (the droprate decreases linearly).
> abs(1-(spawnDistance/DISTANCE_IN_BLOCKS))

---

## Commands

Enable/disable the mod
> /pki [enable|disable]

Set the drop behaviour
> /pki inv mode [vanilla / static / rarity / custom]

Set droprate for the inventory/rarities (0-100)
> /pki inv droprate [static / common / uncommon / rare / epic] <percentage>

List players who will get the normal keepinventory behaviour
> /pki inv savedPlayers list

Remove/add players who get normal keepinventory behaviour
> /pki inv savedPlayers [add / remove] <name>

Set the custom droprate formula
> /pki inv invExpression set [invExpression]

## Experience Droprate
There's currently 2 variables that change how XP is dropped:
- Loss percentage (How much experience is lost) 
- Drop percentage (How much of that experience is dropped)

> /pki xp droprate [drop / loss] <percentage>

There's also 2 "modes" in which these variables can operate.
- Based on levels
- Based on experience points

Levels take increasingly more experience to get, which makes that mode increasingly more punishing when you're on a higher level.
> /pki xp mode [vanilla / static-level / static-points / custom-level / custom-points]


---
[License](https://github.com/atiedebee/partial-keepinventory/blob/master/LICENSE)
