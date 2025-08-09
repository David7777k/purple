package jaypasha.funpay;

import net.fabricmc.api.ClientModInitializer;
import jaypasha.funpay.ui.NotificationManager;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class ModClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        NotificationManager.init();

        // Тестовое уведомление при запуске клиента (чтобы проверить)
        NotificationManager.post(
                Text.of("Привет!"),
                Text.of("Уведомления работают!"),
                Formatting.GREEN,
                5000L
        );
        System.out.println("NotificationManager initialized and test notification posted.");
    }
}
