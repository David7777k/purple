package jaypasha.funpay.api.commands.list.config;

import com.google.common.collect.Lists;
import jaypasha.funpay.api.commands.CommandLayer;


import java.util.List;

public class ConfigLayerCommand extends CommandLayer {

    public ConfigLayerCommand() {
        super(Lists.newArrayList("config", "cfg"));

        getArguments().add(new DirArgumentLayer());
        getArguments().add(new LoadArgumentLayer());
        getArguments().add(new SaveArgumentLayer());
    }

    @Override
    public void execute(List<String> arguments) {
        if (arguments.isEmpty()) {
            print("Не так не получится брат");
            return;
        }

        getArguments().stream()
                .filter(arg -> arg.getIndex() < arguments.size())
                .filter(arg -> arguments.get(arg.getIndex()).equalsIgnoreCase(arg.getArgument()))
                .forEach(arg -> arg.execute(arguments.subList(1, arguments.size())));
    }
}
