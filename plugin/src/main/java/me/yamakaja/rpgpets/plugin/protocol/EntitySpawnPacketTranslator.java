package me.yamakaja.rpgpets.plugin.protocol;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import org.bukkit.plugin.Plugin;

/**
 * Created by Yamakaja on 11.06.17.
 */
public class EntitySpawnPacketTranslator extends PacketAdapter {

    public EntitySpawnPacketTranslator(Plugin plugin) {
        super(plugin, PacketType.Play.Server.SPAWN_ENTITY_LIVING);
    }

    @Override
    public void onPacketSending(PacketEvent event) {
        event.getPacket().getIntegers().write(1, event.getPacket().getIntegers().read(1) % 1000);
    }

}
