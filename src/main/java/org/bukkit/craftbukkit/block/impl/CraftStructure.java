/**
 * Automatically generated file, changes will be lost.
 */
package org.bukkit.craftbukkit.block.impl;

public final class CraftStructure extends org.bukkit.craftbukkit.block.data.CraftBlockData implements org.bukkit.block.data.type.StructureBlock {

    public CraftStructure() {
        super();
    }

    public CraftStructure(net.minecraft.block.BlockState state) {
        super(state);
    }

    // org.bukkit.craftbukkit.block.data.type.CraftStructureBlock

    private static final net.minecraft.state.property.EnumProperty<?> MODE = getEnum(net.minecraft.block.StructureBlock.class, "mode");

    @Override
    public Mode getMode() {
        return get(MODE, Mode.class);
    }

    @Override
    public void setMode(Mode mode) {
        set(MODE, mode);
    }
}
