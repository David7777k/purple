package jaypasha.funpay.modules.impl.combat.auraModule.controllers;

import jaypasha.funpay.Api;
import jaypasha.funpay.api.events.EventManager;
import jaypasha.funpay.api.events.impl.AuraEvents;
import jaypasha.funpay.modules.impl.combat.auraModule.configs.AttackConfiguration;
import jaypasha.funpay.modules.impl.combat.auraModule.interfaces.AttackLayer;
import jaypasha.funpay.utility.math.MathTime;
import net.minecraft.util.Hand;

public class AttackController implements AttackLayer, Api {

    MathTime mathTime = MathTime.create();

    @Override
    public void attack(AttackConfiguration attackConfiguration) {
        if (!attackConfiguration.isValid()) return;

        // Правильная генерация задержки: 500..600 ms
        int delay = 500 + (int) (Math.random() * 100.0); // [500, 599]
        if (!mathTime.isReached(delay)) return;

        mc.interactionManager.attackEntity(mc.player, attackConfiguration.entity());

        AuraEvents.AttackEvent auraAttackEvent = new AuraEvents.AttackEvent();
        EventManager.call(auraAttackEvent);

        mc.player.swingHand(Hand.MAIN_HAND);
        mathTime.resetCounter();
    }

}
