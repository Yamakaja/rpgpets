package me.yamakaja.rpgpets.plugin;

import com.comphenix.protocol.ProtocolLibrary;
import me.yamakaja.rpgpets.api.NMSHandler;
import me.yamakaja.rpgpets.api.RPGPets;
import me.yamakaja.rpgpets.api.config.ConfigGeneral;
import me.yamakaja.rpgpets.api.config.ConfigManager;
import me.yamakaja.rpgpets.api.entity.PetManager;
import me.yamakaja.rpgpets.api.entity.PetType;
import me.yamakaja.rpgpets.api.hook.FeudalHook;
import me.yamakaja.rpgpets.api.item.EggManager;
import me.yamakaja.rpgpets.api.item.RPGPetsItem;
import me.yamakaja.rpgpets.api.item.RecipeManager;
import me.yamakaja.rpgpets.api.logging.ErrorLogHandler;
import me.yamakaja.rpgpets.api.logging.SentryManager;
import me.yamakaja.rpgpets.api.hook.Hooks;
import me.yamakaja.rpgpets.api.hook.PartiesHook;
import me.yamakaja.rpgpets.api.hook.WorldGuardHook;
import me.yamakaja.rpgpets.plugin.command.CommandRPGPets;
import me.yamakaja.rpgpets.plugin.protocol.EntitySpawnPacketTranslator;
import me.yamakaja.rpgpets.v1_11_R1.NMSHandler_v1_11_R1;
import me.yamakaja.rpgpets.v1_12_R1.NMSHandler_v1_12_R1;
import org.bstats.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 * Created by Yamakaja on 10.06.17.
 */
@SuppressWarnings("unused")
public class RPGPetsImpl extends JavaPlugin implements RPGPets {

    private SentryManager sentryManager;

    private NMSHandler nmsHandler;
    private ConfigManager configManager;

    private PetManager petManager;
    private EggManager eggManager;
    private RecipeManager recipeManager;

    @Override
    public void onEnable() {
        this.configManager = new ConfigManager(this);
        this.configManager.injectConfigs();
        this.getLogger().info("Configs loaded!");

        this.sentryManager = new SentryManager(this, "%%__USER__%%");
        Logger.getLogger("").addHandler(new ErrorLogHandler(this.sentryManager));

        if (ConfigGeneral.ENABLE_METRICS.getAsBoolean())
            new Metrics(this);

        this.getCommand("rpgpets").setExecutor(new CommandRPGPets(this));

        this.sentryManager.recordInitializationCrumb("Loading NMSHandler");
        if (!this.loadNMSHandler()) {
            this.getPluginLoader().disablePlugin(this);
            return;
        }
        this.sentryManager.recordInitializationCrumb("Loaded NMSHandler for version " + this.getNMSHandler().getNMSVersion());

        if (Bukkit.getPluginManager().getPlugin("Parties") != null) {
            this.getLogger().info("Parties detected! Enabling hook!");
            this.sentryManager.recordInitializationCrumb("Initializing Parties hook");
            Hooks.PARTIES.enable();
            PartiesHook.initialize();
        }

        if (Bukkit.getPluginManager().getPlugin("WorldGuard") != null) {
            this.getLogger().info("WorldGuard detected! Enabling hook!");
            this.sentryManager.recordInitializationCrumb("Initializing WorldGuard hook");
            Hooks.WORLDGUARD.enable();
            WorldGuardHook.initialize();
        }

        if (Bukkit.getPluginManager().getPlugin("Feudal") != null) {
            this.getLogger().info("Feudal detected! Enabling hook!");
            this.sentryManager.recordInitializationCrumb("Initializing Feudal hook!");
            Hooks.FEUDAL.enable();
            FeudalHook.initialize();
        }

        RPGPetsItem.initialize(this);

        this.getLogger().info("Loaded for NMS version " + this.getNMSHandler().getNMSVersion() + "!");

        this.sentryManager.recordInitializationCrumb("Registering packet handler with ProtocolLib " + ProtocolLibrary.getPlugin().getDescription().getVersion());
        ProtocolLibrary.getProtocolManager().addPacketListener(new EntitySpawnPacketTranslator(this));

        this.sentryManager.recordInitializationCrumb("Registering pets");
        this.registerPets();
        this.getLogger().info("Registered pet entities!");

        this.sentryManager.recordInitializationCrumb("Registering managers");
        this.petManager = new PetManager(this);
        this.eggManager = new EggManager(this);
        this.recipeManager = new RecipeManager(this);
        Bukkit.getOnlinePlayers().forEach(p -> this.eggManager.update(p));

        this.getLogger().info("Successfully enabled RPGPets!");
        this.sentryManager.clearContext();
    }

    @Override
    public void onDisable() {
        this.sentryManager.recordShutdownCrumb("Cleaning up pets");

        if (this.getPetManager() != null)
            this.getPetManager().cleanup();
    }

    private void registerPets() {
        for (PetType petType : PetType.values())
            this.getNMSHandler().getPetRegistry().registerEntity(petType.getEntityId(), petType.getBaseType(), petType.getEntityClass(), petType.getEntityName());
    }

    /**
     * Loads the {@link NMSHandler} for the current version
     *
     * @return Whether loading a suitable handler was successful
     */
    private boolean loadNMSHandler() {
        String nmsVersion;
        try {
            nmsVersion = Bukkit.getServer().getClass().getPackage().getName().split(Pattern.quote("."))[3];
        } catch (Exception ex) {
            this.getLogger().log(Level.SEVERE, "An error occurred while determining server version! Disabling plugin ...", ex);
            return false;
        }

        switch (nmsVersion) {
            case "v1_11_R1":
                nmsHandler = new NMSHandler_v1_11_R1(this);
                break;
            case "v1_12_R1":
                nmsHandler = new NMSHandler_v1_12_R1(this);
                break;
            default:
                this.getLogger().severe("*****************************************************");
                this.getLogger().severe("Unsupported version: \"" + nmsVersion + "\". Disabling plugin!");
                this.getLogger().severe("*****************************************************");
                return false;
        }
        return true;
    }

    @Override
    public NMSHandler getNMSHandler() {
        return nmsHandler;
    }

    @Override
    public ConfigManager getConfigManager() {
        return configManager;
    }

    @Override
    public PetManager getPetManager() {
        return petManager;
    }

    @Override
    public RecipeManager getRecipeManager() {
        return recipeManager;
    }

    @Override
    public SentryManager getSentryManager() {
        return sentryManager;
    }

}
