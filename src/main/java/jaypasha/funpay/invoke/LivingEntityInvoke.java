package jaypasha.funpay.invoke;

/*
 * Create by puzatiy
 * At 03.06.2025
 */

import jaypasha.funpay.Pasxalka;
import jaypasha.funpay.api.events.EventManager;
import jaypasha.funpay.api.events.impl.CollisionEvent;
import jaypasha.funpay.modules.impl.combat.AttackAuraModule;
import jaypasha.funpay.modules.impl.combat.auraModule.Vector;
import jaypasha.funpay.modules.impl.combat.auraModule.services.RotationService;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class LivingEntityInvoke {

    @Inject(method = "isPushable", at = @At("HEAD"), cancellable = true)
    private void hookPushEvent(CallbackInfoReturnable<Boolean> cir) {
        CollisionEvent.PlayerCollisionEvent event = new CollisionEvent.PlayerCollisionEvent();
        EventManager.call(event);

        if (event.isCanceled())
            cir.setReturnValue(false);
    }

    @Redirect(method = "jump", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;addVelocityInternal(Lnet/minecraft/util/math/Vec3d;)V"))
    private void hookFixRotation(LivingEntity instance, Vec3d vec3d) {
        RotationService rotationService = ((AttackAuraModule) Pasxalka.getInstance().getModuleRepository().find(AttackAuraModule.class)).getRotationService();
        Vector vector = rotationService.getCurrentVector();

        if ((Object) this != MinecraftClient.getInstance().player) {
            instance.addVelocityInternal(vec3d);
        }

        if (vector == null) {
            instance.addVelocityInternal(vec3d);
        }

        float yaw = vector.getYaw() * 0.017453292F;

        instance.addVelocityInternal(new Vec3d(-MathHelper.sin(yaw) * 0.2F, 0.0, MathHelper.cos(yaw) * 0.2F));
    }

    @Redirect(method = "calcGlidingVelocity", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;getRotationVector()Lnet/minecraft/util/math/Vec3d;"))
    private Vec3d hookModifyFallFlyingRotationVector(LivingEntity original) {
        if ((Object) this != MinecraftClient.getInstance().player) {
            return original.getRotationVector();
        }

        var rotation = ((AttackAuraModule) Pasxalka.getInstance().getModuleRepository().find(AttackAuraModule.class)).getRotationService().getCurrentVector();

        if (rotation == null) {
            return original.getRotationVector();
        }

        return rotation.toVector();
    }

}
