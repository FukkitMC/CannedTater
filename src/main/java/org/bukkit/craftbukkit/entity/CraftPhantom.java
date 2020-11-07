package org.bukkit.craftbukkit.entity;

import net.minecraft.entity.mob.PhantomEntity;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Phantom;

public class CraftPhantom extends CraftFlying implements Phantom {

    public CraftPhantom(CraftServer server, PhantomEntity entity) {
        super(server, entity);
    }

    @Override
    public PhantomEntity getHandle() {
        return (PhantomEntity) super.getHandle();
    }

    @Override
    public int getSize() {
        return getHandle().getPhantomSize();
    }

    @Override
    public void setSize(int sz) {
        getHandle().setPhantomSize(sz);
    }

    @Override
    public String toString() {
        return "CraftPhantom";
    }

    @Override
    public EntityType getType() {
        return EntityType.PHANTOM;
    }
}
