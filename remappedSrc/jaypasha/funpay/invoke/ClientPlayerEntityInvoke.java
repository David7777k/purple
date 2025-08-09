package jaypasha.funpay.invoke;

/*
 * Create by puzatiy
 * At 03.06.2025
 */

import jaypasha.funpay.api.events.EventManager;
import jaypasha.funpay.api.events.impl.CollisionEvent;
import jaypasha.funpay.api.events.impl.PlayerEvent;
import jaypasha.funpay.api.events.impl.TickEvent;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityInvoke {

    @Inject(method = "tick", at = @At("HEAD"))
    private void tick(CallbackInfo ci) {
        TickEvent tickEvent = new TickEvent();
        EventManager.call(tickEvent);
    }

    @Inject(method = "tickMovement", at = @At("HEAD"))
    private void tickMovement(CallbackInfo ci) {
        PlayerEvent.MovementEvent movementEvent = new PlayerEvent.MovementEvent();
        EventManager.call(movementEvent);
    }

    @Inject(method = "pushOutOfBlocks", at = @At("HEAD"), cancellable = true)
    private void hookPushEvent(double x, double z, CallbackInfo ci) {
        CollisionEvent.BlocksCollisionEvent event = new CollisionEvent.BlocksCollisionEvent(new Vec3d(x, 0, z));
        EventManager.call(event);

        if (event.isCanceled()) ci.cancel();
    }

}
