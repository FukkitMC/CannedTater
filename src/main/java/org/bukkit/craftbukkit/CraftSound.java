package org.bukkit.craftbukkit;

import com.google.common.base.Preconditions;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import org.bukkit.Registry;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;

public class CraftSound {

    public static SoundEvent getSoundEffect(String s) {
        SoundEvent effect = net.minecraft.util.registry.Registry.SOUND_EVENT.get(new Identifier(s));
        Preconditions.checkArgument(effect != null, "Sound effect %s does not exist", s);

        return effect;
    }

    public static SoundEvent getSoundEffect(Sound s) {
        SoundEvent effect = net.minecraft.util.registry.Registry.SOUND_EVENT.get(CraftNamespacedKey.toMinecraft(s.getKey()));
        Preconditions.checkArgument(effect != null, "Sound effect %s does not exist", s);

        return effect;
    }

    public static Sound getBukkit(SoundEvent soundEffect) {
        return Registry.SOUNDS.get(CraftNamespacedKey.fromMinecraft(net.minecraft.util.registry.Registry.SOUND_EVENT.getId(soundEffect)));
    }
}
