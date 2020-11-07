package org.bukkit.craftbukkit.entity;

import net.minecraft.entity.mob.IllusionerEntity;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Illusioner;

public class CraftIllusioner extends CraftSpellcaster implements Illusioner {

    public CraftIllusioner(CraftServer server, IllusionerEntity entity) {
        super(server, entity);
    }

    @Override
    public IllusionerEntity getHandle() {
        return (IllusionerEntity) super.getHandle();
    }

    @Override
    public String toString() {
        return "CraftIllusioner";
    }

    @Override
    public EntityType getType() {
        return EntityType.ILLUSIONER;
    }
}
