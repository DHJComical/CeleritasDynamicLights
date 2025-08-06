package toni.sodiumdynamiclights.config;

import com.google.common.collect.ImmutableList;

import net.minecraft.util.text.ITextComponent;
import org.taumc.celeritas.api.options.OptionIdentifier;
import org.taumc.celeritas.api.options.control.CyclingControl;
import org.taumc.celeritas.api.options.control.TickBoxControl;
import org.taumc.celeritas.api.options.structure.OptionFlag;
import org.taumc.celeritas.api.options.structure.OptionGroup;
import org.taumc.celeritas.api.options.structure.OptionImpl;
import org.taumc.celeritas.api.options.structure.OptionPage;
import org.taumc.celeritas.impl.gui.options.storage.SodiumOptionsStorage;
import org.taumc.celeritas.impl.util.ComponentUtil;
import toni.sodiumdynamiclights.DynamicLightsMode;
import toni.sodiumdynamiclights.ExplosiveLightingMode;

import java.util.ArrayList;
import java.util.List;

public class DynamicLightsCeleritasPage{
    public static final SodiumOptionsStorage mixinsOptionsStorage = new SodiumOptionsStorage();

    public static OptionPage celeritasDynamicLights() {
        final List<OptionGroup> groups = new ArrayList<>();

        groups.add(OptionGroup.createBuilder()
                .setId(ResourceLocations.COMMON)
                .add(OptionImpl.createBuilder(DynamicLightsMode.class, mixinsOptionsStorage)
                        .setId(ResourceLocations.OPTIONS_MODE)
                        .setName(ComponentUtil.translatable("sodium.dynamiclights.options.mode"))
                        .setTooltip(ComponentUtil.translatable("sodium.dynamiclights.options.mode.desc"))
                        .setControl(option -> new CyclingControl<>(option, DynamicLightsMode.class, new ITextComponent[]{
                                DynamicLightsMode.OFF.getTranslatedText(),
                                DynamicLightsMode.SLOW.getTranslatedText(),
                                DynamicLightsMode.FAST.getTranslatedText(),
                                DynamicLightsMode.REALTIME.getTranslatedText()
                        }))
                        .setBinding((options, value) -> {
                                    DynamicLightsConfig.dynamicLightsMode = value;

                                },
                                (options) -> DynamicLightsConfig.dynamicLightsMode)
                        .setFlags(OptionFlag.REQUIRES_RENDERER_RELOAD)
                        .build())
                .add(OptionImpl.createBuilder(boolean.class, mixinsOptionsStorage)
                        .setId(ResourceLocations.OPTIONS_SELF)
                        .setName(ComponentUtil.translatable("sodium.dynamiclights.options.self"))
                        .setTooltip(ComponentUtil.translatable("sodium.dynamiclights.options.self.desc"))
                        .setControl(TickBoxControl::new)
                        .setBinding((options, value) -> {
                                    DynamicLightsConfig.selfLightSource = value;
                                },
                                (options) -> DynamicLightsConfig.selfLightSource)
                        .build())
                .add(OptionImpl.createBuilder(boolean.class, mixinsOptionsStorage)
                        .setId(ResourceLocations.OPTIONS_ENTITIES)
                        .setName(ComponentUtil.translatable("sodium.dynamiclights.options.entities"))
                        .setTooltip(ComponentUtil.translatable("sodium.dynamiclights.options.entities.desc"))
                        .setControl(TickBoxControl::new)
                        .setBinding((options, value) -> {
                                    DynamicLightsConfig.entitiesLightSource = value;
                                },
                                (options) -> DynamicLightsConfig.entitiesLightSource)
                        .build())
                .add(OptionImpl.createBuilder(boolean.class, mixinsOptionsStorage)
                        .setId(ResourceLocations.OPTIONS_BLOCKENTITIES)
                        .setName(ComponentUtil.translatable("sodium.dynamiclights.options.blockentities"))
                        .setTooltip(ComponentUtil.translatable("sodium.dynamiclights.options.blockentities.desc"))
                        .setControl(TickBoxControl::new)
                        .setBinding((options, value) -> {
                                    DynamicLightsConfig.blockEntitiesLightSource = value;
                                },
                                (options) -> DynamicLightsConfig.blockEntitiesLightSource)
                        .build())
                .add(OptionImpl.createBuilder(boolean.class, mixinsOptionsStorage)
                        .setId(ResourceLocations.OPTIONS_UNDERWATER)
                        .setName(ComponentUtil.translatable("sodium.dynamiclights.options.underwater"))
                        .setTooltip(ComponentUtil.translatable("sodium.dynamiclights.options.underwater.desc"))
                        .setControl(TickBoxControl::new)
                        .setBinding((options, value) -> {
                                    DynamicLightsConfig.waterSensitiveCheck = value;
                                },
                                (options) -> DynamicLightsConfig.waterSensitiveCheck)
                        .build())
                .add(OptionImpl.createBuilder(ExplosiveLightingMode.class, mixinsOptionsStorage)
                        .setId(ResourceLocations.OPTIONS_TNT)
                        .setName(ComponentUtil.translatable("sodium.dynamiclights.options.tnt"))
                        .setTooltip(ComponentUtil.translatable("sodium.dynamiclights.options.tnt.desc"))
                        .setControl(option -> new CyclingControl<>(option, ExplosiveLightingMode.class, new ITextComponent[]{
                                ExplosiveLightingMode.OFF.getTranslatedText(),
                                ExplosiveLightingMode.SIMPLE.getTranslatedText(),
                                ExplosiveLightingMode.FANCY.getTranslatedText(),
                        }))
                        .setBinding((options, value) -> {
                                    DynamicLightsConfig.tntLightingMode = (value);
                                },
                                (options) -> DynamicLightsConfig.tntLightingMode)
                        .build())
                .add(OptionImpl.createBuilder(ExplosiveLightingMode.class, mixinsOptionsStorage)
                        .setId(ResourceLocations.OPTIONS_CREEPER)
                        .setName(ComponentUtil.translatable("sodium.dynamiclights.options.creeper"))
                        .setTooltip(ComponentUtil.translatable("sodium.dynamiclights.options.creeper.desc"))
                        .setControl(option -> new CyclingControl<>(option, ExplosiveLightingMode.class, new ITextComponent[]{
                                ExplosiveLightingMode.OFF.getTranslatedText(),
                                ExplosiveLightingMode.SIMPLE.getTranslatedText(),
                                ExplosiveLightingMode.FANCY.getTranslatedText(),
                        }))
                        .setBinding((options, value) -> {
                                    DynamicLightsConfig.creeperLightingMode = (value);
                                },
                                (options) -> DynamicLightsConfig.creeperLightingMode)
                        .build())
                .build());

        OptionIdentifier<Void> id = OptionIdentifier.create("celeritasdynamiclights", null);
        ITextComponent name = ComponentUtil.translatable("sodium.dynamiclights.options.page");

        return new OptionPage(id, name, ImmutableList.copyOf(groups));

    }
}