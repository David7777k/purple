package jaypasha.funpay.modules.settings.impl;

/*
 * Create by puzatiy
 * Updated by Alex’s AI‑напарник
 */

import jaypasha.funpay.modules.more.ModuleLayer;
import jaypasha.funpay.modules.settings.SettingLayer;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import net.minecraft.text.Text;

import java.util.function.Supplier;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SliderSetting extends SettingLayer {

    Float value, min, max, increments;

    @Setter
    Boolean dragging = false;

    public SliderSetting(Text name, Text description, Supplier<Boolean> visible) {
        super(name, description, visible);
    }

    /**
     * Установка диапазона и шага ползунка.
     * Автоматически ставит начальное значение в середину.
     */
    public SliderSetting set(float min, float max, float increments) {
        this.min = min;
        this.max = max;
        this.increments = increments;
        this.set(max / 2);
        return this;
    }

    /**
     * Установка конкретного значения.
     */
    public SliderSetting set(float value) {
        this.value = value;
        return this;
    }

    /**
     * Алиас для задания диапазона — под красивый fluent‑стиль.
     */
    public SliderSetting range(float min, float max, float increments) {
        return set(min, max, increments);
    }

    /**
     * Алиас для задания значения — под красивый fluent‑стиль.
     */
    public SliderSetting value(float value) {
        return set(value);
    }

    @Override
    public SliderSetting register(ModuleLayer provider) {
        super.reg(provider);
        return this;
    }

    @Override
    public SliderSetting collection(Collection collection) {
        collection.put(this);
        return this;
    }
}
