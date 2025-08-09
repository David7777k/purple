package jaypasha.funpay.invoke;

/*
 * Create by puzatiy
 * At 03.06.2025
 */

import jaypasha.funpay.Pasxalka;
import jaypasha.funpay.api.events.EventManager;
import jaypasha.funpay.api.events.impl.KeyEvent;
import net.minecraft.client.Keyboard;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin(Keyboard.class)
public class KeyboardInvoke {

    @Inject(method = "onKey", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/option/InactivityFpsLimiter;onInput()V"))
    private void onKey(long window, int key, int scancode, int action, int modifiers, CallbackInfo ci) {
        EventManager.call(new KeyEvent(window, key, scancode, action, modifiers));
    }

}
