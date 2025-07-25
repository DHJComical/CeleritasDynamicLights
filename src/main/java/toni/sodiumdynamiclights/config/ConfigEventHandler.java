package toni.sodiumdynamiclights.config;

import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.common.config.ConfigManager;

public class ConfigEventHandler {
    @SubscribeEvent
    public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
        if ("celeritasdynamiclights".equals(event.getModID())) {
            ConfigManager.sync("celeritasdynamiclights", net.minecraftforge.common.config.Config.Type.INSTANCE);
        }
    }
}
