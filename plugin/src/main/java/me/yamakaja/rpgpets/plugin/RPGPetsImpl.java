package me.yamakaja.rpgpets.plugin;

import com.comphenix.protocol.ProtocolLibrary;
import me.yamakaja.rpgpets.api.NMSHandler;
import me.yamakaja.rpgpets.api.RPGPets;
import me.yamakaja.rpgpets.api.config.ConfigManager;
import me.yamakaja.rpgpets.api.entity.PetManager;
import me.yamakaja.rpgpets.api.entity.PetType;
import me.yamakaja.rpgpets.api.item.EggManager;
import me.yamakaja.rpgpets.api.item.RPGPetsItem;
import me.yamakaja.rpgpets.api.item.RecipeManager;
import me.yamakaja.rpgpets.api.util.PartiesHook;
import me.yamakaja.rpgpets.plugin.command.CommandRPGPets;
import me.yamakaja.rpgpets.plugin.protocol.EntitySpawnPacketTranslator;
import me.yamakaja.rpgpets.v1_11_R1.NMSHandler_v1_11_R1;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.logging.Level;
import java.util.regex.Pattern;

/**
 * Created by Yamakaja on 10.06.17.
 */
@SuppressWarnings("unused")
public class RPGPetsImpl extends JavaPlugin implements RPGPets {

    private NMSHandler nmsHandler;
    private ConfigManager configManager;

    private PetManager petManager;
    private EggManager eggManager;
    private RecipeManager recipeManager;

    @Override
    public void onEnable() {
        this.getCommand("rpgpets").setExecutor(new CommandRPGPets(this));

        if (!this.loadNMSHandler()) {
            this.getPluginLoader().disablePlugin(this);
            return;
        }

        if (Bukkit.getPluginManager().getPlugin("Parties") != null) {
            this.getLogger().info("Parties detected! Enabling hook!");
            PartiesHook.initialize();
        }

        this.getLogger().info("Loaded for NMS version " + this.getNMSHandler().getNMSVersion() + "!");

        ProtocolLibrary.getProtocolManager().addPacketListener(new EntitySpawnPacketTranslator(this));

        RPGPetsItem.initialize(this);

        this.registerPets();
        this.getLogger().info("Registered pet entities!");

        this.configManager = new ConfigManager(this);
        this.configManager.injectConfigs();
        this.getLogger().info("Configs loaded!");

        this.petManager = new PetManager(this);
        this.eggManager = new EggManager(this);
        this.recipeManager = new RecipeManager(this);
        Bukkit.getOnlinePlayers().forEach(p -> this.eggManager.update(p));

        this.getLogger().info("Pre-loading skins ... this could take a bit");
        this.getLogger().info("Finished loading skins!");

        new BukkitRunnable() {
            @Override
            public void run() {
                getNMSHandler().preloadSkins();
            }
        }.runTaskTimerAsynchronously(RPGPetsImpl.this, 0, 20 * 60 * (60 - 1));

        this.getLogger().info("Successfully enabled RPGPets!");

    }

    @Override
    public void onDisable() {
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
}
