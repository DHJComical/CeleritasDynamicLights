/*
 * Copyright © 2020 LambdAurora <email@lambdaurora.dev>
 *
 * This file is part of SodiumDynamicLights.
 *
 * Licensed under the MIT License. For more information,
 * see the LICENSE file.
 */

package toni.sodiumdynamiclights;

import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Optional;

/**
 * Represents the explosives dynamic lighting mode.
 *
 * @author LambdAurora
 * @version 2.0.1
 * @since 1.2.1
 */
public enum ExplosiveLightingMode {
	OFF(TextFormatting.RED, "sodium.dynamiclights.options.value.off"),
	SIMPLE(TextFormatting.YELLOW, "sodium.dynamiclights.options.value.simple"),
	FANCY(TextFormatting.GREEN, "sodium.dynamiclights.options.value.fancy");

	private final TextComponentTranslation translationComponent;

	ExplosiveLightingMode(@NotNull TextFormatting formatting, @NotNull String translationKey) {
		this.translationComponent = new TextComponentTranslation(translationKey);
		this.translationComponent.getStyle().setColor(formatting);
	}

	/**
	 * Returns whether this mode enables explosives dynamic lighting.
	 *
	 * @return {@code true} if the mode enables explosives dynamic lighting, else {@code false}
	 */
	public boolean isEnabled() {
		return this != OFF;
	}

	/**
	 * Returns the next explosives dynamic lighting mode available.
	 *
	 * @return the next available explosives dynamic lighting mode
	 */
	public ExplosiveLightingMode next() {
		ExplosiveLightingMode[] v = values();
		if (v.length == this.ordinal() + 1)
			return v[0];
		return v[this.ordinal() + 1];
	}

	/**
	 * Returns the translation component of the explosives dynamic lighting mode.
	 *
	 * @return the translation component of the explosives dynamic lighting mode
	 */
	public @NotNull TextComponentTranslation getTranslationComponent() {
		return this.translationComponent;
	}


	/**
	 * Gets the explosives dynamic lighting mode from its ResourceLocation.
	 *
	 * @param id the ResourceLocation of the explosives dynamic lighting mode
	 * @return the explosives dynamic lighting mode if found, else empty
	 */
	public static @NotNull Optional<ExplosiveLightingMode> byId(@NotNull String id) {
		return Arrays.stream(values()).filter(mode -> mode.getName().equalsIgnoreCase(id)).findFirst();
	}

	public @NotNull String getName() {
		return this.name().toLowerCase();
	}
}
