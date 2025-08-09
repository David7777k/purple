package jaypasha.funpay.invoke;

import jaypasha.funpay.Pasxalka;
import jaypasha.funpay.modules.impl.combat.AttackAuraModule;
import jaypasha.funpay.modules.impl.combat.auraModule.Vector;
import jaypasha.funpay.modules.impl.combat.auraModule.services.RotationService;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PlayerEntity.class)
public class PlayerEntityInvoke {

    @Redirect(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;getYaw()F"))
    private float hookFixRotation(PlayerEntity entity) {
        if ((Object) this != MinecraftClient.getInstance().player) return entity.getYaw();

        RotationService rotationService = ((AttackAuraModule) Pasxalka.getInstance().getModuleRepository().find(AttackAuraModule.class)).getRotationService();
        Vector vector = rotationService.getCurrentVector();

        if (vector == null) {
            return entity.getYaw();
        }

        return vector.getYaw();
    }

}
