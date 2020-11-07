package org.bukkit.craftbukkit.block;

import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.ShulkerBox;
import org.bukkit.craftbukkit.inventory.CraftInventory;
import org.bukkit.craftbukkit.util.CraftMagicNumbers;
import org.bukkit.inventory.Inventory;

public class CraftShulkerBox extends CraftLootable<ShulkerBoxBlockEntity> implements ShulkerBox {

    public CraftShulkerBox(final Block block) {
        super(block, ShulkerBoxBlockEntity.class);
    }

    public CraftShulkerBox(final Material material, final ShulkerBoxBlockEntity te) {
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
    public DyeColor getColor() {
        net.minecraft.block.Block block = CraftMagicNumbers.getBlock(this.getType());

        return DyeColor.getByWoolData((byte) ((ShulkerBoxBlock) block).color.getId());
    }

    @Override
    public void open() {
        requirePlaced();
        if (!getTileEntity().opened) {
            World world = getTileEntity().getWorld();
            world.addSyncedBlockEvent(getPosition(), getTileEntity().getCachedState().getBlock(), 1, 1);
            world.playSound(null, getPosition(), SoundEvents.BLOCK_SHULKER_BOX_OPEN, SoundCategory.BLOCKS, 0.5F, world.random.nextFloat() * 0.1F + 0.9F);
        }
        getTileEntity().opened = true;
    }

    @Override
    public void close() {
        requirePlaced();
        if (getTileEntity().opened) {
            World world = getTileEntity().getWorld();
            world.addSyncedBlockEvent(getPosition(), getTileEntity().getCachedState().getBlock(), 1, 0);
            world.playSound(null, getPosition(), SoundEvents.BLOCK_SHULKER_BOX_OPEN, SoundCategory.BLOCKS, 0.5F, world.random.nextFloat() * 0.1F + 0.9F);
        }
        getTileEntity().opened = false;
    }
}
