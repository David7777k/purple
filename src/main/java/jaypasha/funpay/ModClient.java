package jaypasha.funpay;

import net.fabricmc.api.ClientModInitializer;
import jaypasha.funpay.ui.NotificationManager;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.lwjgl.glfw.GLFW;
import jaypasha.funpay.ui.clickGui.ClickGuiScreen;

public class ModClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        NotificationManager.init();

        KeyBinding modernGuiKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.pasxalka.modern_gui",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_RIGHT_SHIFT,
                "category.pasxalka.gui"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (modernGuiKey.wasPressed()) {
                try {
                    Pasxalka pas = Pasxalka.getInstance();
                    if (pas != null) {
                        // Если pasxalka инициализирован — используем его экран
                        if (pas.getModernClickGuiScreen() != null) {
                            client.setScreen(pas.getModernClickGuiScreen());
                        } else {
                            // на случай, если геттер вернул null по ошибке — создаём временный экран
                            client.setScreen(new ClickGuiScreen());
                        }
                    } else {
                        // Если Pasxalka ещё не инициализирован — fallback, создаём экран напрямую
                        client.setScreen(new ClickGuiScreen());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

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
