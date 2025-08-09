package jaypasha.funpay.api.commands.list.config;

import jaypasha.funpay.Pasxalka;
import jaypasha.funpay.api.commands.ArgumentLayer;

import java.util.List;

public class SaveArgumentLayer extends ArgumentLayer {

    public SaveArgumentLayer() {
        super("save", 0);
    }

    @Override
    public void execute(List<String> arguments) {
        if (arguments.isEmpty()) {
            print("не так незя брад");
            return;
        }

        Pasxalka.getInstance().getConfigurationService().save(arguments.getFirst());
        print("Конфигурация сохранена.");
    }
}
