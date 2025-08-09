package jaypasha.funpay.api.commands.list.defaultArguments;

import jaypasha.funpay.api.commands.ArgumentLayer;

import java.util.List;
import java.util.function.Supplier;

public class AddArgumentLayer extends ArgumentLayer {

    List<String> list;

    public AddArgumentLayer(Integer index, Supplier<List<String>> listSupplier) {
        super("add", index);
        this.list = listSupplier.get();
    }

    @Override
    public void execute(List<String> arguments) {
        if (arguments.isEmpty()) {
            print("Не так не получится брад");
            return;
        }

        if (list.contains(arguments.getFirst())) return;

        list.add(arguments.getFirst());
        print("Успешно!");
    }
}
