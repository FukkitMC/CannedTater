/**
 * Automatically generated file, changes will be lost.
 */
package org.bukkit.craftbukkit.block.impl;

public final class CraftRespawnAnchor extends org.bukkit.craftbukkit.block.data.CraftBlockData implements org.bukkit.block.data.type.RespawnAnchor {

    public CraftRespawnAnchor() {
        super();
    }

    public CraftRespawnAnchor(net.minecraft.block.BlockState state) {
        super(state);
    }

    // org.bukkit.craftbukkit.block.data.type.CraftRespawnAnchor

    private static final net.minecraft.state.property.IntProperty CHARGES = getInteger(net.minecraft.block.RespawnAnchorBlock.class, "charges");

    @Override
    public int getCharges() {
        return get(CHARGES);
    }

    @Override
    public void setCharges(int charges) {
        set(CHARGES, charges);
    }

    @Override
    public int getMaximumCharges() {
        return getMax(CHARGES);
    }
}
