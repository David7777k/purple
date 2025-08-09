package jaypasha.funpay.ui.clickGui;

import jaypasha.funpay.Pasxalka;
import jaypasha.funpay.modules.more.Category;
import jaypasha.funpay.modules.more.ModuleLayer;
import jaypasha.funpay.modules.settings.SettingLayer;
import jaypasha.funpay.modules.settings.impl.BooleanSetting;
import jaypasha.funpay.modules.settings.impl.Collection;
import jaypasha.funpay.modules.settings.impl.SliderSetting;
import jaypasha.funpay.ui.clickGui.components.module.ModuleLayerComponent;
import jaypasha.funpay.ui.clickGui.components.settings.SettingComponent;
import jaypasha.funpay.ui.clickGui.components.settings.booleanSetting.BooleanSettingComponent;
import jaypasha.funpay.ui.clickGui.components.settings.collection.CollectionComponent;
import jaypasha.funpay.ui.clickGui.components.settings.sliderSetting.SliderSettingComponent;

import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

public final class Helper {

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
        return 40f / 2 + settingComponents.stream()
                                .filter(e -> e.getSettingLayer().getVisible().get())
                                .map(SettingComponent::getHeight)
                                .reduce(0f, Float::sum) + (settingComponents.isEmpty() ? 0f : 5f);
    }

    public static SettingComponent find(SettingLayer settingLayer) {
        if (settingLayer instanceof BooleanSetting) return new BooleanSettingComponent(settingLayer);
        if (settingLayer instanceof Collection collection) return new CollectionComponent(collection);
        if (settingLayer instanceof SliderSetting sliderSetting) return new SliderSettingComponent(sliderSetting);

        return null;
    }

}
