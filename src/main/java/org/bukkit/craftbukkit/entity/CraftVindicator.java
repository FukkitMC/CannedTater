package org.bukkit.craftbukkit.entity;

import net.minecraft.entity.mob.VindicatorEntity;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Vindicator;

public class CraftVindicator extends CraftIllager implements Vindicator {

    public CraftVindicator(CraftServer server, VindicatorEntity entity) {
        super(server, entity);
    }

    @Override
    public VindicatorEntity getHandle() {
        return (VindicatorEntity) super.getHandle();
    }

    @Override
    public String toString() {
        return "CraftVindicator";
    }

    @Override
    public EntityType getType() {
        return EntityType.VINDICATOR;
    }
}
