/*
 * Copyright © 2021 LambdAurora <email@lambdaurora.dev>
 *
 * This file is part of SodiumDynamicLights.
 *
 * Licensed under the MIT License. For more information,
 * see the LICENSE file.
 */


package toni.sodiumdynamiclights.mixin;

import toni.sodiumdynamiclights.config.DynamicLightsConfig;
import toni.sodiumdynamiclights.accessor.DynamicLightHandlerHolder;
import dev.lambdaurora.lambdynlights.api.DynamicLightHandler;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentString;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.Map;

@Mixin(value = TileEntity.class)
public class TileEntityTypeMixin implements DynamicLightHandlerHolder<TileEntity> {
	@Unique
	private DynamicLightHandler<TileEntity> sodiumdynamiclights$lightHandler;
	@Unique
	private Boolean sodiumdynamiclights$setting;

	@Override
	public @Nullable DynamicLightHandler<TileEntity> sodiumdynamiclights$getDynamicLightHandler() {
		return this.sodiumdynamiclights$lightHandler;
	}

	@Override
	public void sodiumdynamiclights$setDynamicLightHandler(DynamicLightHandler<TileEntity> handler) {
		this.sodiumdynamiclights$lightHandler = handler;
	}

	@Override
	public boolean sodiumdynamiclights$getSetting() {
		if (this.sodiumdynamiclights$setting == null) {
			TileEntity self = (TileEntity) (Object) this;
			ResourceLocation id = getRegistryName(self);
			if (id == null) {
				return false;
			}

			String path = "light_sources.settings.block_entities." + id.getNamespace() + '.' + id.getPath().replace('/', '.');

			Map<String, Boolean> config = DynamicLightsConfig.ENTITIES_SETTINGS;
			if (!config.containsKey(path)) {
				this.sodiumdynamiclights$setting = false;
				return false;
			}

			this.sodiumdynamiclights$setting = config.getOrDefault(path, false);
		}

		return this.sodiumdynamiclights$setting;
	}

	@Override
	public TextComponentString sodiumdynamiclights$getName() {
		TileEntity self = (TileEntity) (Object) this;
		ResourceLocation id = getRegistryName(self);
		if (id == null) {
			return new TextComponentString("");
		}
		return new TextComponentString(id.getNamespace() + ':' + id.getPath());
	}

	private ResourceLocation getRegistryName(TileEntity tileEntity) {
		return TileEntity.getKey(tileEntity.getClass());
	}
}
