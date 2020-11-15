package org.bukkit.craftbukkit.tag;

import net.minecraft.tag.TagGroup;
import net.minecraft.util.Identifier;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;

public abstract class CraftTag<N, B extends Keyed> implements Tag<B> {

    private final net.minecraft.tag.TagGroup<N> registry;
    private final Identifier tag;
    //
    private net.minecraft.tag.Tag<N> handle;

    public CraftTag(TagGroup<N> registry, Identifier tag) {
        this.registry = registry;
        this.tag = tag;
    }

    protected net.minecraft.tag.Tag<N> getHandle() {
        if (handle == null) {
            handle = registry.b(tag);
        }

        return handle;
    }

    @Override
    public NamespacedKey getKey() {
        return CraftNamespacedKey.fromMinecraft(tag);
    }
}
