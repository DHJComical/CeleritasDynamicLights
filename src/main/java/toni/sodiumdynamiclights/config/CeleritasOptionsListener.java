package toni.sodiumdynamiclights.config;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = "celeritasdynamiclights")
public class CeleritasOptionsListener {

    @SubscribeEvent
    public static void onEmbeddiumPagesRegister(Event event) {
        if (!event.getClass().getName().equals("org.taumc.celeritas.api.OptionGUIConstructionEvent")) {
            return;
        }

        // Top 10 most ugly code
        try {
            Class<?> eventClass = Class.forName("org.taumc.celeritas.api.OptionGUIConstructionEvent");
            Class<?> optionPageClass = Class.forName("org.taumc.celeritas.api.options.structure.OptionPage");

            if (eventClass.isInstance(event)) {
                java.lang.reflect.Method addPageMethod = eventClass.getMethod("addPage", optionPageClass);

                Object page = DynamicLightsCeleritasPage.celeritasDynamicLights();

                addPageMethod.invoke(event, page);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
