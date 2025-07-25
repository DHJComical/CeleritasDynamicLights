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
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import toni.sodiumdynamiclights.DynamicLightSource;
import toni.sodiumdynamiclights.config.DynamicLightsConfig;
import toni.sodiumdynamiclights.SodiumDynamicLights;
import dev.lambdaurora.lambdynlights.api.DynamicLightHandlers;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import net.minecraft.client.Minecraft;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class EntityMixin implements DynamicLightSource {
    @Shadow
    public World world;

    @Shadow
    public double posX;

    @Shadow
    public double posY;

    @Shadow
    public double posZ;

    @Shadow
    public abstract boolean isBurning();

    @Shadow
    public boolean isDead;

    @Shadow
    public abstract float getEyeHeight();

    @Shadow
    public int chunkCoordX;

    @Shadow
    public int chunkCoordY;

    @Shadow
    public int chunkCoordZ;

    @Shadow
    public abstract String getName();

    @Unique
    protected int sodiumdynamiclights$luminance = 0;
    @Unique
    private int sodiumdynamiclights$lastLuminance = 0;
    @Unique
    private long sodiumdynamiclights$lastUpdate = 0;
    @Unique
    private double sodiumdynamiclights$prevX;
    @Unique
    private double sodiumdynamiclights$prevY;
    @Unique
    private double sodiumdynamiclights$prevZ;
    @Unique
    private LongOpenHashSet sodiumdynamiclights$trackedLitChunkPos = new LongOpenHashSet();

    @Inject(method = "onEntityUpdate", at = @At("TAIL"))
    public void onTick(CallbackInfo ci) {
        if (this.world.isRemote) {
            if (this.isDead) {
                this.sdl$setDynamicLightEnabled(false);
            } else {
                this.sdl$dynamicLightTick();

                if (!DynamicLightsConfig.entitiesLightSource) {
                    if (!(((Entity) (Object) this) instanceof EntityPlayer)) {
                        this.sodiumdynamiclights$luminance = 0;
                    }
                }

                if (!DynamicLightHandlers.canEntityLightUp((Entity) (Object) this)) {
                    this.sodiumdynamiclights$luminance = 0;
                }

                SodiumDynamicLights.updateTracking(this);
            }
        }
    }

    @Inject(method = "getBrightnessForRender",at = @At("RETURN"), cancellable = true)
        private void ongetBrightnessForRender(CallbackInfoReturnable<Integer> cir){
        int original = cir.getReturnValue();

        int sky = original >> 20;
        int block = (original >> 4) & 0xFFFF;

        int dynamic = (int) SodiumDynamicLights.get().getDynamicLightLevel(new BlockPos(posX,posY + getEyeHeight(),posZ));
        if (dynamic > block) {
            block = dynamic;
        }

        int result = (sky << 20) | (block << 4);
        cir.setReturnValue(result);
    }

    @Inject(method = "onRemovedFromWorld", at = @At("TAIL"))
    public void onRemove(CallbackInfo ci) {
        if (this.world.isRemote)
            this.sdl$setDynamicLightEnabled(false);
    }

    @Override
    public double sdl$getDynamicLightX() {
        return this.posX;
    }

    @Override
    public double sdl$getDynamicLightY() {
        return this.posY + this.getEyeHeight();
    }

    @Override
    public double sdl$getDynamicLightZ() {
        return this.posZ;
    }

    @Override
    public World sdl$getDynamicLightLevel() {
        return this.world;
    }

    @Override
    public void sdl$resetDynamicLight() {
        this.sodiumdynamiclights$lastLuminance = 0;
    }

    @Override
    public boolean sdl$shouldUpdateDynamicLight() {
        var mode = DynamicLightsConfig.dynamicLightsMode;
        if (!mode.isEnabled())
            return false;
        if (mode.hasDelay()) {
            long currentTime = System.currentTimeMillis();
            if (currentTime < this.sodiumdynamiclights$lastUpdate + mode.getDelay()) {
                return false;
            }

            this.sodiumdynamiclights$lastUpdate = currentTime;
        }
        return true;
    }

    @Override
    public void sdl$dynamicLightTick() {
        if(this.isBurning()){
            this.sodiumdynamiclights$luminance = 14;
        }
        else{
            this.sodiumdynamiclights$luminance = 0;
        }

        int luminance = DynamicLightHandlers.getLuminanceFrom((Entity) (Object) this);
        if (luminance > this.sodiumdynamiclights$luminance)
            this.sodiumdynamiclights$luminance = luminance;
    }

    @Override
    public int sdl$getLuminance() {
        return this.sodiumdynamiclights$luminance;
    }

    @Override
    public boolean sodiumdynamiclights$updateDynamicLight(@NotNull RenderGlobal renderer) {
        if (!this.sdl$shouldUpdateDynamicLight())
            return false;

        double deltaX = this.posX - this.sodiumdynamiclights$prevX;
        double deltaY = this.posY - this.sodiumdynamiclights$prevY;
        double deltaZ = this.posZ - this.sodiumdynamiclights$prevZ;

        int luminance = this.sdl$getLuminance();

        if (Math.abs(deltaX) > 0.1D || Math.abs(deltaY) > 0.1D || Math.abs(deltaZ) > 0.1D || luminance != this.sodiumdynamiclights$lastLuminance) {
            this.sodiumdynamiclights$prevX = this.posX;
            this.sodiumdynamiclights$prevY = this.posY;
            this.sodiumdynamiclights$prevZ = this.posZ;
            this.sodiumdynamiclights$lastLuminance = luminance;

            var newPos = new LongOpenHashSet();

            if (luminance > 0) {
                double eyeY = this.posY + this.getEyeHeight();
                int chunkCoordYEyePos = (int) Math.floor(eyeY) >> 4;
                BlockPos.MutableBlockPos chunkPos = new BlockPos.MutableBlockPos(this.chunkCoordX, chunkCoordYEyePos, this.chunkCoordZ);

                SodiumDynamicLights.scheduleChunkRebuild(renderer, chunkPos);
                SodiumDynamicLights.updateTrackedChunks(chunkPos, this.sodiumdynamiclights$trackedLitChunkPos, newPos);

                var directionX = ((int) this.posX & 15) >= 8 ? EnumFacing.EAST : EnumFacing.WEST;
                var directionY = ((int) eyeY & 15) >= 8 ? EnumFacing.UP : EnumFacing.DOWN;
                var directionZ = ((int) this.posZ & 15) >= 8 ? EnumFacing.SOUTH : EnumFacing.NORTH;

                for (int i = 0; i < 7; i++) {
                    if (i % 4 == 0) {
                        chunkPos.move(directionX);
                    } else if (i % 4 == 1) {
                        chunkPos.move(directionZ);
                    } else if (i % 4 == 2) {
                        chunkPos.move(directionX.getOpposite());
                    } else {
                        chunkPos.move(directionZ.getOpposite());
                        chunkPos.move(directionY);
                    }
                    SodiumDynamicLights.scheduleChunkRebuild(renderer, chunkPos);
                    SodiumDynamicLights.updateTrackedChunks(chunkPos, this.sodiumdynamiclights$trackedLitChunkPos, newPos);
                }
            }
            this.sodiumdynamiclights$scheduleTrackedChunksRebuild(renderer);
            this.sodiumdynamiclights$trackedLitChunkPos = newPos;
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
