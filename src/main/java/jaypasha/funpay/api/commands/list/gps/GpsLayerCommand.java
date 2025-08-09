package jaypasha.funpay.api.commands.list.gps;

import com.google.common.collect.Lists;
import jaypasha.funpay.api.commands.CommandLayer;

import java.util.List;

public class GpsLayerCommand extends CommandLayer {

    public GpsLayerCommand() {
        super(Lists.newArrayList("gps"));

        getArguments().add(new AddArgumentLayer());
        getArguments().add(new RemoveArgumentLayer());
    }

    @Override
    public void execute(List<String> arguments) {
        if (arguments.isEmpty()) {
            print("Не так не получится брад");
            return;
        }

        getArguments().stream()
                .filter(arg -> arg.getIndex() < arguments.size())
                .filter(arg -> arguments.get(arg.getIndex()).equalsIgnoreCase(arg.getArgument()))
                .forEach(arg -> arg.execute(arguments.subList(1, arguments.size())));
    }
}
