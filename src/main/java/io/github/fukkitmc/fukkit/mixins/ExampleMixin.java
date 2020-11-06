package io.github.fukkitmc.fukkit.mixins;

import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public class ExampleMixin {

    @Inject(method = "tick", at = @At("RETURN"))
    private void onTick(CallbackInfo callbackInfo) {
    }
}
