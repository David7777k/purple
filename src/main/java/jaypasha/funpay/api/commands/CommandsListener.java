package jaypasha.funpay.api.commands;

import jaypasha.funpay.Pasxalka;
import jaypasha.funpay.api.events.impl.PacketEvent;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;

import java.util.Arrays;

import com.google.common.eventbus.Subscribe;
import java.util.List;

public class CommandsListener {

    public CommandsListener() {
        Pasxalka.getInstance().getEventBus().register(this);
    }

    @Subscribe
    public void listener(PacketEvent packetEvent) {
        if (!(packetEvent.getPacket() instanceof ChatMessageC2SPacket packet)) return;

        String message = packet.chatMessage().trim();
        if (!message.startsWith(".")) return;

        packetEvent.cancel();

        List<String> args = Arrays.asList(message.substring(1).split("\\s+"));
        if (args.isEmpty()) return;

        String commandName = args.getFirst();

        CommandsRepository.getCommandLayer().stream()
                .filter(layer -> layer.getCommands().stream().anyMatch(cmd -> cmd.equalsIgnoreCase(commandName)))
                .findFirst()
                .ifPresent(layer -> layer.execute(args.subList(1, args.size())));
    }
}

