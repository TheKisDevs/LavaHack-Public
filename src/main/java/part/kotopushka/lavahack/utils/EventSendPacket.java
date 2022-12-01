package part.kotopushka.lavahack.utils;

import net.minecraft.network.Packet;

public class EventSendPacket
        extends EventCancellable {
    public Packet<?> packet;

    public EventSendPacket(Packet<?> packet) {
        this.packet = packet;
    }

    public Packet<?> getPacket() {
        return this.packet;
    }

    public void setPacket(Packet<?> packet) {
        this.packet = packet;
    }
}

