package toni.sodiumdynamiclights.mixin;

import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import toni.sodiumdynamiclights.SodiumDynamicLights;

@Mixin(value = LevelRenderer.BrightnessGetter.class)
public interface LevelRendererBrightnessMixin {
    @Inject(
        method = "method_68890(Lnet/minecraft/world/level/BlockAndTintGetter;Lnet/minecraft/core/BlockPos;)I",
        at = @At("TAIL"),
        cancellable = true
    )
    private static void onGetLightmapCoordinates(BlockAndTintGetter level, BlockPos pos, CallbackInfoReturnable<Integer> cir) {
        if (!level.getBlockState(pos).isSolidRender(#if mc < 214 level, pos #endif) && SodiumDynamicLights.get().config.getDynamicLightsMode().isEnabled())
            cir.setReturnValue(SodiumDynamicLights.get().getLightmapWithDynamicLight(pos, cir.getReturnValue()));
    }
}
