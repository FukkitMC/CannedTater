package net.minecraft.server;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.datafixers.DataFixer;
import io.github.fukkitmc.fukkit.asm.Final;
import io.github.fukkitmc.fukkit.asm.Shadow;
import jline.console.ConsoleReader;
import joptsimple.OptionSet;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.command.DataCommandStorage;
import net.minecraft.entity.boss.BossBarManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.loot.LootManager;
import net.minecraft.loot.condition.LootConditionManager;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.resource.DataPackSettings;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.ServerResourceManager;
import net.minecraft.scoreboard.ServerScoreboard;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.CommandOutput;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.filter.TextStream;
import net.minecraft.server.function.CommandFunctionManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructureManager;
import net.minecraft.tag.TagManager;
import net.minecraft.text.Text;
import net.minecraft.util.MetricsData;
import net.minecraft.util.TickDurationMonitor;
import net.minecraft.util.UserCache;
import net.minecraft.util.WorldSavePath;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.profiler.ProfileResult;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.profiler.TickTimeTracker;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.snooper.Snooper;
import net.minecraft.util.snooper.SnooperListener;
import net.minecraft.util.thread.ReentrantThreadExecutor;
import net.minecraft.world.*;
import net.minecraft.world.level.LevelInfo;
import net.minecraft.world.level.ServerWorldProperties;
import net.minecraft.world.level.storage.LevelStorage;
import org.apache.logging.log4j.Logger;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.CraftServer;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.net.Proxy;
import java.nio.file.Path;
import java.security.KeyPair;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BooleanSupplier;
import java.util.function.Function;

public abstract class MinecraftServer extends ReentrantThreadExecutor<ServerTask> implements SnooperListener, CommandOutput, AutoCloseable {

    @Deprecated
    public static MinecraftServer getServer() {
        return (Bukkit.getServer() instanceof CraftServer) ? ((CraftServer) Bukkit.getServer()).getServer() : null;
    }

    public DataPackSettings datapackconfiguration;
    public org.bukkit.craftbukkit.CraftServer server;
    public OptionSet options;
    public org.bukkit.command.ConsoleCommandSender console;
    public org.bukkit.command.RemoteConsoleCommandSender remoteConsole;
    public ConsoleReader reader;
    public static int currentTick = (int) (System.currentTimeMillis() / 50);
    public java.util.Queue<Runnable> processQueue = new java.util.concurrent.ConcurrentLinkedQueue<Runnable>();
    public int autosavePeriod;
    public CommandManager vanillaCommandDispatcher;
    private boolean forceTicks;

    @Final
    @Shadow
    public static Logger LOGGER;

    @Final
    @Shadow
    public static File USER_CACHE_FILE;

    @Final
    @Shadow
    public static LevelInfo DEMO_LEVEL_INFO;

    @Final
    @Shadow
    public LevelStorage.Session session;

    @Final
    @Shadow
    public WorldSaveHandler saveHandler;

    @Final
    @Shadow
    private final Snooper snooper;

    @Final
    @Shadow
    private final List<Runnable> serverGuiTickables;

    @Final
    @Shadow
    private final TickTimeTracker tickTimeTracker;

    @Shadow
    private final Profiler profiler;

    @Final
    @Shadow
    public ServerNetworkIo networkIo;

    @Final
    @Shadow
    public WorldGenerationProgressListenerFactory worldGenerationProgressListenerFactory;

    @Final
    @Shadow
    private final ServerMetadata metadata;

    @Final
    @Shadow
    private final Random random;

    @Final
    @Shadow
    public DataFixer dataFixer;

    @Shadow
    private final String serverIp;

    @Shadow
    private final int serverPort;

    @Final
    @Shadow
    public DynamicRegistryManager.Impl registryManager;

    @Final
    @Shadow
    public Map<RegistryKey<World>, ServerWorld> worlds;

    @Shadow
    private final PlayerManager playerManager;

    @Shadow
    private final boolean running;

    @Shadow
    private final boolean stopped;

    @Shadow
    private final int ticks;

    @Final
    @Shadow
    protected Proxy proxy;

    @Shadow
    private final boolean onlineMode;

    @Shadow
    private final boolean preventProxyConnections;

    @Shadow
    private final boolean pvpEnabled;

    @Shadow
    private final boolean flightEnabled;

    @Shadow
    private final String motd;

    @Shadow
    private final int worldHeight;

    @Shadow
    private final int playerIdleTimeout;

    @Final
    @Shadow
    public long[] lastTickLengths;

    @Shadow
    private final KeyPair keyPair;

    @Shadow
    private final String userName;

    @Shadow
    private final boolean demo;

    @Shadow
    private final String resourcePackUrl;

    @Shadow
    private final String resourcePackHash;

    @Shadow
    private final boolean loading;

    @Shadow
    private final long lastTimeReference;

    @Shadow
    private final boolean profilerStartQueued;

    @Shadow
    private final boolean forceGameMode;

    @Final
    @Shadow
    private final MinecraftSessionService sessionService;

    @Final
    @Shadow
    private final GameProfileRepository gameProfileRepo;

    @Final
    @Shadow
    private final UserCache userCache;

    @Shadow
    private final long lastPlayerSampleUpdate;

    @Final
    @Shadow
    public Thread serverThread;

    @Shadow
    private final long timeReference;

    @Shadow
    private final long field_19248;

    @Shadow
    private final boolean waitingForNextTick;

    @Shadow
    private final boolean iconFilePresent;

    @Final
    @Shadow
    private final ResourcePackManager dataPackManager;

    @Final
    @Shadow
    private final ServerScoreboard scoreboard;

    @Shadow
    private final DataCommandStorage dataCommandStorage;

    @Final
    @Shadow
    private final BossBarManager bossBarManager;

    @Final
    @Shadow
    private final CommandFunctionManager commandFunctionManager;

    @Final
    @Shadow
    private final MetricsData metricsData;

    @Shadow
    private final boolean enforceWhitelist;

    @Shadow
    private final float tickTime;

    @Final
    @Shadow
    public Executor workerExecutor;

    @Shadow
    private final String serverId;

    @Shadow
    public ServerResourceManager serverResourceManager;

    @Final
    @Shadow
    private final StructureManager structureManager;

    @Final
    @Shadow
    public SaveProperties saveProperties;

    @Shadow
    public MinecraftServer(Thread thread, DynamicRegistryManager.Impl impl, LevelStorage.Session session, SaveProperties saveProperties, ResourcePackManager resourcePackManager, Proxy proxy, DataFixer dataFixer, ServerResourceManager serverResourceManager, MinecraftSessionService minecraftSessionService, GameProfileRepository gameProfileRepository, UserCache userCache, WorldGenerationProgressListenerFactory worldGenerationProgressListenerFactory) {
        super(null);
        throw new UnsupportedOperationException();
    }

    @Shadow
    public static <S extends MinecraftServer> S startServer(Function<Thread, S> serverFactory) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    private void initScoreboard(PersistentStateManager persistentStateManager) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    protected abstract boolean setupServer() throws IOException;

    @Shadow
    public static void convertLevel(LevelStorage.Session session) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    protected void loadWorld() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    protected void method_27731() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    protected void createWorlds(WorldGenerationProgressListener worldGenerationProgressListener) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    private static void setupSpawn(ServerWorld world, ServerWorldProperties serverWorldProperties, boolean bonusChest, boolean debugWorld, boolean bl) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    private void setToDebugWorldProperties(SaveProperties properties) {
        throw new UnsupportedOperationException();
    }

    @Final
    @Shadow
    public final void prepareStartRegion(WorldGenerationProgressListener worldGenerationProgressListener) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    protected void loadWorldResourcePack() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public GameMode getDefaultGameMode() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public boolean isHardcore() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public abstract int getOpPermissionLevel();

    @Shadow
    public abstract int getFunctionPermissionLevel();

    @Shadow
    public abstract boolean shouldBroadcastRconToOps();

    @Shadow
    public boolean save(boolean suppressLogs, boolean bl, boolean bl2) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public void close() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    protected void shutdown() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public String getServerIp() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public void setServerIp(String serverIp) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public boolean isRunning() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public void stop(boolean bl) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    protected void runServer() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    private boolean shouldKeepTicking() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    protected void method_16208() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    protected ServerTask createTask(Runnable runnable) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    protected boolean canExecute(ServerTask serverTask) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public boolean runTask() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    private boolean method_20415() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    protected void executeTask(ServerTask serverTask) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    private void setFavicon(ServerMetadata metadata) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    @Environment(EnvType.CLIENT)
    public boolean hasIconFile() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    @Environment(EnvType.CLIENT)
    public File getIconFile() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public File getRunDirectory() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    protected void setCrashReport(CrashReport report) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    protected void exit() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    protected void tick(BooleanSupplier shouldKeepTicking) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    protected void tickWorlds(BooleanSupplier shouldKeepTicking) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public boolean isNetherAllowed() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public void addServerGuiTickable(Runnable tickable) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    protected void setServerId(String serverId) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    @Environment(EnvType.CLIENT)
    public boolean isStopping() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public File getFile(String path) {
        throw new UnsupportedOperationException();
    }

    @Final
    @Shadow
    public final ServerWorld getOverworld() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    @Nullable
    public ServerWorld getWorld(RegistryKey<World> key) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public Set<RegistryKey<World>> getWorldRegistryKeys() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public Iterable<ServerWorld> getWorlds() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public String getVersion() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public int getCurrentPlayerCount() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public int getMaxPlayerCount() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public String[] getPlayerNames() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public String getServerModName() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public CrashReport populateCrashReport(CrashReport report) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public abstract Optional<String> getModdedStatusMessage();

    @Shadow
    public void sendSystemMessage(Text message, UUID senderUuid) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public KeyPair getKeyPair() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public int getServerPort() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public void setServerPort(int serverPort) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public String getUserName() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public void setServerName(String serverName) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public boolean isSinglePlayer() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    protected void method_31400() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public void setDifficulty(Difficulty difficulty, boolean forceUpdate) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public int adjustTrackingDistance(int initialDistance) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    private void updateMobSpawnOptions() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public void setDifficultyLocked(boolean locked) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    private void sendDifficulty(ServerPlayerEntity player) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    protected boolean isMonsterSpawningEnabled() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public boolean isDemo() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public void setDemo(boolean demo) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public String getResourcePackUrl() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public String getResourcePackHash() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public void setResourcePack(String url, String hash) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public void addSnooperInfo(Snooper snooper) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public abstract boolean isDedicated();

    @Shadow
    public abstract int getRateLimit();

    @Shadow
    public boolean isOnlineMode() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public void setOnlineMode(boolean onlineMode) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public boolean shouldPreventProxyConnections() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public void setPreventProxyConnections(boolean preventProxyConnections) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public boolean shouldSpawnAnimals() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public boolean shouldSpawnNpcs() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public abstract boolean isUsingNativeTransport();

    @Shadow
    public boolean isPvpEnabled() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public void setPvpEnabled(boolean pvpEnabled) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public boolean isFlightEnabled() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public void setFlightEnabled(boolean flightEnabled) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public abstract boolean areCommandBlocksEnabled();

    @Shadow
    public String getServerMotd() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public void setMotd(String motd) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public int getWorldHeight() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public void setWorldHeight(int worldHeight) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public boolean isStopped() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public PlayerManager getPlayerManager() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public void setPlayerManager(PlayerManager playerManager) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public abstract boolean isRemote();

    @Shadow
    public void setDefaultGameMode(GameMode gameMode) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    @Nullable
    public ServerNetworkIo getNetworkIo() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    @Environment(EnvType.CLIENT)
    public boolean isLoading() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public boolean hasGui() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public abstract boolean openToLan(GameMode gameMode, boolean cheatsAllowed, int port);

    @Shadow
    public int getTicks() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    @Environment(EnvType.CLIENT)
    public Snooper getSnooper() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public int getSpawnProtectionRadius() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public boolean isSpawnProtected(ServerWorld world, BlockPos pos, PlayerEntity player) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public void setForceGameMode(boolean forceGameMode) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public boolean shouldForceGameMode() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public boolean acceptsStatusQuery() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public int getPlayerIdleTimeout() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public void setPlayerIdleTimeout(int playerIdleTimeout) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public MinecraftSessionService getSessionService() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public GameProfileRepository getGameProfileRepo() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public UserCache getUserCache() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public ServerMetadata getServerMetadata() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public void forcePlayerSampleUpdate() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public int getMaxWorldBorderRadius() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public boolean shouldExecuteAsync() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public Thread getThread() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public int getNetworkCompressionThreshold() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public long getServerStartTime() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public DataFixer getDataFixer() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public int getSpawnRadius(@Nullable ServerWorld world) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public ServerAdvancementLoader getAdvancementLoader() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public CommandFunctionManager getCommandFunctionManager() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public CompletableFuture<Void> reloadResources(Collection<String> datapacks) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public static DataPackSettings loadDataPacks(ResourcePackManager resourcePackManager, DataPackSettings dataPackSettings, boolean safeMode) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    private static DataPackSettings method_29735(ResourcePackManager resourcePackManager) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public void kickNonWhitelistedPlayers(ServerCommandSource source) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public ResourcePackManager getDataPackManager() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public CommandManager getCommandManager() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public ServerCommandSource getCommandSource() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public boolean shouldReceiveFeedback() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public boolean shouldTrackOutput() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public RecipeManager getRecipeManager() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public TagManager getTagManager() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public ServerScoreboard getScoreboard() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public DataCommandStorage getDataCommandStorage() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public LootManager getLootManager() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public LootConditionManager getPredicateManager() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public GameRules getGameRules() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public BossBarManager getBossBarManager() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public boolean isEnforceWhitelist() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public void setEnforceWhitelist(boolean whitelistEnabled) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public float getTickTime() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public int getPermissionLevel(GameProfile profile) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    @Environment(EnvType.CLIENT)
    public MetricsData getMetricsData() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public Profiler getProfiler() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public abstract boolean isHost(GameProfile profile);

    @Shadow
    public void dump(Path path) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Shadow
    private void dumpStats(Path path) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Shadow
    private void dumpExampleCrash(Path path) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Shadow
    private void dumpGamerules(Path path) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Shadow
    private void dumpClasspath(Path path) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Shadow
    private void dumpThreads(Path path) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Shadow
    private void startMonitor(@Nullable TickDurationMonitor monitor) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    private void endMonitor(@Nullable TickDurationMonitor monitor) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public boolean isDebugRunning() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public void enableProfiler() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public ProfileResult stopDebug() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public Path getSavePath(WorldSavePath worldSavePath) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public boolean syncChunkWrites() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public StructureManager getStructureManager() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public SaveProperties getSaveProperties() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public DynamicRegistryManager getRegistryManager() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    @Nullable
    public TextStream createFilterer(ServerPlayerEntity player) {
        throw new UnsupportedOperationException();
    }
}
