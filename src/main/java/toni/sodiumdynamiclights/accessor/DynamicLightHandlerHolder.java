/*
 * Copyright © 2021 LambdAurora <email@lambdaurora.dev>
 *
 * This file is part of SodiumDynamicLights.
 *
 * Licensed under the MIT License. For more information,
 * see the LICENSE file.
 */

package toni.sodiumdynamiclights.accessor;

import dev.lambdaurora.lambdynlights.api.DynamicLightHandler;
import net.minecraft.util.text.TextComponentString;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

@ApiStatus.Internal
@ApiStatus.NonExtendable
public interface DynamicLightHandlerHolder<T> {
	@Nullable DynamicLightHandler<T> sodiumdynamiclights$getDynamicLightHandler();

	void sodiumdynamiclights$setDynamicLightHandler(DynamicLightHandler<T> handler);

	boolean sodiumdynamiclights$getSetting();

	TextComponentString sodiumdynamiclights$getName();

	Map<Class<?>, DynamicLightHandlerHolder<?>> REGISTRY = new HashMap<>();

	@SuppressWarnings("unchecked")
	static <T> void register(Class<T> clazz, DynamicLightHandlerHolder<T> handler) {
		REGISTRY.put(clazz, handler);
	}

	@SuppressWarnings("unchecked")
	static <T> @Nullable DynamicLightHandlerHolder<T> get(Class<T> clazz) {
		return (DynamicLightHandlerHolder<T>) REGISTRY.get(clazz);
	}
}
