package jaypasha.funpay.ui.clickGui;

import jaypasha.funpay.Pasxalka;
import jaypasha.funpay.modules.more.Category;
import jaypasha.funpay.modules.more.ModuleLayer;
import jaypasha.funpay.modules.settings.SettingLayer;
import jaypasha.funpay.modules.settings.impl.*;
import jaypasha.funpay.ui.clickGui.components.module.ModernModuleComponent;
import jaypasha.funpay.ui.clickGui.components.module.ModernModuleComponent;
import jaypasha.funpay.ui.clickGui.components.settings.SettingComponent;
import jaypasha.funpay.ui.clickGui.components.settings.booleanSetting.BooleanSettingComponent;
import jaypasha.funpay.ui.clickGui.components.settings.modeListSetting.ModeListSettingComponent;
import jaypasha.funpay.ui.clickGui.components.settings.modeSetting.ModeSettingComponent;
import jaypasha.funpay.ui.clickGui.components.settings.modern.*;
import jaypasha.funpay.ui.clickGui.components.settings.bindSetting.BindSettingComponent;
import jaypasha.funpay.ui.clickGui.components.settings.collection.CollectionComponent;
import jaypasha.funpay.ui.clickGui.components.settings.sliderSetting.SliderSettingComponent;

import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * Улучшенный Helper для работы с современными компонентами в стиле чужого GUI
 */
public final class Helper {

    /**
     * Получает список современных компонентов модулей для категории
     */
    public static List<ModernModuleComponent> modernModuleLayers(Category category) {
        return Pasxalka.getInstance().getModuleRepository().getModuleLayers().stream()
                .filter(e -> e.getCategory().equals(category))
                .map(ModernModuleComponent::new)
                .toList();
    }

    /**
     * Получает список современных компонентов модулей для категории с предикатом
     */
    public static List<ModernModuleComponent> modernModuleLayers(Category category, Predicate<ModuleLayer> predicate) {
        return Pasxalka.getInstance().getModuleRepository().getModuleLayers().stream()
                .filter(e -> e.getCategory().equals(category))
                .filter(predicate)
                .map(ModernModuleComponent::new)
                .toList();
    }

    /**
     * Получает список обычных компонентов модулей для категории (для обратной совместимости)
     */
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

    /**
     * Создает современные компоненты настроек
     */
    public static List<SettingComponent> modernSettingComponents(ModuleLayer moduleLayer) {
        return moduleLayer.getSettingLayers().stream()
                .map(Helper::createModernSettingComponent)
                .filter(Objects::nonNull)
                .toList();
    }

    /**
     * Создает обычные компоненты настроек (для обратной совместимости)
     */
    public static List<SettingComponent> settingComponents(ModuleLayer moduleLayer) {
        return moduleLayer.getSettingLayers().stream()
                .map(Helper::find)
                .filter(Objects::nonNull)
                .toList();
    }

    /**
     * Фабричный метод для создания современных компонентов настроек
     */
    public static SettingComponent createModernSettingComponent(SettingLayer settingLayer) {
        if (settingLayer == null) return null;

        try {
            // Используем современные компоненты
            if (settingLayer instanceof BooleanSetting booleanSetting) {
                return new ModernBooleanSettingComponent(booleanSetting);
            }
            if (settingLayer instanceof SliderSetting sliderSetting) {
                return new ModernSliderSettingComponent(sliderSetting);
            }
            if (settingLayer instanceof ModeSetting modeSetting) {
                return new ModernModeSettingComponent(modeSetting);
            }

            // Для некоторых настроек используем старые компоненты до их модернизации
            if (settingLayer instanceof Collection collection) {
                return new CollectionComponent(collection);
            }
            if (settingLayer instanceof BindSetting bindSetting) {
                return new BindSettingComponent(bindSetting);
            }
            if (settingLayer instanceof ModeListSetting modeListSetting) {
                return new ModeListSettingComponent(modeListSetting);
            }

        } catch (Exception e) {
            System.err.println("Error creating modern setting component for: " + settingLayer.getClass() + " - " + e.getMessage());
        }
        return null;
    }

    /**
     * Старый фабричный метод для обратной совместимости
     */
    public static SettingComponent find(SettingLayer settingLayer) {
        if (settingLayer == null) return null;

        try {
            if (settingLayer instanceof BooleanSetting) return new BooleanSettingComponent(settingLayer);
            if (settingLayer instanceof Collection collection) return new CollectionComponent(collection);
            if (settingLayer instanceof SliderSetting sliderSetting) return new SliderSettingComponent(sliderSetting);
            if (settingLayer instanceof BindSetting bindSetting) return new BindSettingComponent(bindSetting);
            if (settingLayer instanceof ModeSetting modeSetting) return new ModeSettingComponent(modeSetting);
            if (settingLayer instanceof ModeListSetting modeListSetting) return new ModeListSettingComponent(modeListSetting);
        } catch (Exception e) {
            System.err.println("Error creating setting component for: " + settingLayer.getClass() + " - " + e.getMessage());
        }
        return null;
    }

    /**
     * Вычисляет высоту модуля с учетом современных компонентов
     */
    public static float modernModuleHeight(List<SettingComponent> settingComponents) {
        float base = 20f; // Высота заголовка модуля
        float settingsHeight = settingComponents.stream()
                .filter(e -> e.getSettingLayer().getVisible().get())
                .map(e -> e.getHeight() + 3.5f) // Современный спейсинг
                .reduce(0f, Float::sum);
        return base + settingsHeight;
    }

    /**
     * Вычисляет высоту модуля (старый метод для совместимости)
     */
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

    /**
     * Вычисляет общую высоту настроек с современным спейсингом
     */
    public static float modernSettingsHeight(List<SettingComponent> settingComponents) {
        return settingComponents.stream()
                .filter(e -> e.getSettingLayer().getVisible().get())
                .map(e -> e.getHeight() + 3.5f)
                .reduce(0f, Float::sum);
    }

    /**
     * Старый метод вычисления высоты настроек
     */
    public static float settingsHeight(List<SettingComponent> settingComponents) {
        return settingComponents.stream()
                .filter(e -> e.getSettingLayer().getVisible().get())
                .map(e -> e.getHeight() + 5f)
                .reduce(0f, Float::sum);
    }

    /**
     * Проверяет, подходит ли модуль под поисковый запрос
     */
    public static boolean matchesSearch(ModuleLayer moduleLayer, String searchText) {
        if (searchText == null || searchText.trim().isEmpty()) {
            return true;
        }

        String moduleName = moduleLayer.getModuleName().getString().toLowerCase();
        String query = searchText.toLowerCase().trim();

        return moduleName.contains(query);
    }

    /**
     * Фильтрует модули по поисковому запросу
     */
    public static List<ModernModuleComponent> filterModulesBySearch(List<ModernModuleComponent> modules, String searchText) {
        if (searchText == null || searchText.trim().isEmpty()) {
            return modules;
        }

        return modules.stream()
                .filter(module -> matchesSearch(module.getModuleLayer(), searchText))
                .toList();
    }

    /**
     * Создает анимированный переход между значениями
     */
    public static float lerpValue(float current, float target, float speed, float delta) {
        if (Math.abs(current - target) < 0.001f) {
            return target;
        }
        return current + (target - current) * Math.min(1f, speed * delta);
    }

    /**
     * Интерполирует цвета с учетом альфа-канала
     */
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

    /**
     * Создает эффект плавного появления/исчезновения
     */
    public static float easeInOut(float progress) {
        return progress * progress * (3f - 2f * progress);
    }

    /**
     * Создает эффект отскока
     */
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

    /**
     * Безопасно получает строковое представление объекта
     */
    public static String safeToString(Object obj) {
        if (obj == null) return "null";
        try {
            return obj.toString();
        } catch (Exception e) {
            return obj.getClass().getSimpleName() + "@" + Integer.toHexString(obj.hashCode());
        }
    }

    /**
     * Проверяет, включен ли модуль
     */
    public static boolean isModuleEnabled(ModuleLayer moduleLayer) {
        try {
            return moduleLayer != null && moduleLayer.isEnabled();
        } catch (Exception e) {
            System.err.println("Error checking module state: " + e.getMessage());
            return false;
        }
    }

    /**
     * Безопасно получает имя модуля
     */
    public static String getModuleName(ModuleLayer moduleLayer) {
        try {
            return moduleLayer != null && moduleLayer.getModuleName() != null ?
                    moduleLayer.getModuleName().getString() : "Unknown";
        } catch (Exception e) {
            System.err.println("Error getting module name: " + e.getMessage());
            return "Error";
        }
    }

    /**
     * Форматирует значение для отображения
     */
    public static String formatValue(Object value) {
        if (value == null) return "null";

        if (value instanceof Float f) {
            // Убираем лишние нули после запятой
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