package org.bukkit.craftbukkit.inventory;

import net.minecraft.inventory.Inventory;
import org.bukkit.Location;
import org.bukkit.inventory.SmithingInventory;

public class CraftInventorySmithing extends CraftResultInventory implements SmithingInventory {

    private final Location location;

    public CraftInventorySmithing(Location location, Inventory inventory, Inventory resultInventory) {
        super(inventory, resultInventory);
        this.location = location;
    }

    @Override
    public Location getLocation() {
        return location;
    }
}
