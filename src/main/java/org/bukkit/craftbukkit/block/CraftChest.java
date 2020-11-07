package org.bukkit.craftbukkit.block;

import net.minecraft.block.Blocks;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.sound.SoundEvents;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.inventory.CraftInventory;
import org.bukkit.craftbukkit.inventory.CraftInventoryDoubleChest;
import org.bukkit.inventory.Inventory;

public class CraftChest extends CraftLootable<ChestBlockEntity> implements Chest {

    public CraftChest(final Block block) {
        super(block, ChestBlockEntity.class);
    }

    public CraftChest(final Material material, final ChestBlockEntity te) {
        super(material, te);
    }

    @Override
    public Inventory getSnapshotInventory() {
        return new CraftInventory(this.getSnapshot());
    }

    @Override
    public Inventory getBlockInventory() {
        if (!this.isPlaced()) {
            return this.getSnapshotInventory();
        }

        return new CraftInventory(this.getTileEntity());
    }

    @Override
    public Inventory getInventory() {
        CraftInventory inventory = (CraftInventory) this.getBlockInventory();
        if (!isPlaced()) {
            return inventory;
        }

        // The logic here is basically identical to the logic in BlockChest.interact
        CraftWorld world = (CraftWorld) this.getWorld();

        ChestBlock blockChest = (ChestBlock) (this.getType() == Material.CHEST ? Blocks.CHEST : Blocks.TRAPPED_CHEST);
        NamedScreenHandlerFactory nms = blockChest.createScreenHandlerFactory(data, world.getHandle(), this.getPosition());

        if (nms instanceof ChestBlock.DoubleInventory) {
            inventory = new CraftInventoryDoubleChest((ChestBlock.DoubleInventory) nms);
        }
        return inventory;
    }

    @Override
    public void open() {
        requirePlaced();
        if (!getTileEntity().opened) {
            net.minecraft.block.Block block = getTileEntity().getCachedState().getBlock();
            getTileEntity().getWorld().addSyncedBlockEvent(getTileEntity().getPos(), block, 1, getTileEntity().viewerCount + 1);
            getTileEntity().playSound(SoundEvents.BLOCK_CHEST_OPEN);
        }
        getTileEntity().opened = true;
    }

    @Override
    public void close() {
        requirePlaced();
        if (getTileEntity().opened) {
            net.minecraft.block.Block block = getTileEntity().getCachedState().getBlock();
            getTileEntity().getWorld().addSyncedBlockEvent(getTileEntity().getPos(), block, 1, 0);
            getTileEntity().playSound(SoundEvents.BLOCK_CHEST_CLOSE);
        }
        getTileEntity().opened = false;
    }
}
