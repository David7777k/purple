package jaypasha.funpay.api.commands.list.defaultArguments;

import jaypasha.funpay.api.commands.ArgumentLayer;

import java.util.List;
import java.util.function.Supplier;

public class RemoveArgumentLayer extends ArgumentLayer {

    List<String> list;

    public RemoveArgumentLayer(Integer index, Supplier<List<String>> listSupplier) {
        super("remove", index);
        this.list = listSupplier.get();
    }

    @Override
    public void execute(List<String> arguments) {
        if (arguments.isEmpty()) {
            print("Не так не получится брад");
            return;
        }

        if (!list.contains(arguments.getFirst())) return;

        list.remove(arguments.getFirst());
        print("Успешно!");
    }
}
