package me.atie.partialKeepinventory.network;

import me.atie.partialKeepinventory.PartialKeepInventory;
import net.minecraft.util.Identifier;

public class Identifiers {
    public static Identifier configUpdatePacket = new Identifier(PartialKeepInventory.ID, "config-update");
    public static Identifier clientVersionPacket = new Identifier(PartialKeepInventory.ID, "send-version");
}
