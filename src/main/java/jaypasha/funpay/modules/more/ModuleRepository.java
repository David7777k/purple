package jaypasha.funpay.modules.more;

/*
 * Create by puzatiy
 * At 03.06.2025
 */

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

    List<ModuleLayer> moduleLayers = new ArrayList<>();

    public ModuleRepository() {
        Pasxalka.getInstance().getEventBus().register(this);
    }

    public void init() {
        moduleLayers.addAll(
            List.of(
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
            )
        );

        moduleLayers.forEach(Pasxalka.getInstance().getEventBus()::register);
    }

    public ModuleLayer find(Class<? extends ModuleLayer> clazz) {
        return moduleLayers.stream()
                .filter(e -> e.getClass().equals(clazz))
                .findFirst()
                .orElse(null);
    }

    public List<ModuleLayer> filter(Predicate<ModuleLayer> predicate) {
        return moduleLayers.stream()
                .filter(predicate)
                .toList();
    }

    public void forEach(Consumer<ModuleLayer> action) {
        moduleLayers.forEach(action);
    }

    @Subscribe
    private void keyEventListener(KeyEvent keyEvent) {
        moduleLayers.forEach(e -> {
            if (keyEvent.getKey() == e.getKey() && keyEvent.getAction() == 1 && mc.currentScreen == null) {
                e.toggleEnabled();
            }
        });
    }

    @Subscribe
    private void toggleEventListener(ModuleEvent.ToggleEvent toggleEvent) {
        moduleLayers.forEach(e -> {
            if (toggleEvent.getModuleLayer().equals(e))
                e.toggleEnabled();
        });
    }
}
