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
        if (attackConfiguration == null) return;

        if (!attackConfiguration.isValid()) {
            System.out.println("[Aura] attack blocked: " + AttackConfiguration.getDebugReason());
            return;
        }

        // Правильная генерация задержки 500..600 ms
        int delayMs = 500 + (int)(java.lang.Math.random() * 100.0); // [500,599]
        if (!mathTime.isReached(delayMs)) {
            // не печатаем каждый тик, чтобы не спамить
            return;
        }

        try {
            mc.interactionManager.attackEntity(mc.player, attackConfiguration.entity());
            mc.player.swingHand(Hand.MAIN_HAND);

            AuraEvents.AttackEvent auraAttackEvent = new AuraEvents.AttackEvent();
            EventManager.call(auraAttackEvent);

            System.out.println("[Aura] attacked: " + attackConfiguration.entity().getName().getString());
        } catch (Throwable t) {
            System.err.println("[Aura] attack error: " + t.getMessage());
            t.printStackTrace();
        } finally {
            mathTime.resetCounter();
        }
    }
}
