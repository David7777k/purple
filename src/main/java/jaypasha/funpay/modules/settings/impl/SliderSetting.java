package jaypasha.funpay.modules.settings.impl;

/*
 * Create by puzatiy
 * At 03.06.2025
 */

import jaypasha.funpay.modules.more.ModuleLayer;
import jaypasha.funpay.modules.settings.SettingLayer;
import jaypasha.funpay.modules.settings.SettingLayerBuilder;
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

    public SliderSetting set(float min, float max, float increments) {
        this.min = min;
        this.max = max;
        this.increments = increments;
        this.set(max / 2);

        return this;
    }

    public SliderSetting set(float value) {
        this.value = value;

        return this;
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
