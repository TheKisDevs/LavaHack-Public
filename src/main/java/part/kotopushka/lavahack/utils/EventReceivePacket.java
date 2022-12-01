package part.kotopushka.lavahack.utils;

import net.minecraft.network.Packet;

public class EventReceivePacket
        extends EventCancellable {
    private Packet<?> packet;

    public EventReceivePacket(Packet<?> packet) {
        this.packet = packet;
    }

    public Packet<?> getPacket() {
        return this.packet;
    }

    public void setPacket(Packet<?> packet) {
        this.packet = packet;
    }
}


