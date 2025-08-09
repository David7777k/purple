package jaypasha.funpay.api.commands;

import jaypasha.funpay.api.commands.list.blockESP.BlockESPLayerCommand;
import jaypasha.funpay.api.commands.list.config.ConfigLayerCommand;
import jaypasha.funpay.api.commands.list.friend.FriendLayerCommand;
import jaypasha.funpay.api.commands.list.gps.GpsLayerCommand;
import jaypasha.funpay.api.commands.list.help.HelpLayerCommand;
import jaypasha.funpay.api.commands.list.staffs.StaffsLayerCommand;
import lombok.Getter;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class CommandsRepository {

    @Getter
    private static final List<CommandLayer> commandLayer = new CopyOnWriteArrayList<>();

    static {
        commandLayer.addAll(List.of(
                new ConfigLayerCommand(),
                new HelpLayerCommand(),
                new BlockESPLayerCommand(),
                new FriendLayerCommand(),
                new GpsLayerCommand(),
                new StaffsLayerCommand()
        ));
    }
}
