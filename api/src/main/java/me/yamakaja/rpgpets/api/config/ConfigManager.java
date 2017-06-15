package me.yamakaja.rpgpets.api.config;

import me.yamakaja.rpgpets.api.RPGPets;
import me.yamakaja.rpgpets.api.entity.PetType;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

/**
 * Created by Yamakaja on 10.06.17.
 */
public class ConfigManager {

    private RPGPets plugin;

    public ConfigManager(RPGPets plugin) {
        this.plugin = plugin;

        boolean debug = System.getProperty("me.yamakaja.debug") != null;

        plugin.saveResource("config.yml", debug);
        plugin.saveResource("messages.yml", debug);
        plugin.saveResource("permissions.yml", debug);
        plugin.saveResource("petstats.yml", debug);
    }

    /**
     * Load config values and initialize enums
     */
    public void injectConfigs() {
        File dataDir = plugin.getDataFolder();

        // General
        File generalConfigFile = new File(dataDir, "config.yml");

        YamlConfiguration generalConfig = YamlConfiguration.loadConfiguration(generalConfigFile);
        ConfigGeneral.initialize(generalConfig);

        // Messages
        File messagesConfigFile = new File(dataDir, "messages.yml");

        YamlConfiguration messagesConfig = YamlConfiguration.loadConfiguration(messagesConfigFile);

        String prefix = ChatColor.translateAlternateColorCodes('&', messagesConfig.getString("prefix"));

        for (ConfigMessages message : ConfigMessages.values())
            message.setMessage(ChatColor.translateAlternateColorCodes('&',
                    messagesConfig.getString(message.name().replace('_', '.').toLowerCase()))
                    .replace("{prefix}", prefix));

        // Permissions
        File permissionsConfigFile = new File(dataDir, "permissions.yml");

        YamlConfiguration permissionsConfig = YamlConfiguration.loadConfiguration(permissionsConfigFile);

        for (ConfigPermissions permission : ConfigPermissions.values())
            permission.set(permissionsConfig.getString(permission.name().replace('_', '.').toLowerCase()));

        // Pet Stats
        File petStatsFile = new File(dataDir, "petstats.yml");

        YamlConfiguration petStats = YamlConfiguration.loadConfiguration(petStatsFile);

        for (PetType type : PetType.values()) {
            String basePath = type.name().toLowerCase() + ".";

            type.initStats(
                    petStats.getInt(basePath + "maxLevel"),
                    (float) petStats.getDouble(basePath + "baseExpRequirement"),
                    (float) petStats.getDouble(basePath + "expRequirementMultiplier"),
                    petStats.getInt(basePath + "randomWeight"),

                    (float) petStats.getDouble(basePath + "base.speed"),
                    (float) petStats.getDouble(basePath + "base.attackDamage"),
                    (float) petStats.getDouble(basePath + "base.knockback"),
                    (float) petStats.getDouble(basePath + "base.maxHealth"),

                    (float) petStats.getDouble(basePath + "levelup.speed"),
                    (float) petStats.getDouble(basePath + "levelup.attackDamage"),
                    (float) petStats.getDouble(basePath + "levelup.knockback"),
                    (float) petStats.getDouble(basePath + "levelup.maxHealth"),

                    (float) petStats.getDouble(basePath + "babymodifier.speed"),
                    (float) petStats.getDouble(basePath + "babymodifier.attackDamage"),
                    (float) petStats.getDouble(basePath + "babymodifier.knockback"),
                    (float) petStats.getDouble(basePath + "babymodifier.maxHealth"));
        }

        PetType.initWeightMap();
    }

}
