# Anvil Rework

An anvil-focused Fabric mod for Minecraft 26.2. It changes anvil costs and removes the prior-work penalty.

## Changes

- Removes the prior-work penalty from anvil operations.
- Makes renaming and durability repairs free when no enchantments are applied or upgraded.
- Sets the cost of combining two enchanted books to 1 level.
- Charges 1 level for each enchantment that is applied or upgraded. Enchantments that do not appear on the result are not charged.
- Removes the normal 40-level anvil cost limit.
- Stops anvils from taking durability damage through normal anvil use. Falling anvils can still be damaged.

When an operation also applies or upgrades enchantments, its cost is based on those enchantments even if the same operation repairs or renames the item. The count is per enchantment changed, not per level gained.

Enchanting-table behavior is unchanged.

## Installation

Install Fabric Loader and Fabric API for Minecraft 26.2. Put the mod jar in the `mods` folder on the server. The anvil calculations are server-authoritative, so the mod can be server-only; installing it on clients as well gives matching client-side predictions.

## Build and run

Requirements: Java 25, Fabric Loader 0.19.5 or later, and Minecraft 26.2.

On Windows, build the mod with:

```powershell
.\gradlew.bat build
```

The jar is created in `build/libs/`. To launch the development client:

```powershell
.\gradlew.bat runClient
```

## License

This project is available under the CC0 license.
