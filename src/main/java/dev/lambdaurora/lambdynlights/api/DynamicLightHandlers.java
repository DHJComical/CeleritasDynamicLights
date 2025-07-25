/*
 * Copyright © 2020 LambdAurora <email@lambdaurora.dev>
 *
 * This file is part of SodiumDynamicLights.
 *
 * Licensed under the MIT License. For more information,
 * see the LICENSE file.
 */

package dev.lambdaurora.lambdynlights.api;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityMagmaCube;
import net.minecraft.entity.projectile.EntitySpectralArrow;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import toni.sodiumdynamiclights.config.DynamicLightsConfig;
import toni.sodiumdynamiclights.SodiumDynamicLights;
import toni.sodiumdynamiclights.accessor.DynamicLightHandlerHolder;
import net.minecraft.client.Minecraft;

import java.util.HashMap;
import java.util.Map;

/**
 * @author LambdAurora
 * @version 2.3.0
 * @since 1.1.0
 */
public final class DynamicLightHandlers {
	private DynamicLightHandlers() {
		throw new UnsupportedOperationException("DynamicLightHandlers only contains static definitions.");
	}

	private static final Map<Class<? extends Entity>, DynamicLightHandler<?>> ENTITY_LIGHT_HANDLERS = new HashMap<>();
	private static final Map<Class<? extends TileEntity>, DynamicLightHandler<?>> TILE_ENTITY_LIGHT_HANDLERS = new HashMap<>();


	/**
	 * Registers the default handlers.
	 */

	public static void registerDefaultHandlers() {
		registerEntityDynamicLightHandler(EntityBlaze.class, DynamicLightHandler.makeHandler(blaze -> 10, blaze -> true));
		registerEntityDynamicLightHandler(EntityCreeper.class, DynamicLightHandler.makeCreeperEntityHandler(null));
		registerEntityDynamicLightHandler(EntityEnderman.class, entity -> {
			int luminance = 0;
			if (entity.getHeldBlockState() != null)
				luminance = entity.getHeldBlockState().getLightValue();
			return luminance;
		});
		registerEntityDynamicLightHandler(EntityItem.class, entity -> SodiumDynamicLights.getLuminanceFromItemStack(entity.getItem(), entity.isOverWater()));
		registerEntityDynamicLightHandler(EntityItemFrame.class, entity -> {
            BlockPos pos = new BlockPos(entity.posX, entity.posY + entity.getEyeHeight(), entity.posZ);
			boolean isSubmerged = entity.world.getBlockState(pos).getMaterial().isLiquid();
			return SodiumDynamicLights.getLuminanceFromItemStack(entity.getDisplayedItem(), isSubmerged);
		});
		registerEntityDynamicLightHandler(EntityMagmaCube.class, entity -> (entity.squishFactor > 0.6) ? 11 : 8);
		registerEntityDynamicLightHandler(EntitySpectralArrow.class, entity -> 8);
	}

	/**
	 * Registers an entity dynamic light handler.
	 *
	 * @param entityClass the entity type
	 * @param handler the dynamic light handler
	 * @param <T> the type of the entity
	 */
	public static <T extends Entity> void registerEntityDynamicLightHandler(Class<T> entityClass, DynamicLightHandler<T> handler) {
		DynamicLightHandler<?> existingHandler = ENTITY_LIGHT_HANDLERS.get(entityClass);

		if (existingHandler != null) {
			DynamicLightHandler<T> combinedHandler = entity -> {
				int maxLuminance = Math.max(
						((DynamicLightHandler<T>) existingHandler).getLuminance(entity),
						handler.getLuminance(entity)
				);
				if (maxLuminance >= 15) {
					maxLuminance = 14;
				}
				return maxLuminance;
			};
			ENTITY_LIGHT_HANDLERS.put(entityClass, combinedHandler);
		} else {
			DynamicLightHandler<T> clampedHandler = entity -> {
				int luminance = handler.getLuminance(entity);
				if (luminance >= 15) {
					luminance = 14;
				}
				return luminance;
			};
			ENTITY_LIGHT_HANDLERS.put(entityClass, clampedHandler);
		}
	}


	/**
	 * Registers a block entity dynamic light handler.
	 *
	 * @param tileEntityClass the block entity type
	 * @param handler the dynamic light handler
	 * @param <T> the type of the block entity
	 */
	@SuppressWarnings("unchecked")
	public static <T extends TileEntity> void registerTileEntityDynamicLightHandler(Class<T> tileEntityClass, DynamicLightHandler<T> handler) {
		DynamicLightHandler<?> existingHandler = TILE_ENTITY_LIGHT_HANDLERS.get(tileEntityClass);

		if (existingHandler != null) {
			DynamicLightHandler<T> combinedHandler = entity -> {
				int maxLuminance = Math.max(
						((DynamicLightHandler<T>) existingHandler).getLuminance(entity),
						handler.getLuminance(entity)
				);
				if (maxLuminance >= 15) {
					maxLuminance = 14;
				}
				return maxLuminance;
			};
			TILE_ENTITY_LIGHT_HANDLERS.put(tileEntityClass, combinedHandler);
		} else {
			DynamicLightHandler<T> clampedHandler = entity -> {
				int luminance = handler.getLuminance(entity);
				if (luminance >= 15) {
					luminance = 14;
				}
				return luminance;
			};
			TILE_ENTITY_LIGHT_HANDLERS.put(tileEntityClass, clampedHandler);
		}
	}

	/**
	 * Returns the registered dynamic light handler of the specified entity.
	 *
	 * @param entity the entity type
	 * @param <T> the type of the entity
	 * @return the registered dynamic light handler
	 */
	public static <T extends Entity> DynamicLightHandler<T> getDynamicLightHandler(T entity) {
		Class<?> clazz = entity.getClass();
		DynamicLightHandler<?> handler = ENTITY_LIGHT_HANDLERS.get(clazz);
		return (DynamicLightHandler<T>) handler;
	}

	/**
	 * Returns the registered dynamic light handler of the specified block entity.
	 *
	 * @param tileEntity the block entity type
	 * @param <T> the type of the block entity
	 * @return the registered dynamic light handler
	 */
	public static <T extends TileEntity> DynamicLightHandler<T> getDynamicLightHandler(T tileEntity) {
		Class<?> clazz = tileEntity.getClass();
		DynamicLightHandler<?> handler = TILE_ENTITY_LIGHT_HANDLERS.get(clazz);
		return (DynamicLightHandler<T>) handler;
	}

	/**
	 * Returns whether the given entity can light up.
	 *
	 * @param entity the entity
	 * @param <T> the type of the entity
	 * @return {@code true} if the entity can light up, otherwise {@code false}
	 */
	public static <T extends Entity> boolean canEntityLightUp(T entity) {
		if (entity == Minecraft.getMinecraft().player && !DynamicLightsConfig.selfLightSource)
			return false;

		DynamicLightHandlerHolder<T> handler = DynamicLightHandlerHolder.get((Class<T>) entity.getClass());

		if (handler == null) {
			return true;
		}

		boolean setting = handler.sodiumdynamiclights$getSetting();
		return !setting;
	}

	/**
	 * Returns whether the given block entity can light up.
	 *
	 * @param entity the entity
	 * @param <T> the type of the block entity
	 * @return {@code true} if the block entity can light up, otherwise {@code false}
	 */
	public static <T extends TileEntity> boolean canTileEntityLightUp(T entity) {
		DynamicLightHandlerHolder<T> handler = DynamicLightHandlerHolder.get((Class<T>) entity.getClass());

		if (handler == null) {
			return true;
		}

		boolean setting = handler.sodiumdynamiclights$getSetting();

		return !setting;
	}

	/**
	 * Returns the luminance from an entity.
	 *
	 * @param entity the entity
	 * @param <T> the type of the entity
	 * @return the luminance
	 */
	public static <T extends Entity> int getLuminanceFrom(T entity) {
		if (!DynamicLightsConfig.entitiesLightSource)
			return 0;

		if (entity == Minecraft.getMinecraft().player && !DynamicLightsConfig.selfLightSource)
			return 0;

		var handler = (DynamicLightHandler<T>) getDynamicLightHandler(entity);
		if (handler == null)
			return 0;

		if (!canEntityLightUp(entity))
			return 0;

		if (handler.isWaterSensitive(entity) && entity.getEntityWorld() != null && (entity.isInWater() || entity.isInLava()))
			return 0;

		return handler.getLuminance(entity);
	}

	/**
	 * Returns the luminance from a block entity.
	 *
	 * @param entity the block entity
	 * @param <T> the type of the block entity
	 * @return the luminance
	 */
	public static <T extends TileEntity> int getLuminanceFrom(T entity) {
		if (!DynamicLightsConfig.blockEntitiesLightSource)
			return 0;

		DynamicLightHandler<T> handler = (DynamicLightHandler<T>) getDynamicLightHandler(entity);
		if (handler == null)
			return 0;

		if (!canTileEntityLightUp(entity))
			return 0;

		if (handler.isWaterSensitive(entity) && entity.getWorld() != null && entity.getWorld().getBlockState(entity.getPos()).getMaterial().isLiquid())
			return 0;

		return handler.getLuminance(entity);
	}
}
