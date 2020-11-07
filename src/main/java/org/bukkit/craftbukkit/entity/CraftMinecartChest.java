package org.bukkit.craftbukkit.entity;

import net.minecraft.entity.vehicle.ChestMinecartEntity;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.inventory.CraftInventory;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.minecart.StorageMinecart;
import org.bukkit.inventory.Inventory;

@SuppressWarnings("deprecation")
public class CraftMinecartChest extends CraftMinecartContainer implements StorageMinecart {
    private final CraftInventory inventory;

    public CraftMinecartChest(CraftServer server, ChestMinecartEntity entity) {
        super(server, entity);
        inventory = new CraftInventory(entity);
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    @Override
    public String toString() {
        return "CraftMinecartChest{" + "inventory=" + inventory + '}';
    }

    @Override
    public EntityType getType() {
        return EntityType.MINECART_CHEST;
    }
}
