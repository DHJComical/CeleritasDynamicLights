package toni.sodiumdynamiclights.mixin;

import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import toni.sodiumdynamiclights.DynamicLightSource;

@Mixin(WorldClient.class)
public class WorldClientMixin {

	@Inject(method = "removeEntityFromWorld", at = @At("HEAD"))
	private void onRemoveEntityFromWorld(int entityID, CallbackInfoReturnable<Entity> cir) {
		Entity entity = ((WorldClient)(Object)this).getEntityByID(entityID);
		if (entity instanceof DynamicLightSource) {
			((DynamicLightSource) entity).sdl$setDynamicLightEnabled(false);
		}
	}
}