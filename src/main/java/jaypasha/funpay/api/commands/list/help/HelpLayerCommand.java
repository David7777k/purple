package jaypasha.funpay.api.commands.list.help;

import com.google.common.collect.Lists;
import jaypasha.funpay.api.commands.CommandLayer;

import java.util.List;

public class HelpLayerCommand extends CommandLayer {

    final String helpMessage =
            """
            .help  ->
            
            .gps ( x y / off )
            .staff (add <name> | remove <name> |  list / clear)
            .s ( a  <name>| r <name> |  l | c )
            
            .bESP (add <name> | remove <name> | list / clear)
            .cfg (save  <name> | dir  |  load  <name> |clear)
            
            .friend (add <name> | remove <name> |  list  | clear)
            .f ( a  <name>| r <name> |  l | c )
            """;

    public HelpLayerCommand() {
        super(Lists.newArrayList("help"));
    }

    @Override
    public void execute(List<String> arguments) {
        print(helpMessage);
    }
}
