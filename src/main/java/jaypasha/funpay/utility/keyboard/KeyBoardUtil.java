package jaypasha.funpay.utility.keyboard;

/*
 * Create by puzatiy
 * At 03.06.2025
 */

import jaypasha.funpay.Api;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;

import java.util.Locale;

import static net.minecraft.client.util.InputUtil.fromTranslationKey;

public class KeyBoardUtil implements Api {

    public static boolean isKeyPressed(KeyBinding key) {
        if (key.getDefaultKey().getCode() == -1) return false;

        return InputUtil.isKeyPressed(mc.getWindow().getHandle(), fromTranslationKey(key.getBoundKeyTranslationKey()).getCode());
    }

    public static String translate(int keyCodeIn) {
        InputUtil.Key key = keyCodeIn < 8 ?
                InputUtil.Type.MOUSE.createFromCode(keyCodeIn) :
                InputUtil.Type.KEYSYM.createFromCode(keyCodeIn);

        return keyCodeIn == -1 ? "N/A" : key.getTranslationKey()
                .replace("key.keyboard.", "")
                .replace("key.mouse.", "MOUSE ")
                .toUpperCase(Locale.ROOT);
    }

}
