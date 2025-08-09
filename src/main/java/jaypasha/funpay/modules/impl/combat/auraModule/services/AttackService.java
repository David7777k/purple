package jaypasha.funpay.modules.impl.combat.auraModule.services;

import jaypasha.funpay.modules.impl.combat.auraModule.configs.AttackConfiguration;
import jaypasha.funpay.modules.impl.combat.auraModule.controllers.AttackController;
import jaypasha.funpay.modules.impl.combat.auraModule.interfaces.AttackLayer;

public class AttackService implements AttackLayer {

    AttackController attackController = new AttackController();

    @Override
    public void attack(AttackConfiguration attackConfiguration) {
        attackController.attack(attackConfiguration);
    }

}
