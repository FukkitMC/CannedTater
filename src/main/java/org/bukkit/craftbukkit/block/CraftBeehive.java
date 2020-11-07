package org.bukkit.craftbukkit.block;

import com.google.common.base.Preconditions;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.entity.BeehiveBlockEntity;
import net.minecraft.block.entity.BeehiveBlockEntity.BeeState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Beehive;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.entity.CraftBee;
import org.bukkit.entity.Bee;

public class CraftBeehive extends CraftBlockEntityState<BeehiveBlockEntity> implements Beehive {

    public CraftBeehive(final Block block) {
        super(block, BeehiveBlockEntity.class);
    }

    public CraftBeehive(final Material material, final BeehiveBlockEntity te) {
        super(material, te);
    }

    @Override
    public Location getFlower() {
        BlockPos flower = getSnapshot().flowerPos;
        return (flower == null) ? null : new Location(getWorld(), flower.getX(), flower.getY(), flower.getZ());
    }

    @Override
    public void setFlower(Location location) {
        Preconditions.checkArgument(location == null || this.getWorld().equals(location.getWorld()), "Flower must be in same world");
        getSnapshot().flowerPos = (location == null) ? null : new BlockPos(location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    @Override
    public boolean isFull() {
        return getSnapshot().isFullOfBees();
    }

    @Override
    public boolean isSedated() {
        return isPlaced() && getTileEntity().isSmoked();
    }

    @Override
    public int getEntityCount() {
        return getSnapshot().getBeeCount();
    }

    @Override
    public int getMaxEntities() {
        return getSnapshot().maxBees;
    }

    @Override
    public void setMaxEntities(int max) {
        Preconditions.checkArgument(max > 0, "Max bees must be more than 0");

        getSnapshot().maxBees = max;
    }

    @Override
    public List<Bee> releaseEntities() {
        List<Bee> bees = new ArrayList<>();

        if (isPlaced()) {
            BeehiveBlockEntity beehive = ((BeehiveBlockEntity) this.getTileEntityFromWorld());
            for (Entity bee : beehive.releaseBees(this.getHandle(), BeeState.BEE_RELEASED, true)) {
                bees.add((Bee) bee.getBukkitEntity());
            }
        }

        return bees;
    }

    @Override
    public void addEntity(Bee entity) {
        Preconditions.checkArgument(entity != null, "Entity must not be null");

        getSnapshot().tryEnterHive(((CraftBee) entity).getHandle(), false);
    }
}
