package me.yamakaja.rpgpets.plugin.protocol;

import com.comphenix.packetwrapper.WrapperPlayServerWindowData;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import org.bukkit.plugin.Plugin;

/**
 * Created by Yamakaja on 05.07.17.
 */
public class WindowPropertyPacketListener extends PacketAdapter {

    public WindowPropertyPacketListener(Plugin plugin) {
        super(plugin, PacketType.Play.Server.WINDOW_DATA);
    }

    @Override
    public void onPacketSending(PacketEvent event) {
        WrapperPlayServerWindowData windowData = new WrapperPlayServerWindowData(event.getPacket());

        plugin.getLogger().info("Window: " + windowData.getWindowId() + ", Property: " + windowData.getProperty()
                + ", Value: " + windowData.getValue());
    }

}
