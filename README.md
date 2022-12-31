# Partial Keepinventory
A fabric mod for changing the behaviour of keep inventory. \

Take this fancy list of things I'm working on.

| Feature                         | State                    |
|---------------------------------|--------------------------|
| Custom formulas for droprates   | Working, still worked on |
| Per-item configurable droprates | Planned                  |
| Custom formulas for experience  | Planned, work started    |
 
---

## Commands

Enable/disable the mod
> /pki [enable|disable]

Get info about the mod
> /pki info

Set the drop behaviour
> /pki inv mode [vanilla / static / rarity / custom]


Set droprate for the inventory/rarities (0-100)
> /pki inv droprate [static / common / uncommon / rare / epic] <percentage>

List players who will get the normal keepinventory behaviour
> /pki savedPlayers list

Remove/add players who get normal keepinventory behaviour
> /pki savedPlayers [add / remove] <name>

Set the custom droprate formula
> /pki inv expression set [expression]

Get information on variables for custom formulas
> /pki inv expression help

---

### Expression presets:

#### Distance falloff
Change DISTANCE_IN_BLOCKS to the distance from spawn where you stop dropping items (the droprate decreases linearly).
> max(0.0, min(1-(spawnDistance/DISTANCE_IN_BLOCKS), 1.0))

---

## Experience Droprate
There's currently 2 variables that change how XP is dropped:
- Loss percentage (How much experience is lost) 
- Drop percentage (How much of that experience is dropped)

> /pki xp droprate [drop / loss] <percentage>

There's also 2 "modes" in which these variables can operate.
- Based on levels
- Based on experience points

Levels take increasingly more experience to get, which makes that mode increasingly more punishing when you're on a higher level.
> /pki xp mode [vanilla / static-level / static-points]


---
[License](https://github.com/atiedebee/partial-keepinventory/blob/master/LICENSE)