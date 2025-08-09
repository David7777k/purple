package jaypasha.funpay.api.events;

/*
 * Create by puzatiy
 * At 03.06.2025
 */

import jaypasha.funpay.Pasxalka;

public class EventManager {

    public static void register(Class<?> clazz) {
        Pasxalka.getInstance().getEventBus().register(clazz);
    }

    public static void call(EventLayer eventLayer) {
        Pasxalka.getInstance().getEventBus().post(eventLayer);
    }

    public static void cancel(EventLayer eventLayer) {
        eventLayer.cancel();
    }

    public static boolean isCancel(EventLayer eventLayer) {
        return eventLayer.isCanceled();
    }

}
