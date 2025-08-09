package jaypasha.funpay.modules.impl.combat.auraModule.interfaces;

import jaypasha.funpay.modules.impl.combat.auraModule.configs.AttackConfiguration;

@FunctionalInterface
public interface AttackLayer {

    void attack(AttackConfiguration attackConfiguration);

}
