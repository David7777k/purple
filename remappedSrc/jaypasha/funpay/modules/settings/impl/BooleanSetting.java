package jaypasha.funpay.modules.settings.impl;

/*
 * Create by puzatiy
 * At 03.06.2025
 */

import jaypasha.funpay.api.animations.Direction;
import jaypasha.funpay.modules.more.ModuleLayer;
import jaypasha.funpay.modules.settings.SettingLayer;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import net.minecraft.text.Text;

import java.util.function.Supplier;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BooleanSetting extends SettingLayer {

    Boolean enabled = false;

    public BooleanSetting(Text name, Text description, Supplier<Boolean> visible) {
        super(name, description, visible);

        this.getAnimation().setDirection(this.enabled ? Direction.FORWARDS : Direction.BACKWARDS);
    }

    public BooleanSetting set(Boolean enabled) {
        this.enabled = enabled;
        this.getAnimation().setDirection(this.enabled ? Direction.FORWARDS : Direction.BACKWARDS);
        this.getAnimation().reset();

        return this;
    }

    @Override
    public BooleanSetting register(ModuleLayer provider) {
        super.reg(provider);

        return this;
    }

    @Override
    public BooleanSetting collection(Collection collection) {
        collection.put(this);

        return this;
    }
}
