package jaypasha.funpay.invoke;

/*
 * Create by puzatiy
 * At 03.06.2025
 */

import jaypasha.funpay.api.events.EventManager;
import jaypasha.funpay.api.events.impl.CollisionEvent;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class LivingEntityInvoke {

    @Inject(method = "isPushable", at = @At("HEAD"), cancellable = true)
    private void hookPushEvent(CallbackInfoReturnable<Boolean> cir) {
        CollisionEvent.PlayerCollisionEvent<LivingEntity> event = new CollisionEvent.PlayerCollisionEvent<>((LivingEntity) (Object) this);
        EventManager.call(event);

        if (event.isCanceled()) cir.setReturnValue(false);
    }

}
