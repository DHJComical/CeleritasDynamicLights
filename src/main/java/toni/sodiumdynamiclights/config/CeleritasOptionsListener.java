package toni.sodiumdynamiclights.config;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.taumc.celeritas.api.OptionGUIConstructionEvent;

public class CeleritasOptionsListener {

    @SubscribeEvent
    public static void onCeleritasOptionsConstruct(OptionGUIConstructionEvent event) {
        event.addPage(DynamicLightsCeleritasPage.celeritasDynamicLights());
    }

}
