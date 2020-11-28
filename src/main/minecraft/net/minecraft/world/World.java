package net.minecraft.world;

import com.mojang.serialization.Codec;
import io.github.fukkitmc.fukkit.asm.Final;
import io.github.fukkitmc.fukkit.asm.Shadow;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.map.MapState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Packet;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.server.MinecraftServer;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.tag.TagManager;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.chunk.light.LightingProvider;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.explosion.ExplosionBehavior;
import org.apache.logging.log4j.Logger;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.block.CapturedBlockState;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

public abstract class World implements WorldAccess, AutoCloseable {

    // CraftBukkit start Added the following
    private final RegistryKey<DimensionType> typeKey;
    private final CraftWorld craftWorld;
    public boolean pvpMode;
    public boolean keepSpawnInMemory = true;
    public org.bukkit.generator.ChunkGenerator generator;

    public boolean captureBlockStates = false;
    public boolean captureTreeGeneration = false;
    public Map<BlockPos, CapturedBlockState> capturedBlockStates = new java.util.LinkedHashMap<>();
    public Map<BlockPos, BlockEntity> capturedTileEntities = new HashMap<>();
    public List<ItemEntity> captureDrops;
    public long ticksPerAnimalSpawns;
    public long ticksPerMonsterSpawns;
    public long ticksPerWaterSpawns;
    public long ticksPerWaterAmbientSpawns;
    public long ticksPerAmbientSpawns;
    public boolean populating;

    public CraftWorld getCraftWorld() {
        return this.craftWorld;
    }

    public CraftServer getCraftServer() {
        return (CraftServer) Bukkit.getServer();
    }

    public RegistryKey<DimensionType> getTypeKey() {
        return typeKey;
    }

    @Final
    @Shadow
    protected static Logger LOGGER;

    @Final
    @Shadow
    public static Codec<RegistryKey<World>> CODEC;

    @Final
    @Shadow
    public static RegistryKey<World> OVERWORLD;

    @Final
    @Shadow
    public static RegistryKey<World> NETHER;

    @Final
    @Shadow
    public static RegistryKey<World> END;

    @Final
    @Shadow
    private static Direction[] DIRECTIONS;

    @Final
    @Shadow
    public List<BlockEntity> blockEntities;

    @Final
    @Shadow
    public List<BlockEntity> tickingBlockEntities;

    @Final
    @Shadow
    protected List<BlockEntity> pendingBlockEntities;

    @Final
    @Shadow
    protected List<BlockEntity> unloadedBlockEntities;

    @Final
    @Shadow
    public Thread thread;

    @Final
    @Shadow
    private final boolean debugWorld;

    @Shadow
    private final int ambientDarkness;

    @Shadow
    protected int lcgBlockSeed;

    @Final
    @Shadow
    protected int unusedIncrement;

    @Shadow
    protected float rainGradientPrev;

    @Shadow
    public float rainGradient;

    @Shadow
    protected float thunderGradientPrev;

    @Shadow
    public float thunderGradient;

    @Final
    @Shadow
    public Random random;

    @Final
    @Shadow
    private final DimensionType dimension;

    @Final
    @Shadow
    public MutableWorldProperties properties;

    @Final
    @Shadow
    private final Supplier<Profiler> profiler;

    @Final
    @Shadow
    public boolean isClient;

    @Shadow
    protected boolean iteratingTickingBlockEntities;

    @Final
    @Shadow
    private final WorldBorder border;

    @Final
    @Shadow
    private final BiomeAccess biomeAccess;

    @Final
    @Shadow
    private final RegistryKey<World> registryKey;

    @Shadow
    protected World(MutableWorldProperties properties, RegistryKey<World> registryRef, DimensionType dimensionType, Supplier<Profiler> profiler, boolean isClient, boolean debugWorld, long seed) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public boolean isClient() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    @Nullable
    public MinecraftServer getServer() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public static boolean isInBuildLimit(BlockPos pos) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public static boolean isValid(BlockPos pos) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    private static boolean isValidHorizontally(BlockPos pos) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    private static boolean isInvalidVertically(int y) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public static boolean isOutOfBuildLimitVertically(BlockPos pos) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public static boolean isOutOfBuildLimitVertically(int y) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public WorldChunk getWorldChunk(BlockPos pos) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public WorldChunk getChunk(int i, int j) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public Chunk getChunk(int chunkX, int chunkZ, ChunkStatus leastStatus, boolean create) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public boolean setBlockState(BlockPos pos, BlockState state, int flags) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public boolean setBlockState(BlockPos pos, BlockState state, int flags, int maxUpdateDepth) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public void onBlockChanged(BlockPos pos, BlockState oldBlock, BlockState newBlock) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public boolean removeBlock(BlockPos pos, boolean move) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public boolean breakBlock(BlockPos pos, boolean drop, @Nullable Entity breakingEntity, int maxUpdateDepth) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public boolean setBlockState(BlockPos pos, BlockState state) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public abstract void updateListeners(BlockPos pos, BlockState oldState, BlockState newState, int flags);

    @Shadow
    public void scheduleBlockRerenderIfNeeded(BlockPos pos, BlockState old, BlockState updated) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public void updateNeighborsAlways(BlockPos pos, Block block) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public void updateNeighborsExcept(BlockPos pos, Block sourceBlock, Direction direction) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public void updateNeighbor(BlockPos sourcePos, Block sourceBlock, BlockPos neighborPos) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public int getTopY(Heightmap.Type heightmap, int x, int z) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public LightingProvider getLightingProvider() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public BlockState getBlockState(BlockPos pos) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public FluidState getFluidState(BlockPos pos) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public boolean isDay() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public boolean isNight() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public void playSound(@Nullable PlayerEntity player, BlockPos pos, SoundEvent sound, SoundCategory category, float volume, float pitch) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public abstract void playSound(@Nullable PlayerEntity player, double x, double y, double z, SoundEvent sound, SoundCategory category, float volume, float pitch);

    @Shadow
    public abstract void playSoundFromEntity(@Nullable PlayerEntity player, Entity entity, SoundEvent sound, SoundCategory category, float volume, float pitch);

    @Shadow
    public void playSound(double x, double y, double z, SoundEvent sound, SoundCategory category, float volume, float pitch, boolean bl) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public void addParticle(ParticleEffect parameters, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    @Environment(EnvType.CLIENT)
    public void addParticle(ParticleEffect parameters, boolean alwaysSpawn, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public void addImportantParticle(ParticleEffect parameters, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public void addImportantParticle(ParticleEffect parameters, boolean alwaysSpawn, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public float getSkyAngleRadians(float tickDelta) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public boolean addBlockEntity(BlockEntity blockEntity) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public void addBlockEntities(Collection<BlockEntity> blockEntities) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public void tickBlockEntities() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public void tickEntity(Consumer<Entity> tickConsumer, Entity entity) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public Explosion createExplosion(@Nullable Entity entity, double x, double y, double z, float power, Explosion.DestructionType destructionType) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public Explosion createExplosion(@Nullable Entity entity, double x, double y, double z, float power, boolean createFire, Explosion.DestructionType destructionType) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public Explosion createExplosion(@Nullable Entity entity, @Nullable DamageSource damageSource, @Nullable ExplosionBehavior explosionBehavior, double d, double e, double f, float g, boolean bl, Explosion.DestructionType destructionType) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public String getDebugString() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    @Nullable
    public BlockEntity getBlockEntity(BlockPos pos) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    @Nullable
    private BlockEntity getPendingBlockEntity(BlockPos pos) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public void setBlockEntity(BlockPos pos, @Nullable BlockEntity blockEntity) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public void removeBlockEntity(BlockPos pos) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public boolean canSetBlock(BlockPos pos) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public boolean isDirectionSolid(BlockPos pos, Entity entity, Direction direction) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public boolean isTopSolid(BlockPos pos, Entity entity) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public void calculateAmbientDarkness() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public void setMobSpawnOptions(boolean spawnMonsters, boolean spawnAnimals) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    protected void initWeatherGradients() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public void close() throws IOException {
        throw new UnsupportedOperationException();
    }

    @Shadow
    @Nullable
    public BlockView getExistingChunk(int chunkX, int chunkZ) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public List<Entity> getOtherEntities(@Nullable Entity except, Box box, @Nullable Predicate<? super Entity> predicate) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public <T extends Entity> List<T> getEntitiesByType(@Nullable EntityType<T> type, Box box, Predicate<? super T> predicate) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public <T extends Entity> List<T> getEntitiesByClass(Class<? extends T> entityClass, Box box, @Nullable Predicate<? super T> predicate) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public <T extends Entity> List<T> getEntitiesIncludingUngeneratedChunks(Class<? extends T> entityClass, Box box, @Nullable Predicate<? super T> predicate) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    @Nullable
    public abstract Entity getEntityById(int id);

    @Shadow
    public void markDirty(BlockPos pos, BlockEntity blockEntity) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public int getSeaLevel() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public int getReceivedStrongRedstonePower(BlockPos pos) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public boolean isEmittingRedstonePower(BlockPos pos, Direction direction) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public int getEmittedRedstonePower(BlockPos pos, Direction direction) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public boolean isReceivingRedstonePower(BlockPos pos) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public int getReceivedRedstonePower(BlockPos pos) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    @Environment(EnvType.CLIENT)
    public void disconnect() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public long getTime() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public long getTimeOfDay() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public boolean canPlayerModifyAt(PlayerEntity player, BlockPos pos) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public void sendEntityStatus(Entity entity, byte status) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public void addSyncedBlockEvent(BlockPos pos, Block block, int type, int data) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public WorldProperties getLevelProperties() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public GameRules getGameRules() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public float getThunderGradient(float delta) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    @Environment(EnvType.CLIENT)
    public void setThunderGradient(float thunderGradient) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public float getRainGradient(float delta) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    @Environment(EnvType.CLIENT)
    public void setRainGradient(float rainGradient) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public boolean isThundering() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public boolean isRaining() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public boolean hasRain(BlockPos pos) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public boolean hasHighHumidity(BlockPos pos) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    @Nullable
    public abstract MapState getMapState(String id);

    @Shadow
    public abstract void putMapState(MapState mapState);

    @Shadow
    public abstract int getNextMapId();

    @Shadow
    public void syncGlobalEvent(int eventId, BlockPos pos, int data) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public CrashReportSection addDetailsToCrashReport(CrashReport report) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public abstract void setBlockBreakingInfo(int entityId, BlockPos pos, int progress);

    @Shadow
    @Environment(EnvType.CLIENT)
    public void addFireworkParticle(double x, double y, double z, double velocityX, double velocityY, double velocityZ, @Nullable CompoundTag tag) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public abstract Scoreboard getScoreboard();

    @Shadow
    public void updateComparators(BlockPos pos, Block block) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public LocalDifficulty getLocalDifficulty(BlockPos pos) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public int getAmbientDarkness() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public void setLightningTicksLeft(int lightningTicksLeft) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public WorldBorder getWorldBorder() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public void sendPacket(Packet<?> packet) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public DimensionType getDimension() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public RegistryKey<World> getRegistryKey() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public Random getRandom() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public boolean testBlockState(BlockPos pos, Predicate<BlockState> state) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public abstract RecipeManager getRecipeManager();

    @Shadow
    public abstract TagManager getTagManager();

    @Shadow
    public BlockPos getRandomPosInChunk(int x, int y, int z, int i) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public boolean isSavingDisabled() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public Profiler getProfiler() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public Supplier<Profiler> getProfilerSupplier() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public BiomeAccess getBiomeAccess() {
        throw new UnsupportedOperationException();
    }

    @Final
    @Shadow
    public final boolean isDebugWorld() {
        throw new UnsupportedOperationException();
    }
}
