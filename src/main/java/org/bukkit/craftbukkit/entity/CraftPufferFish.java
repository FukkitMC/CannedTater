package org.bukkit.craftbukkit.entity;

import net.minecraft.entity.passive.PufferfishEntity;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.PufferFish;

public class CraftPufferFish extends CraftFish implements PufferFish {

    public CraftPufferFish(CraftServer server, PufferfishEntity entity) {
        super(server, entity);
    }

    @Override
    public PufferfishEntity getHandle() {
        return (PufferfishEntity) super.getHandle();
    }

    @Override
    public int getPuffState() {
        return getHandle().getPuffState();
    }

    @Override
    public void setPuffState(int state) {
        getHandle().setPuffState(state);
    }

    @Override
    public String toString() {
        return "CraftPufferFish";
    }

    @Override
    public EntityType getType() {
        return EntityType.PUFFERFISH;
    }
}
