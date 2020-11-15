/**
 * Automatically generated file, changes will be lost.
 */
package org.bukkit.craftbukkit.block.impl;

public final class CraftScaffolding extends org.bukkit.craftbukkit.block.data.CraftBlockData implements org.bukkit.block.data.type.Scaffolding, org.bukkit.block.data.Waterlogged {

    public CraftScaffolding() {
        super();
    }

    public CraftScaffolding(net.minecraft.block.BlockState state) {
        super(state);
    }

    // org.bukkit.craftbukkit.block.data.type.CraftScaffolding

    private static final net.minecraft.state.property.BooleanProperty BOTTOM = getBoolean(net.minecraft.block.ScaffoldingBlock.class, "bottom");
    private static final net.minecraft.state.property.IntProperty DISTANCE = getInteger(net.minecraft.block.ScaffoldingBlock.class, "distance");

    @Override
    public boolean isBottom() {
        return get(BOTTOM);
    }

    @Override
    public void setBottom(boolean bottom) {
        set(BOTTOM, bottom);
    }

    @Override
    public int getDistance() {
        return get(DISTANCE);
    }

    @Override
    public void setDistance(int distance) {
        set(DISTANCE, distance);
    }

    @Override
    public int getMaximumDistance() {
        return getMax(DISTANCE);
    }

    // org.bukkit.craftbukkit.block.data.CraftWaterlogged

    private static final net.minecraft.state.property.BooleanProperty WATERLOGGED = getBoolean(net.minecraft.block.ScaffoldingBlock.class, "waterlogged");

    @Override
    public boolean isWaterlogged() {
        return get(WATERLOGGED);
    }

    @Override
    public void setWaterlogged(boolean waterlogged) {
        set(WATERLOGGED, waterlogged);
    }
}
