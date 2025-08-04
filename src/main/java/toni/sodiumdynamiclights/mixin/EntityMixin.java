package toni.sodiumdynamiclights.mixin;

import dev.lambdaurora.lambdynlights.api.DynamicLightHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import toni.sodiumdynamiclights.accessor.DynamicLightHandlerHolder;

import java.util.HashMap;

@Mixin(Entity.class)
public class EntityMixin implements DynamicLightHandlerHolder<Entity> {
	@Unique
	private DynamicLightHandler<Entity> sodiumdynamiclights$lightHandler;
	@Unique
	private Boolean sodiumdynamiclights$setting;

	@Override
	public DynamicLightHandler<Entity> sodiumdynamiclights$getDynamicLightHandler() {
		return this.sodiumdynamiclights$lightHandler;
	}

	@Override
	public void sodiumdynamiclights$setDynamicLightHandler(DynamicLightHandler<Entity> handler) {
		this.sodiumdynamiclights$lightHandler = handler;
	}

	@Override
	public boolean sodiumdynamiclights$getSetting() {
		if (this.sodiumdynamiclights$setting == null) {
			Entity entity = (Entity) (Object) this;

			// Get entity registry name as ResourceLocation string
			ResourceLocation id = EntityList.getKey(entity);

			if (id == null) {
				// fallback, disable dynamic light for unknown entity?
				this.sodiumdynamiclights$setting = false;
				return false;
			}

			if (id.getNamespace().equals("minecraft") && id.getPath().equals("pig") && entity.getClass() != EntityPig.class) {
				this.sodiumdynamiclights$setting = false;
				return false;
			}

			String path = "light_sources.settings.entities." + id.getNamespace() + '.' + id.getPath().replace('/', '.');

			HashMap<String, Boolean> config = new HashMap<>();

			this.sodiumdynamiclights$setting = config.getOrDefault(path, false);
		}
		return this.sodiumdynamiclights$setting;
	}

	@Override
	public TextComponentString sodiumdynamiclights$getName() {
		Entity entity = (Entity) (Object) this;
		ITextComponent name = entity.getDisplayName();

		if (name == null) {
			return new TextComponentString("sodiumdynamiclights.dummy");
		}

		return (TextComponentString) name;
	}
}

