package me.yamakaja.rpgpets.api.config;

import me.yamakaja.rpgpets.api.RPGPets;
import me.yamakaja.rpgpets.api.entity.PetType;
import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

/**
 * Created by Yamakaja on 10.06.17.
 */
public class ConfigManager {

    private RPGPets plugin;

    private ConfigVersionManager versionManager;

    public ConfigManager(RPGPets plugin) {
        this.plugin = plugin;
        this.versionManager = new ConfigVersionManager(this.plugin);

        this.versionManager.migrateConfiguration();
    }

    /**
     * Load config values and initialize enums
     */
    public void injectConfigs() throws InvalidConfigurationException, IOException {
        File dataDir = plugin.getDataFolder();

        // General
        File generalConfigFile = new File(dataDir, "config.yml");

        YamlConfiguration generalConfig = new YamlConfiguration();
        generalConfig.load(generalConfigFile);
        ConfigGeneral.initialize(generalConfig);

        // Messages
        File messagesConfigFile = new File(dataDir, "messages.yml");

        YamlConfiguration messagesConfig = new YamlConfiguration();
        messagesConfig.load(messagesConfigFile);

        String rawPrefix = messagesConfig.getString("prefix");
        String prefix = ChatColor.translateAlternateColorCodes('&', rawPrefix == null ? "" : rawPrefix);

        for (ConfigMessages message : ConfigMessages.values())
            message.setMessage(ChatColor.translateAlternateColorCodes('&',
                    messagesConfig.getString(message.name().replace('_', '.').toLowerCase()))
                    .replace("{prefix}", prefix));

        for (PetType type : PetType.values())
            type.setTypeName(messagesConfig.getString("typename." + type.name().toLowerCase()));

        // Permissions
        File permissionsConfigFile = new File(dataDir, "permissions.yml");

        YamlConfiguration permissionsConfig = new YamlConfiguration();
        permissionsConfig.load(permissionsConfigFile);

        for (ConfigPermissions permission : ConfigPermissions.values())
            permission.set(permissionsConfig.getString(permission.name().replace('_', '.').toLowerCase()));

        for (PetType type : PetType.values()) {
            YamlConfiguration petStats = YamlConfiguration.loadConfiguration(new File(this.plugin.getDataFolder(),
                    "pets/" + type.name().toLowerCase() + ".yml"));

            type.initStats(
                    petStats.getInt("maxLevel"),
                    (float) petStats.getDouble("baseExpRequirement"),
                    (float) petStats.getDouble("expRequirementMultiplier"),
                    petStats.getInt("randomWeight"),
                    (float) petStats.getDouble("base.speed"),
                    (float) petStats.getDouble("base.attackDamage"),
                    (float) petStats.getDouble("base.knockback"),
                    (float) petStats.getDouble("base.maxHealth"),
                    (float) petStats.getDouble("levelup.speed"),
                    (float) petStats.getDouble("levelup.attackDamage"),
                    (float) petStats.getDouble("levelup.knockback"),
                    (float) petStats.getDouble("levelup.maxHealth"),
                    (float) petStats.getDouble("babymodifier.speed"),
                    (float) petStats.getDouble("babymodifier.attackDamage"),
                    (float) petStats.getDouble("babymodifier.knockback"),
                    (float) petStats.getDouble("babymodifier.maxHealth"));
        }

        PetType.initWeightMap();

        // Items
        ConfigItems.initialize(plugin, YamlConfiguration.loadConfiguration(new File(dataDir, "items.yml")));
    }

}
