package jaypasha.funpay.modules.settings.impl;

/*
 * Create by puzatiy
 * At 03.06.2025
 */

import jaypasha.funpay.api.animations.Animation;
import jaypasha.funpay.api.animations.Direction;
import jaypasha.funpay.api.animations.implement.DecelerateAnimation;
import jaypasha.funpay.modules.more.ModuleLayer;
import jaypasha.funpay.modules.settings.SettingLayer;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ModeSetting extends SettingLayer {

    List<String> values = new ArrayList<>();

    @NonFinal
    Boolean opened = false;

    @NonFinal
    String value = null;

    Animation openAnimation = new DecelerateAnimation()
            .setMs(250)
            .setValue(1);

    public ModeSetting(Text name, Text description, Supplier<Boolean> visible) {
        super(name, description, visible);

        openAnimation.setDirection(this.opened ? Direction.FORWARDS : Direction.BACKWARDS);
        getAnimation().setMs(400);
    }

    public ModeSetting setOpened(Boolean opened) {
        this.opened = opened;
        this.openAnimation.setDirection(this.opened ? Direction.FORWARDS : Direction.BACKWARDS);
        this.openAnimation.reset();

        return this;
    }

    public ModeSetting set(String... strings) {
        values.addAll(Arrays.asList(strings));

        return this;
    }

    public ModeSetting set(String value) {
        if (this.value != null && this.value.equalsIgnoreCase(value)) return this;

        this.value = value;
        this.getAnimation().reset();

        return this;
    }

    @Override
    public ModeSetting register(ModuleLayer provider) {
        super.reg(provider);

        return this;
    }

    @Override
    public ModeSetting collection(Collection collection) {
        collection.put(this);

        return this;
    }

    public String getValue() {
        return value;
    }

    public String getSelected() {
        return getValue();
    }

    public ModeSetting select(String value) {
        this.set(value);
        return this;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof String) || value == null) return false;

        return value.equalsIgnoreCase((String) obj);
    }
}
