package org.bukkit.craftbukkit.entity;

import com.google.common.base.Preconditions;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.mob.ZombieVillagerEntity;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Zombie;

public class CraftZombie extends CraftMonster implements Zombie {

    public CraftZombie(CraftServer server, ZombieEntity entity) {
        super(server, entity);
    }

    @Override
    public ZombieEntity getHandle() {
        return (ZombieEntity) entity;
    }

    @Override
    public String toString() {
        return "CraftZombie";
    }

    @Override
    public EntityType getType() {
        return EntityType.ZOMBIE;
    }

    @Override
    public boolean isBaby() {
        return getHandle().isBaby();
    }

    @Override
    public void setBaby(boolean flag) {
        getHandle().a(flag);
    }

    @Override
    public boolean isVillager() {
        return getHandle() instanceof ZombieVillagerEntity;
    }

    @Override
    public void setVillager(boolean flag) {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public void setVillagerProfession(Villager.Profession profession) {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public Villager.Profession getVillagerProfession() {
        return null;
    }

    @Override
    public boolean isConverting() {
        return getHandle().isConvertingInWater();
    }

    @Override
    public int getConversionTime() {
        Preconditions.checkState(isConverting(), "Entity not converting");

        return getHandle().ticksUntilWaterConversion;
    }

    @Override
    public void setConversionTime(int time) {
        if (time < 0) {
            getHandle().ticksUntilWaterConversion = -1;
            getHandle().getDataTracker().set(ZombieEntity.CONVERTING_IN_WATER, false);
        } else {
            getHandle().setTicksUntilWaterConversion(time);
        }
    }

    @Override
    public int getAge() {
        return getHandle().isBaby() ? -1 : 0;
    }

    @Override
    public void setAge(int i) {
        getHandle().a(i < 0);
    }

    @Override
    public void setAgeLock(boolean b) {
    }

    @Override
    public boolean getAgeLock() {
        return false;
    }

    @Override
    public void setBaby() {
        getHandle().a(true);
    }

    @Override
    public void setAdult() {
        getHandle().a(false);
    }

    @Override
    public boolean isAdult() {
        return !getHandle().isBaby();
    }

    @Override
    public boolean canBreed() {
        return false;
    }

    @Override
    public void setBreed(boolean b) {
    }
}
