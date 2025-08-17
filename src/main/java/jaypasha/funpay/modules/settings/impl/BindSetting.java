package jaypasha.funpay.modules.settings.impl;

/*
 * Create by puzatiy
 * At 03.06.2025
 */

import jaypasha.funpay.api.animations.Direction;
import jaypasha.funpay.modules.more.ModuleLayer;
import jaypasha.funpay.modules.settings.SettingLayer;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.text.Text;

import java.util.Objects;
import java.util.function.Supplier;

@Getter
public class BindSetting extends SettingLayer {

    Integer key = -1;
    Boolean selected = false;

    public BindSetting(Text name, Text description, Supplier<Boolean> visible) {
        super(name, description, visible);

        this.getAnimation().setDirection(selected ? Direction.FORWARDS : Direction.BACKWARDS);
    }

    public BindSetting set(Integer keyCode) {
        this.key = keyCode;
        this.setSelected(false);
        this.getAnimation().setDirection(this.selected ? Direction.FORWARDS : Direction.BACKWARDS);

        return this;
    }

    public BindSetting setSelected(boolean selected) {
        this.selected = selected;
        this.getAnimation().setDirection(this.selected ? Direction.FORWARDS : Direction.BACKWARDS);
        this.getAnimation().reset();

        return this;
    }

    @Override
    public BindSetting register(ModuleLayer provider) {
        super.reg(provider);

        return this;
    }

    @Override
    public BindSetting collection(Collection collection) {
        collection.put(this);

        return this;
    }

    public int get() {
        return 0;
    }
}
