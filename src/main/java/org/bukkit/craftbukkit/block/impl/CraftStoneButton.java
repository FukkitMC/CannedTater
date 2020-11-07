/**
 * Automatically generated file, changes will be lost.
 */
package org.bukkit.craftbukkit.block.impl;

public final class CraftStoneButton extends org.bukkit.craftbukkit.block.data.CraftBlockData implements org.bukkit.block.data.type.Switch, org.bukkit.block.data.Directional, org.bukkit.block.data.FaceAttachable, org.bukkit.block.data.Powerable {

    public CraftStoneButton() {
        super();
    }

    public CraftStoneButton(net.minecraft.block.BlockState state) {
        super(state);
    }

    // org.bukkit.craftbukkit.block.data.type.CraftSwitch

    private static final net.minecraft.state.property.EnumProperty<?> FACE = getEnum(net.minecraft.block.StoneButtonBlock.class, "face");

    @Override
    public Face getFace() {
        return get(FACE, Face.class);
    }

    @Override
    public void setFace(Face face) {
        set(FACE, face);
    }

    // org.bukkit.craftbukkit.block.data.CraftDirectional

    private static final net.minecraft.state.property.EnumProperty<?> FACING = getEnum(net.minecraft.block.StoneButtonBlock.class, "facing");

    @Override
    public org.bukkit.block.BlockFace getFacing() {
        return get(FACING, org.bukkit.block.BlockFace.class);
    }

    @Override
    public void setFacing(org.bukkit.block.BlockFace facing) {
        set(FACING, facing);
    }

    @Override
    public java.util.Set<org.bukkit.block.BlockFace> getFaces() {
        return getValues(FACING, org.bukkit.block.BlockFace.class);
    }

    // org.bukkit.craftbukkit.block.data.CraftFaceAttachable

    private static final net.minecraft.state.property.EnumProperty<?> ATTACH_FACE = getEnum(net.minecraft.block.StoneButtonBlock.class, "face");

    @Override
    public AttachedFace getAttachedFace() {
        return get(ATTACH_FACE, AttachedFace.class);
    }

    @Override
    public void setAttachedFace(AttachedFace face) {
        set(ATTACH_FACE, face);
    }

    // org.bukkit.craftbukkit.block.data.CraftPowerable

    private static final net.minecraft.state.property.BooleanProperty POWERED = getBoolean(net.minecraft.block.StoneButtonBlock.class, "powered");

    @Override
    public boolean isPowered() {
        return get(POWERED);
    }

    @Override
    public void setPowered(boolean powered) {
        set(POWERED, powered);
    }
}
