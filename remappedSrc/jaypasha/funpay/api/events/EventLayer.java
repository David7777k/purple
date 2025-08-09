package jaypasha.funpay.api.events;

/*
 * Create by puzatiy
 * At 03.06.2025
 */

import lombok.Getter;

public class EventLayer {

    @Getter
    protected boolean canceled = false;

    public void cancel() {
        this.canceled = true;
    }
}
