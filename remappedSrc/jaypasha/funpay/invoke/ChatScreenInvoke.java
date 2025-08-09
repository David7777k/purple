package jaypasha.funpay.invoke;

/*
 * Create by puzatiy
 * At 03.06.2025
 */

import jaypasha.funpay.Pasxalka;
import jaypasha.funpay.api.draggable.data.DraggableRepository;
import jaypasha.funpay.api.events.EventManager;
import jaypasha.funpay.api.events.impl.RenderEvent;
import jaypasha.funpay.utility.render.builders.Builder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChatScreen.class)
public abstract class ChatScreenInvoke extends Screen {

    protected ChatScreenInvoke() {
        super(Text.empty());
    }

    @Inject(method = "render", at = @At("HEAD"))
    private void renderAfterChatInvokeMethod(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        DraggableRepository draggableRepository = Pasxalka.getInstance().getDraggableRepository();
        MinecraftClient mc = MinecraftClient.getInstance();

        draggableRepository.update(context, delta, mouseX, mouseY);
        draggableRepository.render(context, mc.getRenderTickCounter(), mouseX, mouseY);

        Builder.TEXT_BUILDER
                .text("Зажмите ALT что-бы заблокировать по Y")
                .size(10)
                .color(0xFFFFFFFF)
                .font(Builder.INTER.get())
                .thickness(0.1f)
                .outline(0xFF000000, .2f)
                .build()
                .render(context.getMatrices().peek().getPositionMatrix(),((float) mc.getWindow().getScaledWidth() / 2) - Builder.INTER.get().getWidth("Зажмите ALT что-бы заблокировать по Y", 10) / 2,10);

        Builder.TEXT_BUILDER
                .text("Нажмите CTRL + ALT что-бы переключить сетку")
                .size(10)
                .color(0xFFFFFFFF)
                .font(Builder.INTER.get())
                .thickness(0.1f)
                .outline(0xFF000000, .2f)
                .build()
                .render(context.getMatrices().peek().getPositionMatrix(),((float) mc.getWindow().getScaledWidth() / 2) - Builder.INTER.get().getWidth("Нажмите CTRL + ALT что-бы переключить сетку", 10) / 2,22);

        EventManager.call(new RenderEvent.AfterChat(context, mouseX, mouseY, delta));
    }

    @Inject(method = "mouseClicked", at = @At("HEAD"))
    private void mouseClicked(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
        DraggableRepository draggableRepository = Pasxalka.getInstance().getDraggableRepository();

        draggableRepository.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        DraggableRepository draggableRepository = Pasxalka.getInstance().getDraggableRepository();

        draggableRepository.mouseReleased(mouseX, mouseY, button);

        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
    public void keyPressed(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        DraggableRepository draggableRepository = Pasxalka.getInstance().getDraggableRepository();

        if (draggableRepository.keyPressed(keyCode, scanCode, modifiers)) {
            cir.setReturnValue(false);
        }
    }
}
