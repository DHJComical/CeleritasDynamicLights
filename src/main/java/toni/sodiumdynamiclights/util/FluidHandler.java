package toni.sodiumdynamiclights.util;

import git.jbredwards.fluidlogged_api.api.util.FluidloggedUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.fml.common.Loader;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.Internal
public interface FluidHandler {
    boolean FLUIDLOGGING = Loader.isModLoaded("fluidlogged_api");

    static boolean isFluid(@NotNull final Entity entity) {
        final float partialTicks = Minecraft.getMinecraft().getRenderPartialTicks();
        return isFluid(entity.world, new BlockPos(entity.getPositionEyes(partialTicks)));
    }

    static boolean isFluid(@NotNull final IBlockAccess access, @NotNull final BlockPos pos) {
        @NotNull final IBlockState fluidState = getFluidState(access, pos);
        return fluidState.getMaterial().isLiquid() || fluidState.getBlock() instanceof IFluidBlock;
    }

    @NotNull
    static IBlockState getFluidState(@NotNull final IBlockAccess access, @NotNull final BlockPos pos) {
        return FLUIDLOGGING ? FluidloggedUtils.getFluidOrReal(access, pos) : access.getBlockState(pos);
    }
}
