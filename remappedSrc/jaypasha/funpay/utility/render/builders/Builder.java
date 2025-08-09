package jaypasha.funpay.utility.render.builders;

import com.google.common.base.Suppliers;
import jaypasha.funpay.utility.render.builders.impl.BlurBuilder;
import jaypasha.funpay.utility.render.builders.impl.BorderBuilder;
import jaypasha.funpay.utility.render.builders.impl.RectangleBuilder;
import jaypasha.funpay.utility.render.builders.impl.TextureBuilder;
import jaypasha.funpay.utility.render.builders.impl.TextBuilder;
import jaypasha.funpay.utility.render.msdf.MsdfFont;
import java.util.function.Supplier;

public final class Builder {

    public static final RectangleBuilder RECTANGLE_BUILDER = new RectangleBuilder();
    public static final BorderBuilder BORDER_BUILDER = new BorderBuilder();
    public static final TextureBuilder TEXTURE_BUILDER = new TextureBuilder();
    public static final TextBuilder TEXT_BUILDER = new TextBuilder();
    public static final BlurBuilder BLUR_BUILDER = new BlurBuilder();

    public static final Supplier<MsdfFont> INTER = Suppliers.memoize(() -> MsdfFont.builder().atlas("inter").data("inter").name("inter").build());

}