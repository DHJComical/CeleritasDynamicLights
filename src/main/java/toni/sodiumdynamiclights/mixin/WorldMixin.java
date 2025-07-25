package toni.sodiumdynamiclights.mixin;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import toni.sodiumdynamiclights.DynamicLightSource;
import toni.sodiumdynamiclights.SodiumDynamicLights;

@Mixin(World.class)
public abstract class WorldMixin {

    @Inject(method = "getCombinedLight", at = @At("TAIL"), cancellable = true)
    private void onGetCombinedLight(BlockPos pos, int lightValue, CallbackInfoReturnable<Integer> cir) {
        int original = cir.getReturnValue();

        int sky = original >> 20;
        int block = (original >> 4) & 0xFFFF;

        int dynamic = (int) SodiumDynamicLights.get().getDynamicLightLevel(pos);
        if (dynamic > block) {
            block = dynamic;
        }

        int result = (sky << 20) | (block << 4);
        cir.setReturnValue(result);
    }

    @Inject(method = "updateEntities", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/ITickable;update()V", shift = At.Shift.BEFORE))
    private void beforeBlockEntityTick(CallbackInfo ci) {
        World self = (World)(Object)this;

        if (!self.isRemote) return;

        for (TileEntity tileEntity : self.loadedTileEntityList) {
            if (tileEntity instanceof DynamicLightSource) {
                ((DynamicLightSource) tileEntity).sdl$dynamicLightTick();
            }
        }
    }
}