package jaypasha.funpay.modules.impl.combat.auraModule;

import jaypasha.funpay.Api;
import jaypasha.funpay.modules.impl.combat.AttackAuraModule;
import jaypasha.funpay.modules.impl.combat.auraModule.configs.AttackConfiguration;
import jaypasha.funpay.modules.impl.combat.auraModule.configs.RotationConfiguration;
import jaypasha.funpay.modules.impl.combat.auraModule.configs.TargetFinderConfiguration;
import jaypasha.funpay.modules.impl.combat.auraModule.interfaces.RotationLayer;
import jaypasha.funpay.modules.impl.combat.auraModule.rotation.SmoothRotationLayer;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;

import java.util.List;

public class AuraUtils implements Api {

    public static TargetFinderConfiguration generateTargetFindConfiguration(AttackAuraModule auraModule) {
        return new TargetFinderConfiguration(auraModule.getAttackDistance().getValue() + auraModule.getAddedDistance().getValue(),
            List.of(
                OtherClientPlayerEntity.class,
                PlayerEntity.class)
        );
    }

    /*
        * RotationTypes.valueOf(auraModule.getRotationMode().getName().getString()).getRotationLayer()
    */

    public static RotationConfiguration generateRotationConfiguration(AttackAuraModule auraModule) {
        return new RotationConfiguration(auraModule.getTarget(), auraModule.getAttackDistance().getValue() + auraModule.getAddedDistance().getValue(), new SmoothRotationLayer(), auraModule.getRandomizationMode().equals("Плавная"));
    }

    public static AttackConfiguration generateAttackConfiguration(AttackAuraModule auraModule) {
        return new AttackConfiguration(auraModule.getTarget(), auraModule.getAttackDistance().getValue(),
                auraModule.getOptions().get("Только Критами").getEnabled(),
                auraModule.getOptions().get("Умные криты").getEnabled(),
                auraModule.getOptions().get("Проверка луча").getEnabled(),
                auraModule.getOptions().get("Бить через стены").getEnabled(),
                auraModule.getOptions().get("Не бить при использовании").getEnabled());
    }

    @Getter
    @RequiredArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    enum RotationTypes {
        Smooth(new SmoothRotationLayer()),
        SpookyTime(new SmoothRotationLayer());

        RotationLayer rotationLayer;
    }
}
