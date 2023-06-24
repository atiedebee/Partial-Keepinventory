# PKI Rules
Rules are a way to customize the drop behaviour of items even more.

## Rules
A rule consists of 3 components:
- An item property
- A comparison
- Another item property

They are to expressions what set theory is to high school math, and may function very similarly to sets.

## Rule Groups
Multiple rules together form a rule group, which will change the droprate of an item if it passes all the checks. 

The basic layout of a rule group will be as follows:
```json
// List of expressions that are evaluated
| isStackable = false
| hasDurability = true
\_> static 0.0 // The resulting droprate 
```
Multiple rule groups can exist at the same time and will be evaluated in sequence.

## Rule Group types
Just having rule groups isn't enough, so let's add modifier rules:
```json
// Double the droprate for items that are at less than half durability
| durability < 50%
\_> * 2.0
// All equipables 
| isWearable = true
\_> destroy 0.5
```
