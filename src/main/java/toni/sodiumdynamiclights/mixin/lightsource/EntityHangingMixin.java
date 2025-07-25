package toni.sodiumdynamiclights.mixin.lightsource;

import dev.lambdaurora.lambdynlights.api.DynamicLightHandlers;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityHanging;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import toni.sodiumdynamiclights.DynamicLightSource;
import toni.sodiumdynamiclights.SodiumDynamicLights;
import toni.sodiumdynamiclights.config.DynamicLightsConfig;

@Mixin(EntityHanging.class)
public abstract class EntityHangingMixin extends Entity implements DynamicLightSource {

    public EntityHangingMixin(World worldIn) {
        super(worldIn);
    }

    @Inject(method = "onUpdate", at = @At("TAIL"))
    public void onUpdate(CallbackInfo ci) {
        if (this.world.isRemote) {
            if (this.isDead) {
                this.sdl$setDynamicLightEnabled(false);
            } else {
                if (!DynamicLightsConfig.entitiesLightSource || !DynamicLightHandlers.canEntityLightUp(this))
                    this.sdl$resetDynamicLight();
                else
                    this.sdl$dynamicLightTick();
                SodiumDynamicLights.updateTracking(this);
            }
        }
    }
}
