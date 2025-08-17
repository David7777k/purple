package jaypasha.funpay.modules.settings.impl;

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

import java.util.*;
import java.util.function.Supplier;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ModeSetting extends SettingLayer {

    List<String> values = new ArrayList<>();

    /**
     * true — мультивыбор, false — одиночный выбор.
     */
    boolean multiSelect;

    @NonFinal
    Boolean opened = false;

    // Single‑mode
    @NonFinal
    String value = null;

    // Multi‑mode
    @NonFinal
    Set<String> selectedValues = new LinkedHashSet<>();

    Animation openAnimation = new DecelerateAnimation()
            .setMs(250)
            .setValue(1);

    public ModeSetting(Text name, Text description, Supplier<Boolean> visible) {
        this(name, description, visible, false);
    }

    public ModeSetting(Text name, Text description, Supplier<Boolean> visible, boolean multiSelect) {
        super(name, description, visible);
        this.multiSelect = multiSelect;
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

    /**
     * Устанавливает значение:
     * - в single mode — заменяет текущее,
     * - в multi mode — очищает список и добавляет единственный элемент.
     */
    public ModeSetting set(String value) {
        if (!multiSelect) {
            if (Objects.equals(this.value, value)) return this;
            this.value = value;
            this.getAnimation().reset();
        } else {
            selectedValues.clear();
            selectedValues.add(value);
        }
        return this;
    }

    /**
     * В мультивыборе — инвертирует флаг пункта.
     * В одиночном — просто устанавливает.
     */
    public void toggle(String v) {
        if (!multiSelect) {
            set(v);
        } else {
            if (selectedValues.contains(v)) {
                selectedValues.remove(v);
            } else {
                selectedValues.add(v);
            }
        }
        this.getAnimation().reset();
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

    /**
     * Старый геттер — для совместимости.
     */
    public String getSelected() {
        return getValue();
    }

    /**
     * Возвращает первое выбранное значение (или null).
     */
    public String getValue() {
        if (multiSelect) {
            return selectedValues.isEmpty() ? null : selectedValues.iterator().next();
        } else {
            return value;
        }
    }

    /**
     * Возвращает список всех выбранных значений (только для multiSelect).
     */
    public List<String> getSelectedValues() {
        return new ArrayList<>(selectedValues);
    }

    /**
     * Проверка: выбран ли данный пункт.
     */
    public boolean isSelected(String v) {
        return multiSelect ? selectedValues.contains(v) : Objects.equals(value, v);
    }

    public ModeSetting select(String v) {
        if (!multiSelect) {
            this.value = v;
        } else {
            selectedValues.add(v);
        }
        return this;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof String) || (value == null && selectedValues.isEmpty())) return false;
        String cmp = (String) obj;
        if (!multiSelect) {
            return value.equalsIgnoreCase(cmp);
        }
        return selectedValues.stream().anyMatch(v -> v.equalsIgnoreCase(cmp));
    }
}
