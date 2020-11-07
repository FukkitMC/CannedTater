package org.bukkit.craftbukkit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContextParameter;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.craftbukkit.entity.CraftHumanEntity;
import org.bukkit.craftbukkit.inventory.CraftInventory;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.loot.LootContext;

public class CraftLootTable implements org.bukkit.loot.LootTable {

    private final LootTable handle;
    private final NamespacedKey key;

    public CraftLootTable(NamespacedKey key, LootTable handle) {
        this.handle = handle;
        this.key = key;
    }

    public LootTable getHandle() {
        return handle;
    }

    @Override
    public Collection<ItemStack> populateLoot(Random random, LootContext context) {
        net.minecraft.loot.context.LootContext nmsContext = convertContext(context);
        List<net.minecraft.item.ItemStack> nmsItems = handle.generateLoot(nmsContext);
        Collection<ItemStack> bukkit = new ArrayList<>(nmsItems.size());

        for (net.minecraft.item.ItemStack item : nmsItems) {
            if (item.isEmpty()) {
                continue;
            }
            bukkit.add(CraftItemStack.asBukkitCopy(item));
        }

        return bukkit;
    }

    @Override
    public void fillInventory(Inventory inventory, Random random, LootContext context) {
        net.minecraft.loot.context.LootContext nmsContext = convertContext(context);
        CraftInventory craftInventory = (CraftInventory) inventory;
        net.minecraft.inventory.Inventory handle = craftInventory.getInventory();

        // TODO: When events are added, call event here w/ custom reason?
        getHandle().supplyInventory(handle, nmsContext);
    }

    @Override
    public NamespacedKey getKey() {
        return key;
    }

    private net.minecraft.loot.context.LootContext convertContext(LootContext context) {
        Location loc = context.getLocation();
        ServerWorld handle = ((CraftWorld) loc.getWorld()).getHandle();

        net.minecraft.loot.context.LootContext.Builder builder = new net.minecraft.loot.context.LootContext.Builder(handle);
        setMaybe(builder, LootContextParameters.ORIGIN, new Vec3d(loc.getX(), loc.getY(), loc.getZ()));
        if (getHandle() != LootTable.EMPTY) {
            // builder.luck(context.getLuck());

            if (context.getLootedEntity() != null) {
                Entity nmsLootedEntity = ((CraftEntity) context.getLootedEntity()).getHandle();
                setMaybe(builder, LootContextParameters.THIS_ENTITY, nmsLootedEntity);
                setMaybe(builder, LootContextParameters.DAMAGE_SOURCE, DamageSource.GENERIC);
                setMaybe(builder, LootContextParameters.ORIGIN, nmsLootedEntity.getPos());
            }

            if (context.getKiller() != null) {
                PlayerEntity nmsKiller = ((CraftHumanEntity) context.getKiller()).getHandle();
                setMaybe(builder, LootContextParameters.KILLER_ENTITY, nmsKiller);
                // If there is a player killer, damage source should reflect that in case loot tables use that information
                setMaybe(builder, LootContextParameters.DAMAGE_SOURCE, DamageSource.player(nmsKiller));
                setMaybe(builder, LootContextParameters.LAST_DAMAGE_PLAYER, nmsKiller); // SPIGOT-5603 - Set minecraft:killed_by_player
            }

            // SPIGOT-5603 - Use LootContext#lootingModifier
            if (context.getLootingModifier() != LootContext.DEFAULT_LOOT_MODIFIER) {
                setMaybe(builder, LootContextParameters.LOOTING_MOD, context.getLootingModifier());
            }
        }

        // SPIGOT-5603 - Avoid IllegalArgumentException in LootTableInfo#build()
        LootContextType.Builder nmsBuilder = new LootContextType.Builder();
        for (LootContextParameter<?> param : getHandle().getType().getRequired()) {
            nmsBuilder.require(param);
        }
        for (LootContextParameter<?> param : getHandle().getType().getAllowed()) {
            if (!getHandle().getType().getRequired().contains(param)) {
                nmsBuilder.allow(param);
            }
        }
        nmsBuilder.allow(LootContextParameters.LOOTING_MOD);

        return builder.build(nmsBuilder.build());
    }

    private <T> void setMaybe(net.minecraft.loot.context.LootContext.Builder builder, LootContextParameter<T> param, T value) {
        if (getHandle().getType().getRequired().contains(param) || getHandle().getType().getAllowed().contains(param)) {
            builder.parameter(param, value);
        }
    }

    public static LootContext convertContext(net.minecraft.loot.context.LootContext info) {
        Vec3d position = info.get(LootContextParameters.ORIGIN);
        if (position == null) {
            position = info.get(LootContextParameters.THIS_ENTITY).getPos(); // Every vanilla context has origin or this_entity, see LootContextParameterSets
        }
        Location location = new Location(info.getWorld().getCraftWorld(), position.getX(), position.getY(), position.getZ());
        LootContext.Builder contextBuilder = new LootContext.Builder(location);

        if (info.hasParameter(LootContextParameters.KILLER_ENTITY)) {
            CraftEntity killer = info.get(LootContextParameters.KILLER_ENTITY).getBukkitEntity();
            if (killer instanceof CraftHumanEntity) {
                contextBuilder.killer((CraftHumanEntity) killer);
            }
        }

        if (info.hasParameter(LootContextParameters.THIS_ENTITY)) {
            contextBuilder.lootedEntity(info.get(LootContextParameters.THIS_ENTITY).getBukkitEntity());
        }

        if (info.hasParameter(LootContextParameters.LOOTING_MOD)) {
            contextBuilder.lootingModifier(info.get(LootContextParameters.LOOTING_MOD));
        }

        contextBuilder.luck(info.getLuck());
        return contextBuilder.build();
    }

    @Override
    public String toString() {
        return getKey().toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof org.bukkit.loot.LootTable)) {
            return false;
        }

        org.bukkit.loot.LootTable table = (org.bukkit.loot.LootTable) obj;
        return table.getKey().equals(this.getKey());
    }
}
