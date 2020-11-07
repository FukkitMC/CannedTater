package org.bukkit.craftbukkit.entity;

import net.minecraft.entity.mob.PiglinEntity;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Piglin;

public class CraftPiglin extends CraftPiglinAbstract implements Piglin {

    public CraftPiglin(CraftServer server, PiglinEntity entity) {
        super(server, entity);
    }

    @Override
    public boolean isAbleToHunt() {
        return getHandle().cannotHunt;
    }

    @Override
    public void setIsAbleToHunt(boolean flag) {
        getHandle().cannotHunt = flag;
    }

    @Override
    public PiglinEntity getHandle() {
        return (PiglinEntity) super.getHandle();
    }

    @Override
    public EntityType getType() {
        return EntityType.PIGLIN;
    }

    @Override
    public String toString() {
        return "CraftPiglin";
    }
}
