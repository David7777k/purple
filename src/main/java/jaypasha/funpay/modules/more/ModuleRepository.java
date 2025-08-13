package jaypasha.funpay.modules.more;

import com.google.common.eventbus.Subscribe;
import jaypasha.funpay.Api;
import jaypasha.funpay.Pasxalka;
import jaypasha.funpay.api.events.impl.KeyEvent;
import jaypasha.funpay.api.events.impl.ModuleEvent;
import jaypasha.funpay.modules.impl.combat.AttackAuraModule;
import jaypasha.funpay.modules.impl.combat.AutoTotemModule;
import jaypasha.funpay.modules.impl.movement.GuiWalkModule;
import jaypasha.funpay.modules.impl.movement.SprintModule;
import jaypasha.funpay.modules.impl.player.NoPushModule;
import jaypasha.funpay.modules.impl.render.*;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

@Getter
public final class ModuleRepository implements Api {

    private final List<ModuleLayer> moduleLayers = new ArrayList<>();

    public ModuleRepository() {
        Pasxalka.getInstance().getEventBus().register(this);
    }

    public void init() {
        moduleLayers.addAll(List.of(
                new AttackAuraModule(),
                new SprintModule(),
                new ArrowsModule(),
                new NoPushModule(),
                new GuiWalkModule(),
                new TargetEspModule(),
                new HudModule(),
                new ProjectilePredictModule(),
                new NameTagsModule(),
                new AutoTotemModule(),
                new ESPModule()
        ));

        // Регистрируем все модули в EventBus
        moduleLayers.forEach(Pasxalka.getInstance().getEventBus()::register);
    }

    /**
     * Поиск модуля по классу с безопасным приведением типа
     */
    public <T extends ModuleLayer> T find(Class<T> clazz) {
        return moduleLayers.stream()
                .filter(m -> m.getClass().equals(clazz))
                .map(clazz::cast)
                .findFirst()
                .orElse(null);
    }

    /**
     * Фильтрация модулей по условию
     */
    public List<ModuleLayer> filter(Predicate<ModuleLayer> predicate) {
        return moduleLayers.stream()
                .filter(predicate)
                .toList();
    }

    /**
     * Выполнение действия для каждого модуля
     */
    public void forEach(Consumer<ModuleLayer> action) {
        moduleLayers.forEach(action);
    }

    @Subscribe
    private void keyEventListener(KeyEvent keyEvent) {
        moduleLayers.forEach(module -> {
            if (keyEvent.getKey() == module.getKey()
                    && keyEvent.getAction() == 1
                    && mc.currentScreen == null) {
                module.toggleEnabled();
            }
        });
    }

    @Subscribe
    private void toggleEventListener(ModuleEvent.ToggleEvent toggleEvent) {
        moduleLayers.forEach(module -> {
            if (toggleEvent.getModuleLayer().equals(module)) {
                module.toggleEnabled();
            }
        });
    }
}
