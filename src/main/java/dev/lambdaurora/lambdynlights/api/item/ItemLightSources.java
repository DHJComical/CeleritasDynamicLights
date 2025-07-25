/*
 * Copyright © 2020 LambdAurora <email@lambdaurora.dev>
 *
 * This file is part of SodiumDynamicLights.
 *
 * Licensed under the MIT License. For more information,
 * see the LICENSE file.
 */

package dev.lambdaurora.lambdynlights.api.item;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.block.Block;
import net.minecraft.block.BlockTorch;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import toni.sodiumdynamiclights.SodiumDynamicLights;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

/**
 * Represents an item light sources manager.
 *
 * @author LambdAurora
 * @version 2.3.2
 * @since 1.3.0
 */
public final class ItemLightSources {
    private static final Map<Item, ItemLightSource> ITEM_LIGHT_SOURCES = new Reference2ObjectOpenHashMap<>();
    private static final Map<Item, ItemLightSource> STATIC_ITEM_LIGHT_SOURCES = new Reference2ObjectOpenHashMap<>();

    private static final String[] itemJsons = {
            "blaze_powder.json",
            "blaze_rod.json",
            "fire_charge.json",
            "glowstone_dust.json",
            "lava_bucket.json",
            "nether_star.json",
            "prismarine_crystals.json",
            "redstone_torch.json",
            "spectral_arrow.json",
            "torch.json"
    };

    private ItemLightSources() {
        throw new UnsupportedOperationException("ItemLightSources only contains static definitions.");
    }

    /**
     * Loads the item light source data from resource pack.
     *
     * @param resourceManager The resource manager.
     */
    public static void load(IResourceManager resourceManager) {
        ITEM_LIGHT_SOURCES.clear();

        for (String filename : itemJsons) {
            ResourceLocation location = new ResourceLocation("sodiumdynamiclights", "dynamiclights/item/" + filename);
            try {
                for (IResource resource : resourceManager.getAllResources(location)) {
                    load(location, resource);
                }
            } catch (IOException e) {
                SodiumDynamicLights.get().warn("Failed to load " + location);
            }
        }

        ITEM_LIGHT_SOURCES.putAll(STATIC_ITEM_LIGHT_SOURCES);
    }

    private static void load(ResourceLocation resourceId, IResource resource) {
        ResourceLocation id = new ResourceLocation(resourceId.getNamespace(), resourceId.getPath().replace(".json", "")
        );

        try (InputStreamReader reader = new InputStreamReader(resource.getInputStream())) {
            JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();

            ItemLightSource.fromJson(id, json).ifPresent(data -> {
                if (!STATIC_ITEM_LIGHT_SOURCES.containsKey(data.item()))
                    register(data);
            });
        } catch (IOException | IllegalStateException e) {
            SodiumDynamicLights.get().warn("Failed to load item light source \"" + id + "\".");
        }
    }

    /**
     * Registers an item light source data.
     *
     * @param data The item light source data.
     */
    private static void register(ItemLightSource data) {
        var other = ITEM_LIGHT_SOURCES.get(data.item());

        if (other != null) {
            SodiumDynamicLights.get().warn("Failed to register item light source \"" + data.id() + "\", duplicates item \""
                    + Item.REGISTRY.getNameForObject(data.item()) + "\" found in \"" + other.id() + "\".");
            return;
        }

        ITEM_LIGHT_SOURCES.put(data.item(), data);
    }

    /**
     * Registers an item light source data.
     *
     * @param data the item light source data
     */
    public static void registerItemLightSource(ItemLightSource data) {
        var other = STATIC_ITEM_LIGHT_SOURCES.get(data.item());

        if (other != null) {
            SodiumDynamicLights.get().warn("Failed to register item light source \"" + data.id() + "\", duplicates item \""
                    + Item.REGISTRY.getNameForObject(data.item()) + "\" found in \"" + other.id() + "\".");
            return;
        }

        STATIC_ITEM_LIGHT_SOURCES.put(data.item(), data);
    }

    /**
     * Returns the luminance of the item in the stack.
     *
     * @param stack            the item stack
     * @param submergedInWater {@code true} if the stack is submerged in water, else {@code false}
     * @return a luminance value
     */
    public static int getLuminance(ItemStack stack, boolean submergedInWater) {
        ItemLightSource data = ITEM_LIGHT_SOURCES.get(stack.getItem());

        if (data != null) {
            return data.getLuminance(stack, submergedInWater);
        } else if (stack.getItem() instanceof ItemBlock blockItem) {
            return ItemLightSource.BlockItemLightSource.getLuminance(stack, blockItem.getBlock().getDefaultState());
        } else {
            return 0;
        }
    }
}
