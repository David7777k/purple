package jaypasha.funpay.utility.render.builders;

import com.google.common.base.Suppliers;
import jaypasha.funpay.utility.render.builders.impl.*;
import jaypasha.funpay.utility.render.msdf.MsdfFont;
import org.spongepowered.asm.mixin.Shadow;

import java.util.function.Supplier;

public final class Builder {

    public static final RectangleBuilder RECTANGLE_BUILDER = new RectangleBuilder();
    public static final BorderBuilder BORDER_BUILDER = new BorderBuilder();
    public static final TextureBuilder TEXTURE_BUILDER = new TextureBuilder();
    public static final ShadowBuilder SHADOW_BUILDER = new ShadowBuilder();
    public static final TextBuilder TEXT_BUILDER = new TextBuilder();
    public static final BlurBuilder BLUR_BUILDER = new BlurBuilder();
    public static final Supplier<MsdfFont> INTER = Suppliers.memoize(() -> MsdfFont.builder().atlas("inter").data("inter").name("inter").build());
    public static final Supplier<MsdfFont> ICONS = Suppliers.memoize(() -> MsdfFont.builder().atlas("icons").data("icons").name("icons").build());
    public static final Supplier<MsdfFont> HUD_ICONS = Suppliers.memoize(() -> MsdfFont.builder().atlas("hudicons").data("hudicons").name("hudicons").build());
}