package jaypasha.funpay.ui.clickGui;

import jaypasha.funpay.Pasxalka;
import jaypasha.funpay.modules.more.Category;
import jaypasha.funpay.modules.more.ModuleLayer;
import jaypasha.funpay.modules.settings.SettingLayer;
import jaypasha.funpay.modules.settings.impl.*;
import jaypasha.funpay.ui.clickGui.components.module.ModernModuleComponent;
import jaypasha.funpay.ui.clickGui.components.settings.SettingComponent;
import jaypasha.funpay.ui.clickGui.components.settings.collection.CollectionComponent;
import jaypasha.funpay.ui.clickGui.components.settings.modeListSetting.ModeListSettingComponent;
import jaypasha.funpay.ui.clickGui.components.settings.modeSetting.ModeSettingComponent;
import jaypasha.funpay.ui.clickGui.components.settings.modern.*;
import jaypasha.funpay.ui.clickGui.components.settings.sliderSetting.SliderSettingComponent;

import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

public final class Helper {

    public static List<ModernModuleComponent> modernModuleLayers(Category category) {
        return Pasxalka.getInstance().getModuleRepository().getModuleLayers().stream()
                .filter(e -> e.getCategory().equals(category))
                .map(ModernModuleComponent::new)
                .toList();
    }

    public static List<ModernModuleComponent> modernModuleLayers(Category category, Predicate<ModuleLayer> predicate) {
        return Pasxalka.getInstance().getModuleRepository().getModuleLayers().stream()
                .filter(e -> e.getCategory().equals(category))
                .filter(predicate)
                .map(ModernModuleComponent::new)
                .toList();
    }

    public static List<ModernModuleComponent> moduleLayers(Category category) {
        return Pasxalka.getInstance().getModuleRepository().getModuleLayers().stream()
                .filter(e -> e.getCategory().equals(category))
                .map(ModernModuleComponent::new)
                .toList();
    }

    public static List<ModernModuleComponent> moduleLayers(Category category, Predicate<ModuleLayer> predicate) {
        return Pasxalka.getInstance().getModuleRepository().getModuleLayers().stream()
                .filter(e -> e.getCategory().equals(category))
                .filter(predicate)
                .map(ModernModuleComponent::new)
                .toList();
    }

    public static List<SettingComponent> modernSettingComponents(ModuleLayer moduleLayer) {
        return moduleLayer.getSettingLayers().stream()
                .map(Helper::createModernSettingComponent)
                .filter(Objects::nonNull)
                .toList();
    }

    public static List<SettingComponent> settingComponents(ModuleLayer moduleLayer) {
        return moduleLayer.getSettingLayers().stream()
                .map(Helper::find)
                .filter(Objects::nonNull)
                .toList();
    }

    public static SettingComponent createModernSettingComponent(SettingLayer settingLayer) {
        if (settingLayer == null) return null;
        try {
            if (settingLayer instanceof BooleanSetting bs) return new ModernBooleanSettingComponent(bs);
            if (settingLayer instanceof SliderSetting ss) return new ModernSliderSettingComponent(ss);
            if (settingLayer instanceof ModeSetting ms) return new ModernModeSettingComponent(ms);

            if (settingLayer instanceof Collection c) return new CollectionComponent(c);
            // Исправлено: используем современный компонент для бинда
            if (settingLayer instanceof BindSetting bs) return new ModernBindSettingComponent(bs);
            if (settingLayer instanceof ModeListSetting ml) return new ModeListSettingComponent(ml);
        } catch (Exception e) {
            System.err.println("Error creating modern setting component for: "
                    + settingLayer.getClass() + " - " + e.getMessage());
        }
        return null;
    }

    public static SettingComponent find(SettingLayer settingLayer) {
        if (settingLayer == null) return null;
        try {
            if (settingLayer instanceof BooleanSetting) return new jaypasha.funpay.ui.clickGui.components.settings.booleanSetting.BooleanSettingComponent(settingLayer);
            if (settingLayer instanceof Collection c) return new CollectionComponent(c);
            if (settingLayer instanceof SliderSetting s) return new SliderSettingComponent(s);
            if (settingLayer instanceof BindSetting b) return new jaypasha.funpay.ui.clickGui.components.settings.bindSetting.BindSettingComponent(b);
            if (settingLayer instanceof ModeSetting m) return new ModeSettingComponent(m);
            if (settingLayer instanceof ModeListSetting ml) return new ModeListSettingComponent(ml);
        } catch (Exception e) {
            System.err.println("Error creating setting component for: "
                    + settingLayer.getClass() + " - " + e.getMessage());
        }
        return null;
    }

    public static float modernModuleHeight(List<SettingComponent> settingComponents) {
        float base = 20f;
        float settingsHeight = settingComponents.stream()
                .filter(e -> e.getSettingLayer().getVisible().get())
                .map(e -> e.getHeight() + 3.5f)
                .reduce(0f, Float::sum);
        return base + settingsHeight;
    }

    public static float moduleHeight(List<SettingComponent> settingComponents) {
        float base = 20f;
        float settingsH = settingComponents.stream()
                .filter(e -> e.getSettingLayer().getVisible().get())
                .map(e -> e.getHeight() + 5f)
                .reduce(0f, Float::sum);
        return base + settingsH;
    }

    public static float moduleHeight(ModernModuleComponent moduleComponent) {
        return moduleComponent.getTotalHeight();
    }

    public static float modernSettingsHeight(List<SettingComponent> settingComponents) {
        return settingComponents.stream()
                .filter(e -> e.getSettingLayer().getVisible().get())
                .map(e -> e.getHeight() + 3.5f)
                .reduce(0f, Float::sum);
    }

    public static float settingsHeight(List<SettingComponent> settingComponents) {
        return settingComponents.stream()
                .filter(e -> e.getSettingLayer().getVisible().get())
                .map(e -> e.getHeight() + 5f)
                .reduce(0f, Float::sum);
    }

    public static boolean matchesSearch(ModuleLayer moduleLayer, String searchText) {
        if (searchText == null || searchText.trim().isEmpty()) return true;
        String moduleName = moduleLayer.getModuleName().getString().toLowerCase();
        String query = searchText.toLowerCase().trim();
        return moduleName.contains(query);
    }

    public static List<ModernModuleComponent> filterModulesBySearch(List<ModernModuleComponent> modules, String searchText) {
        if (searchText == null || searchText.trim().isEmpty()) return modules;
        return modules.stream()
                .filter(module -> matchesSearch(module.getModuleLayer(), searchText))
                .toList();
    }

    public static float lerpValue(float current, float target, float speed, float delta) {
        if (Math.abs(current - target) < 0.001f) return target;
        return current + (target - current) * Math.min(1f, speed * delta);
    }

    public static int lerpColor(int colorA, int colorB, float progress) {
        progress = Math.max(0f, Math.min(1f, progress));
        int aA = (colorA >> 24) & 0xFF;
        int rA = (colorA >> 16) & 0xFF;
        int gA = (colorA >> 8) & 0xFF;
        int bA = colorA & 0xFF;

        int aB = (colorB >> 24) & 0xFF;
        int rB = (colorB >> 16) & 0xFF;
        int gB = (colorB >> 8) & 0xFF;
        int bB = colorB & 0xFF;

        int a = (int) (aA + (aB - aA) * progress);
        int r = (int) (rA + (rB - rA) * progress);
        int g = (int) (gA + (gB - gA) * progress);
        int b = (int) (bA + (bB - bA) * progress);

        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    public static float easeInOut(float progress) {
        return progress * progress * (3f - 2f * progress);
    }

    public static float easeOutBounce(float progress) {
        if (progress < 1f / 2.75f) {
            return 7.5625f * progress * progress;
        } else if (progress < 2f / 2.75f) {
            return 7.5625f * (progress -= 1.5f / 2.75f) * progress + 0.75f;
        } else if (progress < 2.5f / 2.75f) {
            return 7.5625f * (progress -= 2.25f / 2.75f) * progress + 0.9375f;
        } else {
            return 7.5625f * (progress -= 2.625f / 2.75f) * progress + 0.984375f;
        }
    }

    public static String safeToString(Object obj) {
        if (obj == null) return "null";
        try {
            return obj.toString();
        } catch (Exception e) {
            return obj.getClass().getSimpleName() + "@" + Integer.toHexString(obj.hashCode());
        }
    }

    public static boolean isModuleEnabled(ModuleLayer moduleLayer) {
        try {
            return moduleLayer != null && moduleLayer.isEnabled();
        } catch (Exception e) {
            System.err.println("Error checking module state: " + e.getMessage());
            return false;
        }
    }

    public static String getModuleName(ModuleLayer moduleLayer) {
        try {
            return moduleLayer != null && moduleLayer.getModuleName() != null
                    ? moduleLayer.getModuleName().getString()
                    : "Unknown";
        } catch (Exception e) {
            System.err.println("Error getting module name: " + e.getMessage());
            return "Error";
        }
    }

    public static String formatValue(Object value) {
        if (value == null) return "null";

        if (value instanceof Float f) {
            if (f == f.intValue()) {
                return String.valueOf(f.intValue());
            } else {
                return String.format("%.2f", f);
            }
        } else if (value instanceof Double d) {
            if (d == d.intValue()) {
                return String.valueOf(d.intValue());
            } else {
                return String.format("%.2f", d);
            }
        }
        return value.toString();
    }
}
