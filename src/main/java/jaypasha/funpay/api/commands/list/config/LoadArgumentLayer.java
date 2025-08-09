package jaypasha.funpay.api.commands.list.config;

import jaypasha.funpay.Pasxalka;
import jaypasha.funpay.api.commands.ArgumentLayer;

import java.util.List;

public class LoadArgumentLayer extends ArgumentLayer {

    public LoadArgumentLayer() {
        super("load", 0);
    }

    @Override
    public void execute(List<String> arguments) {
        if (arguments.isEmpty()) {
            print("не так незя брад");
            return;
        }

        Pasxalka.getInstance().getConfigurationService().load(arguments.getFirst());
        print("Конфигурация загружена.");
    }
}
