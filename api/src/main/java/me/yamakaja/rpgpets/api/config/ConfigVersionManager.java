package me.yamakaja.rpgpets.api.config;

import me.yamakaja.rpgpets.api.RPGPets;
import me.yamakaja.rpgpets.api.entity.PetType;
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

    private static final int CURRENT_VERSION = 3;
    private RPGPets plugin;
    private int configVersion = 0;

    public ConfigVersionManager(RPGPets plugin) {
        this.plugin = plugin;

        readConfigVersion();
    }

    private void readConfigVersion() {
        File versionFile = new File(this.plugin.getDataFolder(), ".config-version.dat");

        this.plugin.getDataFolder().mkdir();
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
                "items.yml",
                "pets/chicken.yml",
                "pets/cow.yml",
                "pets/donkey.yml",
                "pets/horse.yml",
                "pets/llama.yml",
                "pets/mushroom_cow.yml",
                "pets/ocelot.yml",
                "pets/pig.yml",
                "pets/pig_zombie.yml",
                "pets/polar_bear.yml",
                "pets/rabbit.yml",
                "pets/README",
                "pets/sheep.yml",
                "pets/villager.yml",
                "pets/wolf.yml",
                "pets/zombie.yml"})
            this.saveResource(file, debug);
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

        for (; this.configVersion < CURRENT_VERSION; configVersion++)
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

    public void saveResource(String resourcePath, boolean replace) {
        if (resourcePath == null || resourcePath.isEmpty())
            throw new IllegalArgumentException("ResourcePath cannot be null or empty!");

        int lastIndex = resourcePath.lastIndexOf('/');
        File outDir = new File(this.plugin.getDataFolder(), resourcePath.substring(0, lastIndex >= 0 ? lastIndex : 0));
        if (!outDir.exists())
            outDir.mkdirs();

        File outFile = new File(this.plugin.getDataFolder(), resourcePath);
        if (outFile.exists() && !replace)
            return;

        try (InputStream in = this.plugin.getResource(resourcePath);
             OutputStream out = new FileOutputStream(outFile)) {
            byte[] buf = new byte[1024];
            int len;

            while ((len = in.read(buf)) > 0)
                out.write(buf, 0, len);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save resource \"" + resourcePath + "\": ", e);
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
        }),
        VERSION_1_TO_2(it -> {
            // To update:   - Add "command.give.everyone" to messages.yml

            File messageConfigFile = new File(it.plugin.getDataFolder(), "messages.yml");
            YamlConfiguration messagesConfig = YamlConfiguration.loadConfiguration(messageConfigFile);
            messagesConfig.set("command.give.everyone", "all online players");
            try {
                messagesConfig.save(messageConfigFile);
            } catch (IOException e) {
                throw new RuntimeException("Failed to migrate config!", e);
            }
        }),
        VERSION_2_TO_3(it -> {
            // To update:   - Add "pet.<pet>: <name>" entries to messages.yml

            File messageConfigFile = new File(it.plugin.getDataFolder(), "messages.yml");
            YamlConfiguration messagesConfig = YamlConfiguration.loadConfiguration(messageConfigFile);

            PetType[] types = new PetType[]{
                    PetType.CHICKEN,
                    PetType.COW,
                    PetType.DONKEY,
                    PetType.HORSE,
                    PetType.LLAMA,
                    PetType.MUSHROOM_COW,
                    PetType.OCELOT,
                    PetType.PIG,
                    PetType.PIG_ZOMBIE,
                    PetType.POLAR_BEAR,
                    PetType.RABBIT,
                    PetType.SHEEP,
                    PetType.VILLAGER,
                    PetType.WOLF,
                    PetType.ZOMBIE};

            String[] names = new String[]{
                    "Chicken",
                    "Cow",
                    "Donkey",
                    "Horse",
                    "Llama",
                    "Mushroom Cow",
                    "Ocelot",
                    "Pig",
                    "Zombie Pigman",
                    "Polar Bear",
                    "Rabbit",
                    "Sheep",
                    "Villager",
                    "Wolf",
                    "Zombie"
            };

            // noinspection ConstantConditions
            for (int i = 0; i < types.length && i < names.length; i++)
                messagesConfig.set("typename." + types[i].name().toLowerCase(), names);

            try {
                messagesConfig.save(messageConfigFile);
            } catch (IOException e) {
                throw new RuntimeException("Failed to migrate config!", e);
            }
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
