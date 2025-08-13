package jaypasha.funpay.modules.impl.combat.auraModule;

import jaypasha.funpay.modules.impl.combat.AttackAuraModule;
import jaypasha.funpay.modules.impl.combat.auraModule.configs.AttackConfiguration;
import jaypasha.funpay.modules.impl.combat.auraModule.configs.RotationConfiguration;
import jaypasha.funpay.modules.impl.combat.auraModule.configs.TargetFinderConfiguration;
import jaypasha.funpay.modules.impl.combat.auraModule.interfaces.RotationLayer;
import jaypasha.funpay.modules.impl.combat.auraModule.rotation.SmoothRotationLayer;

public class AuraUtils {

    public static TargetFinderConfiguration generateTargetFindConfiguration(AttackAuraModule m) {
        return new TargetFinderConfiguration(
                m.getAttackDistance().getValue() + m.getAddedDistance().getValue(),
                m.getFov().getValue(),
                m.getTargets().get("Игроки").getEnabled(),
                m.getTargets().get("Мобы").getEnabled(),
                m.getTargets().get("Инвиз").getEnabled()
        );
    }

    public static AttackConfiguration generateAttackConfiguration(AttackAuraModule aura) {
        // Важно: используем attackDistance + addedDistance — иначе цель может быть выбрана, но не атаковаться
        float fullDistance = aura.getAttackDistance().getValue() + aura.getAddedDistance().getValue();
        return new AttackConfiguration(
                aura.getTarget(),
                fullDistance,
                aura.getOptions().isEnabled("Только Критами"),
                aura.getOptions().isEnabled("Умные криты"),
                aura.getOptions().isEnabled("Проверка луча"),
                aura.getOptions().isEnabled("Бить через стены"),
                aura.getOptions().isEnabled("Не бить при использовании"),
                aura.getOptions().isEnabled("Только с оружием"),
                aura.getFallDetector()::isFalling
        );
    }

    public static RotationConfiguration generateRotationConfiguration(AttackAuraModule m) {
        RotationLayer layer = switch (m.getRotationMode().getSelected()) {
            case "Обычный" -> new SmoothRotationLayer();
            case "Тестовый" -> new SmoothRotationLayer(); // тут подменишь на экспериментальную ротацию
            default -> new SmoothRotationLayer();
        };
        return new RotationConfiguration(
                m.getTarget(),
                m.getAttackDistance().getValue() + m.getAddedDistance().getValue(),
                layer,
                false // smoothRandomization убран — если понадобится, вернёшь настройку
        );
    }
}
