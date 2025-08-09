package jaypasha.funpay.invoke;

/*
 * Create by puzatiy
 * At 03.06.2025
 */

import jaypasha.funpay.api.events.EventManager;
import jaypasha.funpay.api.events.impl.PlayerEvent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Entity.class)
public class EntityInvoke {

    @Shadow
    protected static Vec3d movementInputToVelocity(Vec3d movementInput, float speed, float yaw) {
        return null;
    }

    @Redirect(method = "updateVelocity", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;movementInputToVelocity(Lnet/minecraft/util/math/Vec3d;FF)Lnet/minecraft/util/math/Vec3d;"))
    public Vec3d hookVelocity(Vec3d movementInput, float speed, float yaw) {
        if ((Object) this == MinecraftClient.getInstance().player) {
            PlayerEvent.VelocityEvent event = new PlayerEvent.VelocityEvent(movementInput, speed, yaw, EntityInvoke.movementInputToVelocity(movementInput, speed, yaw));
            EventManager.call(event);

            return event.getVelocity();
        }

        return EntityInvoke.movementInputToVelocity(movementInput, speed, yaw);
    }
}
