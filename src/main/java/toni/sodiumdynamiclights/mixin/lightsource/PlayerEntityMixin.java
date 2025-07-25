package toni.sodiumdynamiclights.mixin.lightsource;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import toni.sodiumdynamiclights.DynamicLightSource;
import toni.sodiumdynamiclights.SodiumDynamicLights;
import dev.lambdaurora.lambdynlights.api.DynamicLightHandlers;

@Mixin(EntityPlayer.class)
public abstract class PlayerEntityMixin extends EntityLivingBase implements DynamicLightSource {
	@Shadow public abstract boolean isSpectator();

	@Unique
	protected int sodiumdynamiclights$luminance;
	@Unique
	private World sodiumdynamiclights$lastWorld;

	public PlayerEntityMixin(World world) {
		super(world);
	}

	@Override
	public void sdl$dynamicLightTick() {
		if (!DynamicLightHandlers.canEntityLightUp(this)) {
			this.sodiumdynamiclights$luminance = 0;
			return;
		}

		if (this.isBurning() || this.isGlowing()) {
			this.sodiumdynamiclights$luminance = 14;
		} else {
			this.sodiumdynamiclights$luminance = Math.max(
					DynamicLightHandlers.getLuminanceFrom(this),
					SodiumDynamicLights.getLivingEntityLuminanceFromItems(this)
			);
		}

		if (this.isSpectator()) {
			this.sodiumdynamiclights$luminance = 0;
		}

		if (this.sodiumdynamiclights$lastWorld != this.world) {
			this.sodiumdynamiclights$lastWorld = this.world;
			this.sodiumdynamiclights$luminance = 0;
		}
	}

	@Override
	public int sdl$getLuminance() {
		return this.sodiumdynamiclights$luminance;
	}
}
