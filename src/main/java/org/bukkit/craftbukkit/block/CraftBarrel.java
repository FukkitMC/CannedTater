package org.bukkit.craftbukkit.block;

import net.minecraft.block.BarrelBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BarrelBlockEntity;
import net.minecraft.sound.SoundEvents;
import org.bukkit.Material;
import org.bukkit.block.Barrel;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.inventory.CraftInventory;
import org.bukkit.inventory.Inventory;

public class CraftBarrel extends CraftLootable<BarrelBlockEntity> implements Barrel {

    public CraftBarrel(Block block) {
        super(block, BarrelBlockEntity.class);
    }

    public CraftBarrel(Material material, BarrelBlockEntity te) {
        super(material, te);
    }

    @Override
    public Inventory getSnapshotInventory() {
        return new CraftInventory(this.getSnapshot());
    }

    @Override
    public Inventory getInventory() {
        if (!this.isPlaced()) {
            return this.getSnapshotInventory();
        }

        return new CraftInventory(this.getTileEntity());
    }

    @Override
    public void open() {
        requirePlaced();
        if (!getTileEntity().opened) {
            BlockState blockData = getTileEntity().getCachedState();
            boolean open = blockData.get(BarrelBlock.OPEN);

            if (!open) {
                getTileEntity().setOpen(blockData, true);
                getTileEntity().playSound(blockData, SoundEvents.BLOCK_BARREL_OPEN);
            }
        }
        getTileEntity().opened = true;
    }

    @Override
    public void close() {
        requirePlaced();
        if (getTileEntity().opened) {
            BlockState blockData = getTileEntity().getCachedState();
            getTileEntity().setOpen(blockData, false);
            getTileEntity().playSound(blockData, SoundEvents.BLOCK_BARREL_CLOSE);
        }
        getTileEntity().opened = false;
    }
}
