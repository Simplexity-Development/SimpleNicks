package simplexity.simplenicks.util;

import com.github.retrooper.packetevents.PacketEvents;
import simplexity.simplenicks.listener.NamePacketListener;

public class PacketStuff {
    public static void registerPacketStuff(){
        PacketEvents.getAPI().getEventManager().registerListener(new NamePacketListener());
        PacketEvents.getAPI().load();
    }
}
