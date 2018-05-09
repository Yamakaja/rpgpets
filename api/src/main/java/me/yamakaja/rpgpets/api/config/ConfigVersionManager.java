package me.yamakaja.rpgpets.api.config;

import me.yamakaja.rpgpets.api.RPGPets;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Created by Yamakaja on 5/9/18.
 */
public class ConfigVersionManager {

    private static final int CURRENT_VERSION = 1;
    private RPGPets plugin;
    private int configVersion = 0;

    public ConfigVersionManager(RPGPets plugin) {
        this.plugin = plugin;

        readConfigVersion();
    }

    private void readConfigVersion() {
        this.plugin.getDataFolder().mkdir();

        File versionFile = new File(this.plugin.getDataFolder(), ".config-version.dat");

        if (Objects.requireNonNull(this.plugin.getDataFolder().list()).length == 0) {
            System.out.println("Loading default configs ...");
            saveResources();
            this.configVersion = CURRENT_VERSION;
            updateConfigVersion();
            return;
        }

        if (!versionFile.exists())
            return;

        try (DataInputStream reader = new DataInputStream(new FileInputStream(versionFile))) {
            this.configVersion = reader.readUnsignedShort();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void saveResources() {
        boolean debug = System.getProperty("me.yamakaja.debug") != null;

        for (String file : new String[]{
                "config.yml",
                "messages.yml",
                "permissions.yml",
                "pets/chicken.yml",
                "pets/cow.yml",
                "pets/llama.yml",
                "pets/mushroom_cow.yml",
                "pets/pig.yml",
                "pets/pig_zombie.yml",
                "pets/polar_bear.yml",
                "pets/rabbit.yml",
                "pets/README",
                "pets/sheep.yml",
                "pets/villager.yml",
                "pets/wolf.yml",
                "pets/zombie.yml"})
            this.plugin.saveResource(file, debug);
    }

    /**
     * Run all required transformers until the current version has been reached
     */
    public void migrateConfiguration() {
        this.plugin.getLogger().info("[Config] Checking config version ...");

        VersionTransformer[] transformers = VersionTransformer.values();

        if (this.configVersion > CURRENT_VERSION)
            throw new RuntimeException("Unknown configuration version! It appears that you have downgraded your RPGPets" +
                    "version, which is not supported!");

        if (this.configVersion == CURRENT_VERSION) {
            saveResources();
            return;
        }

        this.plugin.getLogger().info("[Config] Starting config migration to schema version " + CURRENT_VERSION);

        while (this.configVersion < CURRENT_VERSION)
            transformers[this.configVersion].accept(this);

        updateConfigVersion();
        saveResources();
    }

    public void updateConfigVersion() {
        File file = new File(this.plugin.getDataFolder(), ".config-version.dat");
        try (DataOutputStream outputStream = new DataOutputStream(new FileOutputStream(file))) {
            outputStream.writeShort(CURRENT_VERSION);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private enum VersionTransformer implements Consumer<ConfigVersionManager> {

        VERSION_0_to_1(it -> {
            // To update:   - Separate pet stats into multiple files
            //              - Add new file for villager and wolf
            String[] petTypes = {"chicken", "cow", "llama", "mushroom_cow", "pig", "pig_zombie", "polar_bear", "rabbit",
                    "sheep", "zombie"};

            File petStatsFile = new File(it.plugin.getDataFolder(), "petstats.yml");
            FileConfiguration petStats = YamlConfiguration.loadConfiguration(petStatsFile);

            // Create newly introduced directory
            File petsDir = new File(it.plugin.getDataFolder(), "pets");
            petsDir.mkdirs();

            for (String type : petTypes) {
                it.plugin.getLogger().info("[Config] Moving petstats.yml:" + type + " to pets/" + type + ".yml");
                File petSpecificFile = new File(petsDir, type + ".yml");
                try {
                    petSpecificFile.createNewFile();
                    YamlConfiguration newConfig = YamlConfiguration.loadConfiguration(petSpecificFile);
                    ConfigurationSection petConfigSection = petStats.getConfigurationSection(type);

                    for (String k : petConfigSection.getKeys(false))
                        newConfig.set(k, petConfigSection.get(k));

                    newConfig.save(petSpecificFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            it.plugin.getLogger().info("[Config] Saving new resources ...");
            it.plugin.saveResource("pets/wolf.yml", false);
            it.plugin.saveResource("pets/villager.yml", false);

            petStatsFile.delete();
            it.configVersion = 1;
        });

        private Consumer<ConfigVersionManager> consumer;

        VersionTransformer(Consumer<ConfigVersionManager> consumer) {
            this.consumer = consumer;
        }

        @Override
        public void accept(ConfigVersionManager configVersionManager) {
            this.consumer.accept(configVersionManager);
        }

    }

}
