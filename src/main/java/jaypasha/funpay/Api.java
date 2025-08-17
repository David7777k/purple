package jaypasha.funpay;

import jaypasha.funpay.utility.render.builders.Builder;
import jaypasha.funpay.utility.render.builders.impl.*;
import jaypasha.funpay.utility.render.msdf.MsdfFont;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

public interface Api {

    MinecraftClient mc = MinecraftClient.getInstance();

    /**
     * Оставляем как default-метод, чтобы классы, которые реализуют Api
     * могли просто вызывать print("...") без квалификации.
     */
    default void print(Object o) {
        try {
            if (mc == null || mc.inGameHud == null) return;
            mc.inGameHud.getChatHud().addMessage(Text.of(o == null ? "null" : o.toString()));
        } catch (Throwable t) {
            // Если в ранней инициализации ещё нет hud — падаем в stderr, но не спамим
            System.err.println("Api.print fallback: " + (o == null ? "null" : o.toString()));
        }
    }

    /**
     * В этой сборке явного CIRCLE_BUILDER может не быть — используем BLUR_BUILDER как рабочий fallback.
     */
    static BlurBuilder circle() {
        return Builder.BLUR_BUILDER;
    }

    static RectangleBuilder rectangle() {
        return Builder.RECTANGLE_BUILDER;
    }

    static BorderBuilder border() {
        return Builder.BORDER_BUILDER;
    }

    static TextureBuilder texture() {
        return Builder.TEXTURE_BUILDER;
    }

    static TextBuilder text() {
        return Builder.TEXT_BUILDER;
    }

    static BlurBuilder blur() {
        return Builder.BLUR_BUILDER;
    }

    static ShadowBuilder shadow() {
        return Builder.SHADOW_BUILDER;
    }

    static MsdfFont inter() {
        return Builder.INTER.get();
    }

    static MsdfFont icons() {
        return Builder.ICONS.get();
    }

    static MsdfFont hudIcons() {
        return Builder.HUD_ICONS.get();
    }
}
