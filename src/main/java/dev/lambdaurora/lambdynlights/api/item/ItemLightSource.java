/*
 * Copyright © 2020 LambdAurora <email@lambdaurora.dev>
 *
 * This file is part of LambDynamicLights.
 *
 * Licensed under the MIT License. For more information,
 * see the LICENSE file.
 */

package dev.lambdaurora.lambdynlights.api.item;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import toni.sodiumdynamiclights.config.DynamicLightsConfig;
import toni.sodiumdynamiclights.SodiumDynamicLights;

import java.util.Optional;

/**
 * Represents an item light source.
 *
 * @author LambdAurora
 * @version 3.0.0
 * @since 1.3.0
 */
public abstract class ItemLightSource {
	private final ResourceLocation id;
	private final Item item;
	private final boolean waterSensitive;

	public ItemLightSource(ResourceLocation id, Item item, boolean waterSensitive) {
		this.id = id;
		this.item = item;
		this.waterSensitive = waterSensitive;
	}

	public ItemLightSource(ResourceLocation id, Item item) {
		this(id, item, false);
	}

	public ResourceLocation id() {
		return this.id;
	}

	public Item item() {
		return this.item;
	}

	public boolean waterSensitive() {
		return this.waterSensitive;
	}

	/**
	 * Gets the luminance of the item.
	 *
	 * @param stack the item stack
	 * @param submergedInWater {@code true} if submerged in water, else {@code false}.
	 * @return the luminance value between {@code 0} and {@code 15}
	 */
	public int getLuminance(ItemStack stack, boolean submergedInWater) {
		if (this.waterSensitive() && DynamicLightsConfig.waterSensitiveCheck && submergedInWater)
			return 0; // Don't emit light with water sensitive items while submerged in water.

		return this.getLuminance(stack);
	}

	/**
	 * Gets the luminance of the item.
	 *
	 * @param stack the item stack
	 * @return the luminance value between {@code 0} and {@code 15}
	 */
	public abstract int getLuminance(ItemStack stack);

	@Override
	public String toString() {
		return "ItemLightSource{" +
				"id=" + this.id() +
				"item=" + this.item() +
				", water_sensitive=" + this.waterSensitive() +
				'}';
	}

	public static Optional<ItemLightSource> fromJson(ResourceLocation id, JsonObject json) {
		if (!json.has("item") || !json.has("luminance")) {
			SodiumDynamicLights.get().warn("Failed to parse item light source \"" + id + "\", invalid format: missing required fields.");
			return Optional.empty();
		}

		Item item;
		try {
			ResourceLocation affectId = new ResourceLocation(json.get("item").getAsString());
			item = ForgeRegistries.ITEMS.getValue(affectId);
		} catch (Exception e) {
			SodiumDynamicLights.get().warn("Failed to parse item ID for \"" + id + "\".");
			return Optional.empty();
		}

		if (item == null || item == Items.AIR)
			return Optional.empty();

		boolean waterSensitive = false;
		if (json.has("water_sensitive"))
			waterSensitive = json.get("water_sensitive").getAsBoolean();

		JsonPrimitive luminanceElement = json.getAsJsonPrimitive("luminance");

		if (luminanceElement.isNumber()) {
			return Optional.of(new StaticItemLightSource(id, item, luminanceElement.getAsInt(), waterSensitive));
		} else if (luminanceElement.isString()) {
			String luminanceStr = luminanceElement.getAsString();
			if (luminanceStr.equals("block")) {
				if (item instanceof ItemBlock) {
					ItemBlock blockItem = (ItemBlock) item;
					return Optional.of(new BlockItemLightSource(id, item, blockItem.getBlock().getDefaultState(), waterSensitive));
				}
			} else {
				try {
					ResourceLocation blockId = new ResourceLocation(luminanceStr);
					net.minecraft.block.Block block = ForgeRegistries.BLOCKS.getValue(blockId);
					if (block != null && block != Blocks.AIR) {
						return Optional.of(new BlockItemLightSource(id, item, block.getDefaultState(), waterSensitive));
					}
				} catch (Exception e) {
					SodiumDynamicLights.get().warn("Invalid block ID in luminance: \"" + luminanceStr + "\"");
				}
			}
		} else {
			SodiumDynamicLights.get().warn("Failed to parse item light source \"" + id + "\", invalid format: \"luminance\" field is not a string or number.");
		}

		return Optional.empty();
	}

	public static class StaticItemLightSource extends ItemLightSource {
		private final int luminance;

		public StaticItemLightSource(ResourceLocation id, Item item, int luminance, boolean waterSensitive) {
			super(id, item, waterSensitive);
			this.luminance = luminance;
		}

		public StaticItemLightSource(ResourceLocation id, Item item, int luminance) {
			super(id, item);
			this.luminance = luminance;
		}

		@Override
		public int getLuminance(ItemStack stack) {
			return this.luminance;
		}
	}

	public static class BlockItemLightSource extends ItemLightSource {
		private final IBlockState mimic;

		public BlockItemLightSource(ResourceLocation id, Item item, IBlockState block, boolean waterSensitive) {
			super(id, item, waterSensitive);
			this.mimic = block;
		}

		@Override
		public int getLuminance(ItemStack stack) {
			return getLuminance(stack, this.mimic);
		}

		static int getLuminance(ItemStack stack, IBlockState state) {
			NBTTagCompound nbt = stack.getTagCompound();
			if (nbt != null && nbt.hasKey("BlockStateTag", 10)) {
				NBTTagCompound blockStateTag = nbt.getCompoundTag("BlockStateTag");

				for (String key : blockStateTag.getKeySet()) {
					IProperty<?> property = state.getPropertyKeys().stream()
							.filter(p -> p.getName().equals(key))
							.findFirst()
							.orElse(null);

					if (property != null) {
						String value = blockStateTag.getString(key);
						state = updateState(state, property, value);
					}
				}
			}

			return state.getLightValue();
		}

		private static <T extends Comparable<T>> IBlockState updateState(IBlockState state, IProperty<T> property, String valueStr) {
			for (T value : property.getAllowedValues()) {
				if (value.toString().equalsIgnoreCase(valueStr)) {
					return state.withProperty(property, value);
				}
			}
			return state;
		}
	}
}
