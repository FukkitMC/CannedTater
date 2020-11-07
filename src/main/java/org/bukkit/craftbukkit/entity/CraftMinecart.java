package org.bukkit.craftbukkit.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.block.data.CraftBlockData;
import org.bukkit.craftbukkit.util.CraftMagicNumbers;
import org.bukkit.entity.Minecart;
import org.bukkit.material.MaterialData;
import org.bukkit.util.Vector;

public abstract class CraftMinecart extends CraftVehicle implements Minecart {
    public CraftMinecart(CraftServer server, AbstractMinecartEntity entity) {
        super(server, entity);
    }

    @Override
    public void setDamage(double damage) {
        getHandle().setDamageWobbleStrength((float) damage);
    }

    @Override
    public double getDamage() {
        return getHandle().getDamageWobbleStrength();
    }

    @Override
    public double getMaxSpeed() {
        return getHandle().maxSpeed;
    }

    @Override
    public void setMaxSpeed(double speed) {
        if (speed >= 0D) {
            getHandle().maxSpeed = speed;
        }
    }

    @Override
    public boolean isSlowWhenEmpty() {
        return getHandle().slowWhenEmpty;
    }

    @Override
    public void setSlowWhenEmpty(boolean slow) {
        getHandle().slowWhenEmpty = slow;
    }

    @Override
    public Vector getFlyingVelocityMod() {
        return getHandle().getFlyingVelocityMod();
    }

    @Override
    public void setFlyingVelocityMod(Vector flying) {
        getHandle().setFlyingVelocityMod(flying);
    }

    @Override
    public Vector getDerailedVelocityMod() {
        return getHandle().getDerailedVelocityMod();
    }

    @Override
    public void setDerailedVelocityMod(Vector derailed) {
        getHandle().setDerailedVelocityMod(derailed);
    }

    @Override
    public AbstractMinecartEntity getHandle() {
        return (AbstractMinecartEntity) entity;
    }

    @Override
    public void setDisplayBlock(MaterialData material) {
        if (material != null) {
            BlockState block = CraftMagicNumbers.getBlock(material);
            this.getHandle().setCustomBlock(block);
        } else {
            // Set block to air (default) and set the flag to not have a display block.
            this.getHandle().setCustomBlock(Blocks.AIR.getDefaultState());
            this.getHandle().setCustomBlockPresent(false);
        }
    }

    @Override
    public void setDisplayBlockData(BlockData blockData) {
        if (blockData != null) {
            BlockState block = ((CraftBlockData) blockData).getState();
            this.getHandle().setCustomBlock(block);
        } else {
            // Set block to air (default) and set the flag to not have a display block.
            this.getHandle().setCustomBlock(Blocks.AIR.getDefaultState());
            this.getHandle().setCustomBlockPresent(false);
        }
    }

    @Override
    public MaterialData getDisplayBlock() {
        BlockState blockData = getHandle().getContainedBlock();
        return CraftMagicNumbers.getMaterial(blockData);
    }

    @Override
    public BlockData getDisplayBlockData() {
        BlockState blockData = getHandle().getContainedBlock();
        return CraftBlockData.fromData(blockData);
    }

    @Override
    public void setDisplayBlockOffset(int offset) {
        getHandle().setCustomBlockOffset(offset);
    }

    @Override
    public int getDisplayBlockOffset() {
        return getHandle().getBlockOffset();
    }
}
