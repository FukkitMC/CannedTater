package net.minecraft.entity;

import io.github.fukkitmc.fukkit.asm.Final;
import io.github.fukkitmc.fukkit.asm.Shadow;
import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.class_5459;
import net.minecraft.command.argument.EntityAnchorArgumentType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.Packet;
import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandOutput;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.tag.Tag;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.util.collection.ReusableStream;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.*;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.explosion.Explosion;
import org.apache.logging.log4j.Logger;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

public abstract class Entity implements Nameable, CommandOutput {

    // CraftBukkit start
    private static final int CURRENT_LEVEL = 2;
    static boolean isLevelAtLeast(CompoundTag tag, int level) {
        return tag.contains("Bukkit.updateLevel") && tag.getInt("Bukkit.updateLevel") >= level;
    }

    private CraftEntity bukkitEntity;

    public CraftEntity getBukkitEntity() {
        if (bukkitEntity == null) {
            bukkitEntity = CraftEntity.getEntity(world.getCraftServer(), this);
        }
        return bukkitEntity;
    }

    @Override
    public CommandSender getBukkitSender(ServerCommandSource wrapper) {
        return getBukkitEntity();
    }
    // CraftBukkit end

    @Final
    @Shadow
    protected static Logger LOGGER;

    @Final
    @Shadow
    private static AtomicInteger MAX_ENTITY_ID;

    @Final
    @Shadow
    private static List<ItemStack> EMPTY_STACK_LIST;

    @Final
    @Shadow
    private static Box NULL_BOX;

    @Shadow
    private static double renderDistanceMultiplier;

    @Final
    @Shadow
    private final EntityType<?> type;

    @Shadow
    private final int entityId;

    @Shadow
    public boolean inanimate;

    @Final
    @Shadow
    public List<Entity> passengerList;

    @Shadow
    protected int ridingCooldown;

    @Shadow
    private final Entity vehicle;

    @Shadow
    public boolean teleporting;

    @Shadow
    public World world;

    @Shadow
    public double prevX;

    @Shadow
    public double prevY;

    @Shadow
    public double prevZ;

    @Shadow
    private final Vec3d pos;

    @Shadow
    private final BlockPos blockPos;

    @Shadow
    private final Vec3d velocity;

    @Shadow
    public float yaw;

    @Shadow
    public float pitch;

    @Shadow
    public float prevYaw;

    @Shadow
    public float prevPitch;

    @Shadow
    private final Box entityBounds;

    @Shadow
    protected boolean onGround;

    @Shadow
    public boolean horizontalCollision;

    @Shadow
    public boolean verticalCollision;

    @Shadow
    public boolean velocityModified;

    @Shadow
    protected Vec3d movementMultiplier;

    @Shadow
    public boolean removed;

    @Shadow
    public float prevHorizontalSpeed;

    @Shadow
    public float horizontalSpeed;

    @Shadow
    public float distanceTraveled;

    @Shadow
    public float fallDistance;

    @Shadow
    private final float nextStepSoundDistance;

    @Shadow
    private final float nextFlySoundDistance;

    @Shadow
    public double lastRenderX;

    @Shadow
    public double lastRenderY;

    @Shadow
    public double lastRenderZ;

    @Shadow
    public float stepHeight;

    @Shadow
    public boolean noClip;

    @Shadow
    public float pushSpeedReduction;

    @Final
    @Shadow
    protected Random random;

    @Shadow
    public int age;

    @Shadow
    public int fireTicks;

    @Shadow
    public boolean touchingWater;

    @Shadow
    protected Object2DoubleMap<Tag<Fluid>> fluidHeight;

    @Shadow
    protected boolean submergedInWater;

    @Shadow
    protected Tag<Fluid> field_25599;

    @Shadow
    public int timeUntilRegen;

    @Shadow
    protected boolean firstUpdate;

    @Final
    @Shadow
    public DataTracker dataTracker;

    @Final
    @Shadow
    protected static TrackedData<Byte> FLAGS;

    @Final
    @Shadow
    private static TrackedData<Integer> AIR;

    @Final
    @Shadow
    private static TrackedData<Optional<Text>> CUSTOM_NAME;

    @Final
    @Shadow
    private static TrackedData<Boolean> NAME_VISIBLE;

    @Final
    @Shadow
    private static TrackedData<Boolean> SILENT;

    @Final
    @Shadow
    private static TrackedData<Boolean> NO_GRAVITY;

    @Final
    @Shadow
    protected static TrackedData<EntityPose> POSE;

    @Shadow
    public boolean updateNeeded;

    @Shadow
    public int chunkX;

    @Shadow
    public int chunkY;

    @Shadow
    public int chunkZ;

    @Shadow
    private final boolean chunkPosUpdateRequested;

    @Shadow
    private final Vec3d trackedPosition;

    @Shadow
    public boolean ignoreCameraFrustum;

    @Shadow
    public boolean velocityDirty;

    @Shadow
    public int netherPortalCooldown;

    @Shadow
    protected boolean inNetherPortal;

    @Shadow
    protected int netherPortalTime;

    @Shadow
    protected BlockPos lastNetherPortalPosition;

    @Shadow
    private final boolean invulnerable;

    @Shadow
    protected UUID uuid;

    @Shadow
    protected String uuidString;

    @Shadow
    public boolean glowing;

    @Final
    @Shadow
    private final Set<String> scoreboardTags;

    @Shadow
    private final boolean teleportRequested;

    @Final
    @Shadow
    private final double[] pistonMovementDelta;

    @Shadow
    private final long pistonMovementTick;

    @Shadow
    private final EntityDimensions dimensions;

    @Shadow
    private final float standingEyeHeight;

    @Shadow
    public Entity(EntityType<?> type, World world) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    @Environment(EnvType.CLIENT)
    public boolean method_30632(BlockPos blockPos, BlockState blockState) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    @Environment(EnvType.CLIENT)
    public int getTeamColorValue() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public boolean isSpectator() {
        throw new UnsupportedOperationException();
    }

    @Final
    @Shadow
    public final void detach() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public void updateTrackedPosition(double x, double y, double z) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public void updateTrackedPosition(Vec3d pos) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    @Environment(EnvType.CLIENT)
    public Vec3d getTrackedPosition() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public EntityType<?> getType() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public int getEntityId() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public void setEntityId(int id) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public Set<String> getScoreboardTags() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public boolean addScoreboardTag(String tag) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public boolean removeScoreboardTag(String tag) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public void kill() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    protected abstract void initDataTracker();

    @Shadow
    public DataTracker getDataTracker() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public boolean equals(Object o) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public int hashCode() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    @Environment(EnvType.CLIENT)
    protected void afterSpawn() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public void remove() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public void setPose(EntityPose pose) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public EntityPose getPose() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public boolean isInRange(Entity other, double radius) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    protected void setRotation(float yaw, float pitch) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public void updatePosition(double x, double y, double z) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    protected void refreshPosition() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    @Environment(EnvType.CLIENT)
    public void changeLookDirection(double cursorDeltaX, double cursorDeltaY) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public void tick() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public void baseTick() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public void resetNetherPortalCooldown() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public boolean hasNetherPortalCooldown() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    protected void tickNetherPortalCooldown() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public int getMaxNetherPortalTime() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    protected void setOnFireFromLava() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public void setOnFireFor(int seconds) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public void setFireTicks(int ticks) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public int getFireTicks() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public void extinguish() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    protected void destroy() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public boolean doesNotCollide(double offsetX, double offsetY, double offsetZ) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    private boolean doesNotCollide(Box box) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public void setOnGround(boolean onGround) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public boolean isOnGround() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public void move(MovementType type, Vec3d movement) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    protected BlockPos getLandingPos() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    protected float getJumpVelocityMultiplier() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    protected float getVelocityMultiplier() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    protected BlockPos getVelocityAffectingPos() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    protected Vec3d adjustMovementForSneaking(Vec3d movement, MovementType type) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    protected Vec3d adjustMovementForPiston(Vec3d movement) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    private double calculatePistonMovementFactor(Direction.Axis axis, double offsetFactor) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    private Vec3d adjustMovementForCollisions(Vec3d movement) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public static double squaredHorizontalLength(Vec3d vector) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public static Vec3d adjustMovementForCollisions(@Nullable Entity entity, Vec3d movement, Box entityBoundingBox, World world, ShapeContext context, ReusableStream<VoxelShape> collisions) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public static Vec3d adjustMovementForCollisions(Vec3d movement, Box entityBoundingBox, ReusableStream<VoxelShape> collisions) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public static Vec3d adjustSingleAxisMovementForCollisions(Vec3d movement, Box entityBoundingBox, WorldView world, ShapeContext context, ReusableStream<VoxelShape> collisions) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    protected float calculateNextStepSoundDistance() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public void moveToBoundingBoxCenter() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    protected SoundEvent getSwimSound() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    protected SoundEvent getSplashSound() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    protected SoundEvent getHighSpeedSplashSound() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    protected void checkBlockCollision() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    protected void onBlockCollision(BlockState state) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    protected void playStepSound(BlockPos pos, BlockState state) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    protected void playSwimSound(float volume) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    protected float playFlySound(float distance) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    protected boolean hasWings() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public void playSound(SoundEvent sound, float volume, float pitch) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public boolean isSilent() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public void setSilent(boolean silent) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public boolean hasNoGravity() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public void setNoGravity(boolean noGravity) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    protected boolean canClimb() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    protected void fall(double heightDifference, boolean onGround, BlockState landedState, BlockPos landedPosition) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public boolean isFireImmune() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public boolean handleFallDamage(float fallDistance, float damageMultiplier) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public boolean isTouchingWater() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    private boolean isBeingRainedOn() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    private boolean isInsideBubbleColumn() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public boolean isTouchingWaterOrRain() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public boolean isWet() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public boolean isInsideWaterOrBubbleColumn() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public boolean isSubmergedInWater() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public void updateSwimming() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    protected boolean updateWaterState() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    void checkWaterState() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    private void updateSubmergedInWaterState() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    protected void onSwimmingStart() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    protected BlockState getLandingBlockState() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public boolean shouldSpawnSprintingParticles() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    protected void spawnSprintingParticles() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public boolean isSubmergedIn(Tag<Fluid> tag) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public boolean isInLava() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public void updateVelocity(float speed, Vec3d movementInput) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    private static Vec3d movementInputToVelocity(Vec3d movementInput, float speed, float yaw) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public float getBrightnessAtEyes() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public void setWorld(World world) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public void updatePositionAndAngles(double x, double y, double z, float yaw, float pitch) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public void method_30634(double x, double y, double z) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public void refreshPositionAfterTeleport(Vec3d vec3d) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public void refreshPositionAfterTeleport(double x, double y, double z) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public void refreshPositionAndAngles(BlockPos pos, float yaw, float pitch) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public void refreshPositionAndAngles(double x, double y, double z, float yaw, float pitch) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public void resetPosition(double x, double y, double z) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public float distanceTo(Entity entity) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public double squaredDistanceTo(double x, double y, double z) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public double squaredDistanceTo(Entity entity) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public double squaredDistanceTo(Vec3d vector) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public void onPlayerCollision(PlayerEntity player) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public void pushAwayFrom(Entity entity) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public void addVelocity(double deltaX, double deltaY, double deltaZ) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    protected void scheduleVelocityUpdate() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public boolean damage(DamageSource source, float amount) {
        throw new UnsupportedOperationException();
    }

    @Final
    @Shadow
    public final Vec3d getRotationVec(float tickDelta) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public float getPitch(float tickDelta) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public float getYaw(float tickDelta) {
        throw new UnsupportedOperationException();
    }

    @Final
    @Shadow
    protected final Vec3d getRotationVector(float pitch, float yaw) {
        throw new UnsupportedOperationException();
    }

    @Final
    @Shadow
    public final Vec3d getOppositeRotationVector(float tickDelta) {
        throw new UnsupportedOperationException();
    }

    @Final
    @Shadow
    protected final Vec3d getOppositeRotationVector(float pitch, float yaw) {
        throw new UnsupportedOperationException();
    }

    @Final
    @Shadow
    public final Vec3d getCameraPosVec(float tickDelta) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    @Environment(EnvType.CLIENT)
    public Vec3d method_31166(float tickDelta) {
        throw new UnsupportedOperationException();
    }

    @Final
    @Shadow
    @Environment(EnvType.CLIENT)
    public final Vec3d method_30950(float f) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public HitResult raycast(double maxDistance, float tickDelta, boolean includeFluids) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public boolean collides() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public boolean isPushable() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public void updateKilledAdvancementCriterion(Entity killer, int score, DamageSource damageSource) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    @Environment(EnvType.CLIENT)
    public boolean shouldRender(double cameraX, double cameraY, double cameraZ) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    @Environment(EnvType.CLIENT)
    public boolean shouldRender(double distance) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public boolean saveSelfToTag(CompoundTag tag) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public boolean saveToTag(CompoundTag tag) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public CompoundTag toTag(CompoundTag tag) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public void fromTag(CompoundTag tag) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    protected boolean shouldSetPositionOnLoad() {
        throw new UnsupportedOperationException();
    }

    @Final
    @Shadow
    @Nullable
    public final String getSavedEntityId() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    protected abstract void readCustomDataFromTag(CompoundTag tag);

    @Shadow
    protected abstract void writeCustomDataToTag(CompoundTag tag);

    @Shadow
    protected ListTag toListTag(double... values) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    protected ListTag toListTag(float... values) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    @Nullable
    public ItemEntity dropItem(ItemConvertible item) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    @Nullable
    public ItemEntity dropItem(ItemConvertible item, int yOffset) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    @Nullable
    public ItemEntity dropStack(ItemStack stack) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    @Nullable
    public ItemEntity dropStack(ItemStack stack, float yOffset) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public boolean isAlive() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public boolean isInsideWall() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public ActionResult interact(PlayerEntity player, Hand hand) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public boolean collidesWith(Entity other) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public boolean isCollidable() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public void tickRiding() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public void updatePassengerPosition(Entity passenger) {
        throw new UnsupportedOperationException();
    }

    // @Shadow
    // private void updatePassengerPosition(Entity passenger, Entity.PositionUpdater positionUpdater) {
    //     throw new UnsupportedOperationException();
    // }

    @Shadow
    @Environment(EnvType.CLIENT)
    public void onPassengerLookAround(Entity passenger) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public double getHeightOffset() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public double getMountedHeightOffset() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public boolean startRiding(Entity entity) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    @Environment(EnvType.CLIENT)
    public boolean isLiving() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public boolean startRiding(Entity entity, boolean force) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    protected boolean canStartRiding(Entity entity) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    protected boolean wouldPoseNotCollide(EntityPose pose) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public void removeAllPassengers() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public void method_29239() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public void stopRiding() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    protected void addPassenger(Entity passenger) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    protected void removePassenger(Entity passenger) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    protected boolean canAddPassenger(Entity passenger) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    @Environment(EnvType.CLIENT)
    public void updateTrackedPositionAndAngles(double x, double y, double z, float yaw, float pitch, int interpolationSteps, boolean interpolate) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    @Environment(EnvType.CLIENT)
    public void updateTrackedHeadRotation(float yaw, int interpolationSteps) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public float getTargetingMargin() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public Vec3d getRotationVector() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public Vec2f getRotationClient() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    @Environment(EnvType.CLIENT)
    public Vec3d getRotationVecClient() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public void setInNetherPortal(BlockPos pos) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    protected void tickNetherPortal() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public int getDefaultNetherPortalCooldown() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    @Environment(EnvType.CLIENT)
    public void setVelocityClient(double x, double y, double z) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    @Environment(EnvType.CLIENT)
    public void handleStatus(byte status) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    @Environment(EnvType.CLIENT)
    public void animateDamage() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public Iterable<ItemStack> getItemsHand() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public Iterable<ItemStack> getArmorItems() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public Iterable<ItemStack> getItemsEquipped() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public void equipStack(EquipmentSlot slot, ItemStack stack) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public boolean isOnFire() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public boolean hasVehicle() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public boolean hasPassengers() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public boolean canBeRiddenInWater() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public void setSneaking(boolean sneaking) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public boolean isSneaking() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public boolean bypassesSteppingEffects() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public boolean bypassesLandingEffects() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public boolean isSneaky() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public boolean isDescending() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public boolean isInSneakingPose() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public boolean isSprinting() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public void setSprinting(boolean sprinting) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public boolean isSwimming() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public boolean isInSwimmingPose() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    @Environment(EnvType.CLIENT)
    public boolean shouldLeaveSwimmingPose() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public void setSwimming(boolean swimming) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public boolean isGlowing() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public void setGlowing(boolean glowing) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public boolean isInvisible() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    @Environment(EnvType.CLIENT)
    public boolean isInvisibleTo(PlayerEntity player) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    @Nullable
    public AbstractTeam getScoreboardTeam() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public boolean isTeammate(Entity other) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public boolean isTeamPlayer(AbstractTeam team) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public void setInvisible(boolean invisible) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public boolean getFlag(int index) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public void setFlag(int index, boolean value) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public int getMaxAir() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public int getAir() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public void setAir(int air) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public void onStruckByLightning(ServerWorld world, LightningEntity lightning) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public void onBubbleColumnSurfaceCollision(boolean drag) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public void onBubbleColumnCollision(boolean drag) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public void onKilledOther(ServerWorld serverWorld, LivingEntity livingEntity) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    protected void pushOutOfBlocks(double x, double y, double z) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public void slowMovement(BlockState state, Vec3d multiplier) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    private static Text removeClickEvents(Text textComponent) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public Text getName() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    protected Text getDefaultName() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public boolean isPartOf(Entity entity) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public float getHeadYaw() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public void setHeadYaw(float headYaw) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public void setYaw(float yaw) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public boolean isAttackable() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public boolean handleAttack(Entity attacker) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public String toString() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public boolean isInvulnerableTo(DamageSource damageSource) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public boolean isInvulnerable() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public void setInvulnerable(boolean invulnerable) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public void copyPositionAndRotation(Entity entity) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public void copyFrom(Entity original) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    @Nullable
    public Entity moveToWorld(ServerWorld destination) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    protected void method_30076() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    @Nullable
    protected TeleportTarget getTeleportTarget(ServerWorld destination) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    protected Vec3d method_30633(Direction.Axis axis, class_5459.class_5460 arg) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    protected Optional<class_5459.class_5460> method_30330(ServerWorld serverWorld, BlockPos blockPos, boolean bl) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public boolean canUsePortals() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public float getEffectiveExplosionResistance(Explosion explosion, BlockView world, BlockPos pos, BlockState blockState, FluidState fluidState, float max) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public boolean canExplosionDestroyBlock(Explosion explosion, BlockView world, BlockPos pos, BlockState state, float explosionPower) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public int getSafeFallDistance() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public boolean canAvoidTraps() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public void populateCrashReport(CrashReportSection section) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    @Environment(EnvType.CLIENT)
    public boolean doesRenderOnFire() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public void setUuid(UUID uuid) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public UUID getUuid() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public String getUuidAsString() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public String getEntityName() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public boolean canFly() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    @Environment(EnvType.CLIENT)
    public static double getRenderDistanceMultiplier() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    @Environment(EnvType.CLIENT)
    public static void setRenderDistanceMultiplier(double value) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public Text getDisplayName() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public void setCustomName(@Nullable Text name) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    @Nullable
    public Text getCustomName() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public boolean hasCustomName() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public void setCustomNameVisible(boolean visible) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public boolean isCustomNameVisible() {
        throw new UnsupportedOperationException();
    }

    @Final
    @Shadow
    public final void teleport(double destX, double destY, double destZ) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public void requestTeleport(double destX, double destY, double destZ) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    @Environment(EnvType.CLIENT)
    public boolean shouldRenderName() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public void onTrackedDataSet(TrackedData<?> data) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public void calculateDimensions() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public Direction getHorizontalFacing() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public Direction getMovementDirection() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    protected HoverEvent getHoverEvent() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public boolean canBeSpectated(ServerPlayerEntity spectator) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public Box getBoundingBox() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    @Environment(EnvType.CLIENT)
    public Box getVisibilityBoundingBox() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    protected Box calculateBoundsForPose(EntityPose pos) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public void setBoundingBox(Box boundingBox) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    protected float getEyeHeight(EntityPose pose, EntityDimensions dimensions) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    @Environment(EnvType.CLIENT)
    public float getEyeHeight(EntityPose pose) {
        throw new UnsupportedOperationException();
    }

    @Final
    @Shadow
    public final float getStandingEyeHeight() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    @Environment(EnvType.CLIENT)
    public Vec3d method_29919() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public boolean equip(int slot, ItemStack item) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public void sendSystemMessage(Text message, UUID senderUuid) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public World getEntityWorld() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    @Nullable
    public MinecraftServer getServer() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public ActionResult interactAt(PlayerEntity player, Vec3d hitPos, Hand hand) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public boolean isImmuneToExplosion() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public void dealDamage(LivingEntity attacker, Entity target) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public void onStartedTrackingBy(ServerPlayerEntity player) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public void onStoppedTrackingBy(ServerPlayerEntity player) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public float applyRotation(BlockRotation rotation) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public float applyMirror(BlockMirror mirror) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public boolean entityDataRequiresOperator() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public boolean teleportRequested() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public boolean isChunkPosUpdateRequested() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    @Nullable
    public Entity getPrimaryPassenger() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public List<Entity> getPassengerList() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public boolean hasPassenger(Entity passenger) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public boolean hasPassengerType(Class<? extends Entity> clazz) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public Collection<Entity> getPassengersDeep() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public Stream<Entity> streamPassengersRecursively() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public boolean hasPlayerRider() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    private void collectPassengers(boolean playersOnly, Set<Entity> output) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public Entity getRootVehicle() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public boolean isConnectedThroughVehicle(Entity entity) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    @Environment(EnvType.CLIENT)
    public boolean hasPassengerDeep(Entity passenger) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public boolean isLogicalSideForUpdatingMovement() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    protected static Vec3d getPassengerDismountOffset(double vehicleWidth, double passengerWidth, float passengerYaw) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public Vec3d updatePassengerForDismount(LivingEntity passenger) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    @Nullable
    public Entity getVehicle() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public PistonBehavior getPistonBehavior() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public SoundCategory getSoundCategory() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public int getBurningDuration() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public ServerCommandSource getCommandSource() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    protected int getPermissionLevel() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public boolean hasPermissionLevel(int permissionLevel) {
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
    public boolean shouldBroadcastConsoleToOps() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public void lookAt(EntityAnchorArgumentType.EntityAnchor anchorPoint, Vec3d target) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public boolean updateMovementInFluid(Tag<Fluid> tag, double d) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public double getFluidHeight(Tag<Fluid> fluid) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public double method_29241() {
        throw new UnsupportedOperationException();
    }

    @Final
    @Shadow
    public final float getWidth() {
        throw new UnsupportedOperationException();
    }

    @Final
    @Shadow
    public final float getHeight() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public abstract Packet<?> createSpawnPacket();

    @Shadow
    public EntityDimensions getDimensions(EntityPose pose) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public Vec3d getPos() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public BlockPos getBlockPos() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public Vec3d getVelocity() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public void setVelocity(Vec3d velocity) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public void setVelocity(double x, double y, double z) {
        throw new UnsupportedOperationException();
    }

    @Final
    @Shadow
    public final double getX() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public double offsetX(double widthScale) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public double getParticleX(double widthScale) {
        throw new UnsupportedOperationException();
    }

    @Final
    @Shadow
    public final double getY() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public double getBodyY(double heightScale) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public double getRandomBodyY() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public double getEyeY() {
        throw new UnsupportedOperationException();
    }

    @Final
    @Shadow
    public final double getZ() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public double offsetZ(double widthScale) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public double getParticleZ(double widthScale) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public void setPos(double x, double y, double z) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public void checkDespawn() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    @Environment(EnvType.CLIENT)
    public Vec3d method_30951(float f) {
        throw new UnsupportedOperationException();
    }
}
