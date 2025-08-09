package jaypasha.funpay.invoke;

/*
 * Create by puzatiy
 * At 03.06.2025
 */

import jaypasha.funpay.Pasxalka;
import jaypasha.funpay.api.draggable.data.DraggableRepository;
import jaypasha.funpay.api.events.EventManager;
import jaypasha.funpay.api.events.impl.RenderEvent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.render.RenderTickCounter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class InGameHudInvoke {

    @Inject(method = "render", at = @At(value = "HEAD"))
    private void renderBeforeHudInvokeMethod(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        EventManager.call(new RenderEvent.BeforeHud(context, tickCounter));
    }

    @Inject(method = "render", at = @At(value = "RETURN"))
    private void renderAfterHudInvokeMethod(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        DraggableRepository draggableRepository = Pasxalka.getInstance().getDraggableRepository();
        MinecraftClient mc = MinecraftClient.getInstance();

        if (!(mc.currentScreen instanceof ChatScreen))
            draggableRepository.render(context, tickCounter, mc.mouse.getX(), mc.mouse.getY());

        EventManager.call(new RenderEvent.AfterHud(context, tickCounter));
    }

}
