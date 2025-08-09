package jaypasha.funpay;

/*
 * Create by puzatiy
 * At 03.06.2025
 */

import jaypasha.funpay.utility.render.builders.Builder;
import jaypasha.funpay.utility.render.builders.impl.*;
import jaypasha.funpay.utility.render.msdf.MsdfFont;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

public interface Api {

    MinecraftClient mc = MinecraftClient.getInstance();

    default void print(Object o) {
        if (mc == null) return;

        mc.inGameHud.getChatHud().addMessage(Text.of(o.toString()));
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

    static TextBuilder text() { return Builder.TEXT_BUILDER; }

    static BlurBuilder blur() {
        return Builder.BLUR_BUILDER;
    }

    static ShadowBuilder shadow() { return Builder.SHADOW_BUILDER; }

    static MsdfFont inter() { return Builder.INTER.get(); }

    static MsdfFont icons() { return Builder.ICONS.get(); }

    static MsdfFont hudIcons() { return Builder.HUD_ICONS.get(); }

}
