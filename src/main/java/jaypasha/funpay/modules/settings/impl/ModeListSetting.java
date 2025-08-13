package jaypasha.funpay.modules.settings.impl;

/*
 * Create by puzatiy
 * Updated by Alex’s AI‑напарник
 */

import jaypasha.funpay.modules.more.ModuleLayer;
import jaypasha.funpay.modules.settings.SettingLayer;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ModeListSetting extends SettingLayer {

    @Getter
    List<BooleanSetting> values = new ArrayList<>();

    public ModeListSetting(Text name, Text description, Supplier<Boolean> visible) {
        super(name, description, visible);
    }

    /**
     * Задаёт список доступных значений.
     */
    public ModeListSetting set(String... list) {
        Arrays.stream(list)
                .map(this::create)
                .forEach(values::add);
        return this;
    }

    /**
     * Включает элемент по имени (если найден).
     */
    public ModeListSetting enable(String name) {
        BooleanSetting bs = get(name);
        if (bs != null) {
            bs.set(true);
        }
        return this;
    }

    /**
     * Выключает элемент по имени (если найден).
     */
    public ModeListSetting disable(String name) {
        BooleanSetting bs = get(name);
        if (bs != null) {
            bs.set(false);
        }
        return this;
    }

    public boolean empty() {
        return values.isEmpty();
    }

    public boolean emptySelected() {
        return values.stream()
                .noneMatch(BooleanSetting::getEnabled);
    }

    public List<String> asStringList() {
        return values.stream()
                .map(BooleanSetting::getName)
                .map(Text::getString)
                .toList();
    }

    public List<String> getSelected() {
        return values.stream()
                .filter(BooleanSetting::getEnabled)
                .map(BooleanSetting::getName)
                .map(Text::getString)
                .toList();
    }

    /**
     * Возвращает BooleanSetting по имени (регистр не учитывается).
     * Если не найдено — вернёт null (без NPE).
     */
    public BooleanSetting get(String text) {
        if (text == null) return null;
        return values.stream()
                .filter(e -> e.getName().getString().equalsIgnoreCase(text))
                .findFirst()
                .orElse(null);
    }

    public BooleanSetting create(String text) {
        return new BooleanSetting(Text.of(text), Text.empty(), () -> true);
    }

    @Override
    public ModeListSetting register(ModuleLayer provider) {
        super.reg(provider);
        return this;
    }

    @Override
    public ModeListSetting collection(Collection collection) {
        collection.put(this);
        return this;
    }
}
