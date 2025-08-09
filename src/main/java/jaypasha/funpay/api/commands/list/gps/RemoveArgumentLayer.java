package jaypasha.funpay.api.commands.list.gps;

import jaypasha.funpay.api.commands.ArgumentLayer;

import java.util.List;

public class RemoveArgumentLayer extends ArgumentLayer {

    public RemoveArgumentLayer() {
        super("remove", 0);
    }

    @Override
    public void execute(List<String> arguments) {
        if (arguments.isEmpty()) {
            print("Не так не получится брад");
            return;
        }

        GpsRepository.getGps().removeIf(e -> e.getName().equalsIgnoreCase(arguments.getFirst()));
        print("Успешно!");
    }
}
