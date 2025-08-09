package jaypasha.funpay.api.events.impl;

/*
 * Create by puzatiy
 * At 03.06.2025
 */

import jaypasha.funpay.api.events.EventLayer;
import jaypasha.funpay.modules.more.ModuleLayer;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

public class ModuleEvent extends EventLayer {

    @Getter
    @RequiredArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    public static class ToggleEvent extends ModuleEvent {
        ModuleLayer moduleLayer;
    }

}
