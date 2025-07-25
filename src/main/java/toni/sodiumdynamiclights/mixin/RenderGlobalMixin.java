package toni.sodiumdynamiclights.mixin;

import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import toni.sodiumdynamiclights.SodiumDynamicLights;
import toni.sodiumdynamiclights.accessor.WorldRendererAccessor;

@Mixin(RenderGlobal.class)
public abstract class RenderGlobalMixin implements WorldRendererAccessor{

    @Invoker("markBlocksForUpdate")
    @Override
    public abstract void sodiumdynamiclights$scheduleChunkRebuild(int minX, int minY, int minZ, int maxX, int maxY, int maxZ, boolean updateImmediately);


    @Inject(method = "renderEntities", at = @At("HEAD"))
    private void beforeRender(Entity renderViewEntity, ICamera camera, float partialTicks, CallbackInfo ci) {
        SodiumDynamicLights.get().updateAll((RenderGlobal) (Object) this);
    }
}