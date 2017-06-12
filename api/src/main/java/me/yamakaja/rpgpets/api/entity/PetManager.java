package me.yamakaja.rpgpets.api.entity;

import me.yamakaja.rpgpets.api.RPGPets;
import me.yamakaja.rpgpets.api.config.ConfigMessages;
import me.yamakaja.rpgpets.api.event.PetLevelUpEvent;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Yamakaja on 12.06.17.
 */
public class PetManager implements Listener {

    private Map<Player, LivingEntity> spawnedPets = new HashMap<>();
    private RPGPets plugin;

    public PetManager(RPGPets plugin) {
        this.plugin = plugin;

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (!(event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK))
            return;

        ItemStack stack = event.getItem();
        if (stack.getType() != Material.SKULL_ITEM)
            return;

        SkullMeta meta = (SkullMeta) stack.getItemMeta();

        Set<ItemFlag> itemFlags = meta.getItemFlags();
        if (!itemFlags.contains(ItemFlag.HIDE_ENCHANTS) ||
                !itemFlags.contains(ItemFlag.HIDE_ATTRIBUTES))
            return;

        if (!meta.hasLore())
            return;

        PetDescriptor petDescriptor = readLore(meta.getDisplayName(), meta.getLore());

        // TODO: Parse pet items

    }

    private PetDescriptor readLore(String name, List<String> lore) {
        // TODO: Parse pet item lore
        return null;
    }

    @EventHandler
    public void onPetLevelup(PetLevelUpEvent e) {
        PetDescriptor descriptor = e.getPet().getPetDescriptor();
        descriptor.getOwner().sendMessage(ConfigMessages.GENERAL_LEVELUP.get(descriptor.getName(),
                Integer.toString(descriptor.getLevel())));
    }

    @EventHandler
    public void onEntityUnload(ChunkUnloadEvent event) {
        for (Entity entity : event.getChunk().getEntities()) {
            PetDescriptor petDescriptor = this.plugin.getNMSHandler().getPetDescriptor(entity);

            if (petDescriptor == null)
                continue;

            despawnPet(petDescriptor);

            entity.remove();
        }
    }

    private void despawnPet(PetDescriptor petDescriptor) {
        // TODO: Despawn pet
    }

}
