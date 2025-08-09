package jaypasha.funpay.ui.clickGui;

import jaypasha.funpay.Pasxalka;
import jaypasha.funpay.modules.more.Category;
import jaypasha.funpay.modules.more.ModuleLayer;
import jaypasha.funpay.modules.settings.SettingLayer;
import jaypasha.funpay.modules.settings.impl.*;
import jaypasha.funpay.ui.clickGui.components.module.ModuleLayerComponent;
import jaypasha.funpay.ui.clickGui.components.settings.SettingComponent;
import jaypasha.funpay.ui.clickGui.components.settings.bindSetting.BindSettingComponent;
import jaypasha.funpay.ui.clickGui.components.settings.booleanSetting.BooleanSettingComponent;
import jaypasha.funpay.ui.clickGui.components.settings.collection.CollectionComponent;
import jaypasha.funpay.ui.clickGui.components.settings.modeListSetting.ModeListSettingComponent;
import jaypasha.funpay.ui.clickGui.components.settings.modeSetting.ModeSettingComponent;
import jaypasha.funpay.ui.clickGui.components.settings.sliderSetting.SliderSettingComponent;

import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

public final class Helper {

    public static List<ModuleLayerComponent> moduleLayers(Category category) {
        return Pasxalka.getInstance().getModuleRepository().getModuleLayers().stream()
                .filter(e -> e.getCategory().equals(category))
                .map(ModuleLayerComponent::new)
                .toList();
    }

    public static List<ModuleLayerComponent> moduleLayers(Category category, Predicate<ModuleLayer> predicate) {
        return Pasxalka.getInstance().getModuleRepository().getModuleLayers().stream()
                .filter(e -> e.getCategory().equals(category))
                .filter(predicate)
                .map(ModuleLayerComponent::new)
                .toList();
    }

    public static List<SettingComponent> settingComponents(ModuleLayer moduleLayer) {
        return moduleLayer.getSettingLayers().stream()
                .map(Helper::find)
                .filter(Objects::nonNull)
                .toList();
    }

    public static float moduleHeight(List<SettingComponent> settingComponents) {
        float base = 20f;
        float settingsH = settingComponents.stream()
                .filter(e -> e.getSettingLayer().getVisible().get())
                .map(e -> e.getHeight() + 5f)
                .reduce(0f, Float::sum);
        return base + settingsH;
    }

    public static float moduleHeight(ModuleLayerComponent moduleComponent) {
        return moduleComponent.getTotalHeight();
    }

    public static float settingsHeight(List<SettingComponent> settingComponents) {
        return settingComponents.stream()
                .filter(e -> e.getSettingLayer().getVisible().get())
                .map(e -> e.getHeight() + 5f)
                .reduce(0f, Float::sum);
    }

    public static SettingComponent find(SettingLayer settingLayer) {
        if (settingLayer instanceof BooleanSetting) return new BooleanSettingComponent(settingLayer);
        if (settingLayer instanceof Collection collection) return new CollectionComponent(collection);
        if (settingLayer instanceof SliderSetting sliderSetting) return new SliderSettingComponent(sliderSetting);
        if (settingLayer instanceof BindSetting bindSetting) return new BindSettingComponent(bindSetting);
        if (settingLayer instanceof ModeSetting modeSetting) return new ModeSettingComponent(modeSetting);
        if (settingLayer instanceof ModeListSetting modeListSetting) return new ModeListSettingComponent(modeListSetting);
        return null;
    }
}