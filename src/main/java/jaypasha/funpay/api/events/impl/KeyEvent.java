package jaypasha.funpay.api.events.impl;

/*
 * Create by puzatiy
 * At 03.06.2025
 */

import jaypasha.funpay.api.events.EventLayer;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Getter
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class KeyEvent extends EventLayer {

    long window;
    int key;
    int scancode;
    int action;
    int modifiers;

}
