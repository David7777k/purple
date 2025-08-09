package jaypasha.funpay.api.events.impl;

/*
 * Create by puzatiy
 * At 03.06.2025
 */

import jaypasha.funpay.api.events.EventLayer;
import lombok.*;
import lombok.experimental.FieldDefaults;
import net.minecraft.util.math.Vec3d;

public class PlayerEvent extends EventLayer {

    public static class DeathEvent extends PlayerEvent { }

    public static class MovementEvent extends PlayerEvent { }

    @Setter
    @Getter
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class VelocityEvent extends PlayerEvent {
        Vec3d input;
        float speed;
        float yaw;
        Vec3d velocity;
    }
}
