package jaypasha.funpay.api.commands.list.blockESP;

import com.google.common.collect.Lists;
import jaypasha.funpay.api.commands.CommandLayer;
import jaypasha.funpay.api.commands.list.defaultArguments.AddArgumentLayer;
import jaypasha.funpay.api.commands.list.defaultArguments.RemoveArgumentLayer;

import java.util.List;

public class BlockESPLayerCommand extends CommandLayer {

    public BlockESPLayerCommand() {
        super(Lists.newArrayList("blockesp","besp"));

        getArguments().add(new AddArgumentLayer(0, BlockESPRepository::getBlocks));
        getArguments().add(new RemoveArgumentLayer(0, BlockESPRepository::getBlocks));
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
