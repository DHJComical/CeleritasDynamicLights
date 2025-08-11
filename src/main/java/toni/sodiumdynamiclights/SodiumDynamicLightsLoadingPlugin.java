package toni.sodiumdynamiclights;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.Mixins;
import zone.rong.mixinbooter.IEarlyMixinLoader;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class SodiumDynamicLightsLoadingPlugin implements IFMLLoadingPlugin, IEarlyMixinLoader {
    @Override
    public @Nullable String[] getASMTransformerClass() {
        return new String[0];
    }

    @Override
    public @Nullable String getModContainerClass() {
        return null;
    }

    @Override
    public @Nullable String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> map) {
    }

    @Override
    public @Nullable String getAccessTransformerClass() {
        return null;
    }

	@Override
	public List<String> getMixinConfigs() {
		return Collections.singletonList("celeritasdynamiclights.default.mixin.json");
	}
}