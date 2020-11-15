package org.bukkit.craftbukkit.entity;

import com.google.common.base.Preconditions;
import net.minecraft.entity.mob.SpellcastingIllagerEntity;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.Spellcaster;

public class CraftSpellcaster extends CraftIllager implements Spellcaster {

    public CraftSpellcaster(CraftServer server, SpellcastingIllagerEntity entity) {
        super(server, entity);
    }

    @Override
    public SpellcastingIllagerEntity getHandle() {
        return (SpellcastingIllagerEntity) super.getHandle();
    }

    @Override
    public String toString() {
        return "CraftSpellcaster";
    }

    @Override
    public Spell getSpell() {
        return toBukkitSpell(getHandle().getSpell());
    }

    @Override
    public void setSpell(Spell spell) {
        Preconditions.checkArgument(spell != null, "Use Spell.NONE");

        getHandle().setSpell(toNMSSpell(spell));
    }

    public static Spell toBukkitSpell(SpellcastingIllagerEntity.Spell spell) {
        return Spell.valueOf(spell.name());
    }

    public static SpellcastingIllagerEntity.Spell toNMSSpell(Spell spell) {
        return SpellcastingIllagerEntity.Spell.a(spell.ordinal());
    }
}
