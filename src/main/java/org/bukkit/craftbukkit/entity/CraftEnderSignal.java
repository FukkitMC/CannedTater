package org.bukkit.craftbukkit.entity;

import com.google.common.base.Preconditions;
import net.minecraft.entity.EyeOfEnderEntity;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import org.bukkit.Location;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.entity.EnderSignal;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

public class CraftEnderSignal extends CraftEntity implements EnderSignal {
    public CraftEnderSignal(CraftServer server, EyeOfEnderEntity entity) {
        super(server, entity);
    }

    @Override
    public EyeOfEnderEntity getHandle() {
        return (EyeOfEnderEntity) entity;
    }

    @Override
    public String toString() {
        return "CraftEnderSignal";
    }

    @Override
    public EntityType getType() {
        return EntityType.ENDER_SIGNAL;
    }

    @Override
    public Location getTargetLocation() {
        return new Location(getWorld(), getHandle().targetX, getHandle().targetY, getHandle().targetZ, getHandle().yaw, getHandle().pitch);
    }

    @Override
    public void setTargetLocation(Location location) {
        Preconditions.checkArgument(getWorld().equals(location.getWorld()), "Cannot target EnderSignal across worlds");
        getHandle().a(new BlockPos(location.getX(), location.getY(), location.getZ()));
    }

    @Override
    public boolean getDropItem() {
        return getHandle().dropsItem;
    }

    @Override
    public void setDropItem(boolean shouldDropItem) {
        getHandle().dropsItem = shouldDropItem;
    }

    @Override
    public ItemStack getItem() {
        return CraftItemStack.asBukkitCopy(getHandle().g());
    }

    @Override
    public void setItem(ItemStack item) {
        getHandle().setItem(item != null ? CraftItemStack.asNMSCopy(item) : Items.ENDER_EYE.getDefaultStack());
    }

    @Override
    public int getDespawnTimer() {
        return getHandle().lifespan;
    }

    @Override
    public void setDespawnTimer(int time) {
        getHandle().lifespan = time;
    }
}
