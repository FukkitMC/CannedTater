package org.bukkit.craftbukkit;

import java.util.HashMap;
import net.minecraft.sound.BlockSoundGroup;
import org.bukkit.Sound;
import org.bukkit.SoundGroup;

public class CraftSoundGroup implements SoundGroup {

    private final net.minecraft.sound.BlockSoundGroup handle;
    private static final HashMap<BlockSoundGroup, CraftSoundGroup> SOUND_GROUPS = new HashMap<>();

    public static SoundGroup getSoundGroup(BlockSoundGroup soundEffectType) {
        return SOUND_GROUPS.computeIfAbsent(soundEffectType, CraftSoundGroup::new);
    }

    private CraftSoundGroup(net.minecraft.sound.BlockSoundGroup soundEffectType) {
        this.handle = soundEffectType;
    }

    public net.minecraft.sound.BlockSoundGroup getHandle() {
        return handle;
    }

    @Override
    public float getVolume() {
        return getHandle().volume; // PAIL rename volume
    }

    @Override
    public float getPitch() {
        return getHandle().pitch; // PAIL rename pitch
    }

    @Override
    public Sound getBreakSound() {
        return CraftSound.getBukkit(getHandle().breakSound);
    }

    @Override
    public Sound getStepSound() {
        return CraftSound.getBukkit(getHandle().getStepSound()); // PAIL rename getStepSound
    }

    @Override
    public Sound getPlaceSound() {
        return CraftSound.getBukkit(getHandle().getPlaceSound()); // PAIL rename getPlaceSound
    }

    @Override
    public Sound getHitSound() {
        return CraftSound.getBukkit(getHandle().hitSound);
    }

    @Override
    public Sound getFallSound() {
        return CraftSound.getBukkit(getHandle().getFallSound()); // PAIL rename getFallSound
    }
}
