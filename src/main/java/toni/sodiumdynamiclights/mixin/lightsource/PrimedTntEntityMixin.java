package toni.sodiumdynamiclights.mixin.lightsource;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import toni.sodiumdynamiclights.DynamicLightSource;
import toni.sodiumdynamiclights.config.DynamicLightsConfig;
import toni.sodiumdynamiclights.ExplosiveLightingMode;
import toni.sodiumdynamiclights.SodiumDynamicLights;
import dev.lambdaurora.lambdynlights.api.DynamicLightHandlers;

@Mixin(EntityTNTPrimed.class)
public abstract class PrimedTntEntityMixin extends Entity implements DynamicLightSource {
    @Shadow
    public abstract int getFuse();

    @Unique
    private int startFuseTimer = 80;

    @Unique
    private int sodiumdynamiclights$luminance;

    public PrimedTntEntityMixin(World worldIn) {
        super(worldIn);
    }

    @Inject(method = "<init>(Lnet/minecraft/world/World;DDDLnet/minecraft/entity/EntityLivingBase;)V", at = @At("TAIL"))
    private void onInit(World p_i1730_1, double p_i1730_2, double p_i1730_3, double p_i1730_4, EntityLivingBase p_i1730_5, CallbackInfo ci) {
        this.startFuseTimer = this.getFuse();
    }

    @Inject(method = "onUpdate", at = @At("TAIL"))
    private void onTick(CallbackInfo ci) {
        if (this.world.isRemote) {
            if (!DynamicLightsConfig.tntLightingMode.isEnabled()) {
                return;
            }

            if (this.isDead) {
                this.sdl$setDynamicLightEnabled(false);
            } else {
                if (!DynamicLightsConfig.entitiesLightSource || !DynamicLightHandlers.canEntityLightUp(this)) {
                    this.sdl$resetDynamicLight();
                } else {
                    this.sdl$dynamicLightTick();
                }
                SodiumDynamicLights.updateTracking(this);
            }
        }
    }

    @Override
    public void sdl$dynamicLightTick() {
        if (this.isBurning()) {
            this.sodiumdynamiclights$luminance = 14;
        } else {
            ExplosiveLightingMode lightingMode = DynamicLightsConfig.tntLightingMode;
            if (lightingMode == ExplosiveLightingMode.FANCY) {
                float fuseRatio = (float) this.getFuse() / (float) this.startFuseTimer;
                this.sodiumdynamiclights$luminance = (int) (-(fuseRatio * fuseRatio) * 10.0F) + 10;
            } else {
                this.sodiumdynamiclights$luminance = 10;
            }
        }
    }

    @Override
    public int sdl$getLuminance() {
        return this.sodiumdynamiclights$luminance;
    }
}
