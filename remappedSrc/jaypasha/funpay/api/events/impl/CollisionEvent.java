package jaypasha.funpay.api.events.impl;

/*
 * Create by puzatiy
 * At 03.06.2025
 */

import jaypasha.funpay.api.events.EventLayer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.util.math.Vec3d;

public class CollisionEvent extends EventLayer {

    @Getter
    @AllArgsConstructor
    public static class PlayerCollisionEvent<T> extends CollisionEvent {
        T t;
    }

    @AllArgsConstructor
    public static class BlocksCollisionEvent extends CollisionEvent {
        Vec3d motion;
    }

}