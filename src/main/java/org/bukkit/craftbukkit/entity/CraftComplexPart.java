package org.bukkit.craftbukkit.entity;

import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.boss.dragon.EnderDragonPart;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.ComplexEntityPart;
import org.bukkit.entity.ComplexLivingEntity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.EntityDamageEvent;

public class CraftComplexPart extends CraftEntity implements ComplexEntityPart {
    public CraftComplexPart(CraftServer server, EnderDragonPart entity) {
        super(server, entity);
    }

    @Override
    public ComplexLivingEntity getParent() {
        return (ComplexLivingEntity) ((EnderDragonEntity) getHandle().owner).getBukkitEntity();
    }

    @Override
    public void setLastDamageCause(EntityDamageEvent cause) {
        getParent().setLastDamageCause(cause);
    }

    @Override
    public EntityDamageEvent getLastDamageCause() {
        return getParent().getLastDamageCause();
    }

    @Override
    public boolean isValid() {
        return getParent().isValid();
    }

    @Override
    public EnderDragonPart getHandle() {
        return (EnderDragonPart) entity;
    }

    @Override
    public String toString() {
        return "CraftComplexPart";
    }

    @Override
    public EntityType getType() {
        return EntityType.UNKNOWN;
    }
}
