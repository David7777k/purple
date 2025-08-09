package jaypasha.funpay.invoke;

/*
 * Create by puzatiy
 * At 03.06.2025
 */

import jaypasha.funpay.api.events.EventManager;
import jaypasha.funpay.api.events.impl.PacketEvent;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.network.packet.Packet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientConnection.class)
public class ClientConnectionInvoke {

    @Inject(method = "handlePacket", at = @At("HEAD"), cancellable = true)
    private static void handlePacketReceived(Packet<? extends PacketListener> packet, PacketListener listener, CallbackInfo ci) {
        PacketEvent packetEvent = new PacketEvent(packet, PacketEvent.PacketEventType.RECEIVE);
        EventManager.call(packetEvent);

        if (packetEvent.isCanceled()) ci.cancel();
    }

    @Inject(method = "send(Lnet/minecraft/network/packet/Packet;)V", at = @At("HEAD"), cancellable = true)
    private void handlePacketSend(Packet<?> packet, CallbackInfo ci) {
        PacketEvent packetEvent = new PacketEvent(packet, PacketEvent.PacketEventType.SEND);
        EventManager.call(packetEvent);

        if (packetEvent.isCanceled()) ci.cancel();
    }

}
