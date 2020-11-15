package org.bukkit.craftbukkit.tag;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.tag.TagGroup;
import net.minecraft.util.Identifier;
import org.bukkit.Fluid;
import org.bukkit.craftbukkit.util.CraftMagicNumbers;

public class CraftFluidTag extends CraftTag<net.minecraft.fluid.Fluid, Fluid> {

    public CraftFluidTag(TagGroup<net.minecraft.fluid.Fluid> registry, Identifier tag) {
        super(registry, tag);
    }

    @Override
    public boolean isTagged(Fluid fluid) {
        return getHandle().contains(CraftMagicNumbers.getFluid(fluid));
    }

    @Override
    public Set<Fluid> getValues() {
        return Collections.unmodifiableSet(getHandle().values().stream().map(CraftMagicNumbers::getFluid).collect(Collectors.toSet()));
    }
}
