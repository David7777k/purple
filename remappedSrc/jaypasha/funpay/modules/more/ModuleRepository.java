package jaypasha.funpay.modules.more;

/*
 * Create by puzatiy
 * At 03.06.2025
 */

import jaypasha.funpay.Api;
import jaypasha.funpay.Pasxalka;
import jaypasha.funpay.modules.impl.combat.AttackAuraModule;
import jaypasha.funpay.modules.impl.miscellaneous.AutoLeaveModule;
import jaypasha.funpay.modules.impl.movement.SprintModule;
import jaypasha.funpay.modules.impl.player.NoPushModule;
import jaypasha.funpay.modules.impl.render.ArrowsModule;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public final class ModuleRepository implements Api {

    @Getter
    List<ModuleLayer> moduleLayers = new ArrayList<>();

    public void init() {
        moduleLayers.addAll(
            List.of(
                new AttackAuraModule(),
                new SprintModule(),
                new ArrowsModule(),
                new NoPushModule(),
                new AutoLeaveModule()
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

}
