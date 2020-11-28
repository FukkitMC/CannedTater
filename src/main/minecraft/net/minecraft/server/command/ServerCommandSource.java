package net.minecraft.server.command;

import com.mojang.brigadier.ResultConsumer;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.tree.CommandNode;
import io.github.fukkitmc.fukkit.asm.Final;
import io.github.fukkitmc.fukkit.asm.Shadow;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.EntityAnchorArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.BinaryOperator;
import java.util.stream.Stream;

public class ServerCommandSource {

    public volatile CommandNode<?> currentCommand;

    public boolean hasPermission(int i, String bukkitPermission) {
        // World is null when loading functions
        return ((getWorld() == null || !getWorld().getCraftServer().ignoreVanillaPermissions) && this.level >= i) || getBukkitSender().hasPermission(bukkitPermission);
    }

    public org.bukkit.command.CommandSender getBukkitSender() {
        return output.getBukkitSender(this);
    }

    @Final
    @Shadow
    public static SimpleCommandExceptionType REQUIRES_PLAYER_EXCEPTION;

    @Final
    @Shadow
    public static SimpleCommandExceptionType REQUIRES_ENTITY_EXCEPTION;

    @Final
    @Shadow
    public CommandOutput output;

    @Final
    @Shadow
    private final Vec3d position;

    @Final
    @Shadow
    private final ServerWorld world;

    @Final
    @Shadow
    private final int level;

    @Final
    @Shadow
    private final String simpleName;

    @Final
    @Shadow
    private final Text name;

    @Final
    @Shadow
    private final MinecraftServer server;

    @Final
    @Shadow
    private final boolean silent;

    @Final
    @Shadow
    private final Entity entity;

    @Final
    @Shadow
    private final ResultConsumer<ServerCommandSource> resultConsumer;

    @Final
    @Shadow
    private final EntityAnchorArgumentType.EntityAnchor entityAnchor;

    @Final
    @Shadow
    private final Vec2f rotation;

    @Shadow
    public ServerCommandSource(CommandOutput output, Vec3d pos, Vec2f rot, ServerWorld world, int level, String simpleName, Text name, MinecraftServer server, @Nullable Entity entity) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    protected ServerCommandSource(CommandOutput output, Vec3d pos, Vec2f rot, ServerWorld world, int level, String simpleName, Text name, MinecraftServer server, @Nullable Entity entity, boolean silent, ResultConsumer<ServerCommandSource> consumer, EntityAnchorArgumentType.EntityAnchor entityAnchor) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public ServerCommandSource withEntity(Entity entity) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public ServerCommandSource withPosition(Vec3d position) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public ServerCommandSource withRotation(Vec2f rotation) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public ServerCommandSource withConsumer(ResultConsumer<ServerCommandSource> consumer) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public ServerCommandSource mergeConsumers(ResultConsumer<ServerCommandSource> consumer, BinaryOperator<ResultConsumer<ServerCommandSource>> binaryOperator) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public ServerCommandSource withSilent() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public ServerCommandSource withLevel(int level) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public ServerCommandSource withMaxLevel(int level) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public ServerCommandSource withEntityAnchor(EntityAnchorArgumentType.EntityAnchor anchor) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public ServerCommandSource withWorld(ServerWorld world) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public ServerCommandSource withLookingAt(Entity entity, EntityAnchorArgumentType.EntityAnchor anchor) throws CommandSyntaxException {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public ServerCommandSource withLookingAt(Vec3d position) throws CommandSyntaxException {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public Text getDisplayName() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public String getName() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public boolean hasPermissionLevel(int level) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public Vec3d getPosition() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public ServerWorld getWorld() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    @Nullable
    public Entity getEntity() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public Entity getEntityOrThrow() throws CommandSyntaxException {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public ServerPlayerEntity getPlayer() throws CommandSyntaxException {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public Vec2f getRotation() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public MinecraftServer getMinecraftServer() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public EntityAnchorArgumentType.EntityAnchor getEntityAnchor() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public void sendFeedback(Text message, boolean broadcastToOps) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    private void sendToOps(Text message) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public void sendError(Text message) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public void onCommandComplete(CommandContext<ServerCommandSource> context, boolean success, int result) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public Collection<String> getPlayerNames() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public Collection<String> getTeamNames() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public Collection<Identifier> getSoundIds() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public Stream<Identifier> getRecipeIds() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public CompletableFuture<Suggestions> getCompletions(CommandContext<CommandSource> context, SuggestionsBuilder builder) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public Set<RegistryKey<World>> getWorldKeys() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public DynamicRegistryManager getRegistryManager() {
        throw new UnsupportedOperationException();
    }
}
