package me.yamakaja.rpgpets.api;

import me.yamakaja.rpgpets.api.config.ConfigManager;
import me.yamakaja.rpgpets.api.entity.PetManager;
import org.bukkit.plugin.Plugin;

/**
 * Created by Yamakaja on 10.06.17.
 */
public interface RPGPets extends Plugin {

    /**
     * Get the {@link NMSHandler} providing version specific implementations
     *
     * @return The {@link NMSHandler}
     */
    NMSHandler getNMSHandler();

    /**
     * Gets the {@link ConfigManager} which manages the configuration files
     *
     * @return The {@link ConfigManager}
     */
    ConfigManager getConfigManager();

    /**
     * Gets the {@link PetManager} which manages spawned pets on the server
     *
     * @return The {@link PetManager}
     */
    PetManager getPetManager();

}
