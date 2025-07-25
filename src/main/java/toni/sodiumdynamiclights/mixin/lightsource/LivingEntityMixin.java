package toni.sodiumdynamiclights.mixin.lightsource;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import toni.sodiumdynamiclights.DynamicLightSource;
import toni.sodiumdynamiclights.config.DynamicLightsConfig;
import toni.sodiumdynamiclights.SodiumDynamicLights;
import dev.lambdaurora.lambdynlights.api.DynamicLightHandlers;

@Mixin(EntityLiving.class)
public abstract class LivingEntityMixin extends Entity implements DynamicLightSource {
    @Unique
    protected int sodiumdynamiclights$luminance;

    public LivingEntityMixin(World world) {
        super(world);
    }

    @Override
    public void sdl$dynamicLightTick() {
        if (!DynamicLightsConfig.entitiesLightSource || !DynamicLightHandlers.canEntityLightUp(this)) {
            this.sodiumdynamiclights$luminance = 0;
            return;
        }

        if (this.isBurning() || this.isGlowing()) {
            this.sodiumdynamiclights$luminance = 14;
        } else {
            this.sodiumdynamiclights$luminance = SodiumDynamicLights.getLivingEntityLuminanceFromItems((EntityLiving) (Object) this);
        }

        int luminance = DynamicLightHandlers.getLuminanceFrom(this);
        if (luminance > this.sodiumdynamiclights$luminance) {
            this.sodiumdynamiclights$luminance = luminance;
        }
    }

    @Override
    public int sdl$getLuminance() {
        return this.sodiumdynamiclights$luminance;
    }
}
