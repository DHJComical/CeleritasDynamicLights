/*
 * Copyright © 2020 LambdAurora <email@lambdaurora.dev>
 *
 * This file is part of SodiumDynamicLights.
 *
 * Licensed under the MIT License. For more information,
 * see the LICENSE file.
 */

package toni.sodiumdynamiclights.mixin.lightsource;

import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import toni.sodiumdynamiclights.DynamicLightSource;
import toni.sodiumdynamiclights.config.DynamicLightsConfig;
import toni.sodiumdynamiclights.SodiumDynamicLights;
import dev.lambdaurora.lambdynlights.api.DynamicLightHandlers;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import net.minecraft.client.Minecraft;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TileEntity.class)
public abstract class TileEntityMixin implements DynamicLightSource {
    @Shadow
    protected BlockPos pos;

    @Shadow
    @Nullable
    protected World world;

    @Shadow
    protected boolean tileEntityInvalid;

    @Unique
    private int luminance = 0;
    @Unique
    private int lastLuminance = 0;
    @Unique
    private long lastUpdate = 0;
    @Unique
    private final LongOpenHashSet sodiumdynamiclights$trackedLitChunkPos = new LongOpenHashSet();

    @Override
    public double sdl$getDynamicLightX() {
        return this.pos.getX() + 0.5;
    }

    @Override
    public double sdl$getDynamicLightY() {
        return this.pos.getY() + 0.5;
    }

    @Override
    public double sdl$getDynamicLightZ() {
        return this.pos.getZ() + 0.5;
    }

    @Override
    public World sdl$getDynamicLightLevel() {
        return this.world;
    }

    @Inject(method = "invalidate", at = @At("TAIL"))
    private void onRemoved(CallbackInfo ci) {
        this.sdl$setDynamicLightEnabled(false);
    }

    @Override
    public void sdl$resetDynamicLight() {
        this.lastLuminance = 0;
    }

    @Override
    public void sdl$dynamicLightTick() {
        // We do not want to update the entity on the server.
        if (this.world == null || !this.world.isRemote)
            return;
        if (!this.tileEntityInvalid) {
            this.luminance = DynamicLightHandlers.getLuminanceFrom((TileEntity) (Object) this);
            SodiumDynamicLights.updateTracking(this);

            if (!this.sdl$isDynamicLightEnabled()) {
                this.lastLuminance = 0;
            }
        }
    }

    @Override
    public int sdl$getLuminance() {
        return this.luminance;
    }

    @Override
    public boolean sdl$shouldUpdateDynamicLight() {
        var mode = DynamicLightsConfig.dynamicLightsMode;
        if (!mode.isEnabled())
            return false;
        if (mode.hasDelay()) {
            long currentTime = System.currentTimeMillis();
            if (currentTime < this.lastUpdate + mode.getDelay()) {
                return false;
            }

            this.lastUpdate = currentTime;
        }
        return true;
    }

    @Override
    public boolean sodiumdynamiclights$updateDynamicLight(@NotNull RenderGlobal renderer) {
        if (!this.sdl$shouldUpdateDynamicLight())
            return false;

        int luminance = this.sdl$getLuminance();

        if (luminance != this.lastLuminance) {
            this.lastLuminance = luminance;

            if (this.sodiumdynamiclights$trackedLitChunkPos.isEmpty()) {
                var chunkPos = new BlockPos.MutableBlockPos(Math.floorDiv(this.pos.getX(), 16),
                        Math.floorDiv(this.pos.getY(), 16),
                        Math.floorDiv(this.pos.getZ(), 16));

                SodiumDynamicLights.updateTrackedChunks(chunkPos, null, this.sodiumdynamiclights$trackedLitChunkPos);

                double localX = this.pos.getX() - Math.floor(this.pos.getX() / 16.0) * 16.0;
                double localY = this.pos.getY() - Math.floor(this.pos.getY() / 16.0) * 16.0;
                double localZ = this.pos.getZ() - Math.floor(this.pos.getZ() / 16.0) * 16.0;

                EnumFacing directionX = localX >= 8.0 ? EnumFacing.EAST : EnumFacing.WEST;
                EnumFacing directionY = localY >= 8.0 ? EnumFacing.UP : EnumFacing.DOWN;
                EnumFacing directionZ = localZ >= 8.0 ? EnumFacing.SOUTH : EnumFacing.NORTH;

                for (int i = 0; i < 7; i++) {
                    if (i % 4 == 0) {
                        chunkPos.move(directionX); // X
                    } else if (i % 4 == 1) {
                        chunkPos.move(directionZ); // XZ
                    } else if (i % 4 == 2) {
                        chunkPos.move(directionX.getOpposite()); // Z
                    } else {
                        chunkPos.move(directionZ.getOpposite()); // origin
                        chunkPos.move(directionY); // Y
                    }
                    SodiumDynamicLights.updateTrackedChunks(chunkPos, null, this.sodiumdynamiclights$trackedLitChunkPos);
                }
            }

            this.sodiumdynamiclights$scheduleTrackedChunksRebuild(renderer);
            return true;
        }
        return false;
    }

    @Override
    public void sodiumdynamiclights$scheduleTrackedChunksRebuild(@NotNull RenderGlobal renderer) {
        if (Minecraft.getMinecraft().world == this.world)
            for (long pos : this.sodiumdynamiclights$trackedLitChunkPos) {
                SodiumDynamicLights.scheduleChunkRebuild(renderer, pos);
            }
    }
}
