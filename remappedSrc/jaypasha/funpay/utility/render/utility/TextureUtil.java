package jaypasha.funpay.utility.render.utility;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.util.Identifier;

public class TextureUtil {

    public static AbstractTexture of(String path) {
        return MinecraftClient.getInstance().getTextureManager().getTexture(Identifier.of("pasxalka", path));
    }

}
