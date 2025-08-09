package jaypasha.funpay.invoke;

/*
 * Create by puzatiy
 * At 03.06.2025
 */

import jaypasha.funpay.api.events.EventManager;
import jaypasha.funpay.api.events.impl.PlayerEvent;
import net.minecraft.client.gui.screen.DeathScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DeathScreen.class)
public class DeathScreenInvoke {

    @Inject(method = "init", at = @At("HEAD"))
    private void init(CallbackInfo ci) {
        PlayerEvent.DeathEvent playerDeathEvent = new PlayerEvent.DeathEvent();
        EventManager.call(playerDeathEvent);
    }

}
