package toni.sodiumdynamiclights.mixin;

import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;
import toni.sodiumdynamiclights.SodiumDynamicLights;

@Mixin(TileEntityRendererDispatcher.class)
public class TileEntityRendererDispatcherMixin {

    @ModifyArgs(method = "render(Lnet/minecraft/tileentity/TileEntity;FI)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getCombinedLight(Lnet/minecraft/util/math/BlockPos;I)I"))
    private void onRender(Args args) {
        BlockPos pos = args.get(0);
        int vanillaLight = args.get(1);

        int dynamicLight = (int) SodiumDynamicLights.get().getDynamicLightLevel(pos);

        int finalLight = Math.max(vanillaLight, dynamicLight);

        args.set(1, finalLight);
    }
}
