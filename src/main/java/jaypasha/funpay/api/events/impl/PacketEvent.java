package jaypasha.funpay.api.events.impl;

/*
 * Create by puzatiy
 * At 03.06.2025
 */

import jaypasha.funpay.api.events.EventLayer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.network.packet.Packet;

@Getter
@AllArgsConstructor
public class PacketEvent extends EventLayer {

    Packet<?> packet;
    PacketEventType packetEventType;

    public enum PacketEventType {
        SEND,
        RECEIVE
    }
}
