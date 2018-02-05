package me.yamakaja.rpgpets.api.item;

import me.yamakaja.rpgpets.api.RPGPets;
import me.yamakaja.rpgpets.api.config.ConfigGeneral;
import org.bukkit.event.Listener;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Yamakaja on 2/5/18.
 */
public abstract class AbstractRevivalManager implements Listener, Runnable {

    private RPGPets plugin;

    private Map<UUID, Long> cooldowns = new HashMap<>();

    public AbstractRevivalManager(RPGPets plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        long cd = ConfigGeneral.FEED_COOLDOWN.getAsInt() * 1000;
        long currentTime = System.currentTimeMillis();
        this.cooldowns.entrySet().removeIf(entry -> currentTime > entry.getValue() + cd);
    }

    protected void setCooldown(UUID uuid) {
        this.cooldowns.put(uuid, System.currentTimeMillis());
    }

    protected boolean isCoolingDown(UUID uuid) {
        return this.cooldowns.getOrDefault(uuid, 0L) + ConfigGeneral.FEED_COOLDOWN.getAsLong() * 1000 >= System.currentTimeMillis();
    }

}
