package me.yamakaja.rpgpets.api.entity;

import me.yamakaja.rpgpets.api.RPGPets;
import me.yamakaja.rpgpets.api.config.ConfigMessages;
import me.yamakaja.rpgpets.api.event.PetLevelUpEvent;
import me.yamakaja.rpgpets.api.item.RPGPetsItem;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Yamakaja on 12.06.17.
 */
public class PetManager implements Listener {

    private Map<String, LivingEntity> spawnedPets = new HashMap<>();
    private Map<String, Integer> petSlots = new HashMap<>();
    private RPGPets plugin;

    public PetManager(RPGPets plugin) {
        this.plugin = plugin;

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    /**
     * Attempts to summon a pet using the {@link PetDescriptor}
     *
     * @param petDescriptor The {@link PetDescriptor} describing the summoning job
     * @param slot
     * @return Whether or not the pet has been spawned, this may be false when the player already has a pet active
     */
    public boolean summon(PetDescriptor petDescriptor, int slot) {
        if (this.spawnedPets.containsKey(petDescriptor.getOwner().getName()))
            return false;

        this.spawnedPets.put(petDescriptor.getOwner().getName(), this.plugin.getNMSHandler().summon(petDescriptor));
        this.petSlots.put(petDescriptor.getOwner().getName(), slot);

        return true;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (!(event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK))
            return;

        if (!event.hasItem())
            return;

        PetDescriptor petDescriptor = RPGPetsItem.decode(event.getItem(), event.getPlayer());

        if (petDescriptor == null)
            return;

        event.setCancelled(true);

        ItemStack stack = event.getItem();

        if (this.spawnedPets.containsKey(event.getPlayer().getName())) {
            if (!petDescriptor.hasEntityId())
                return;

            LivingEntity entity = this.spawnedPets.get(event.getPlayer().getName());

            if (entity.getHealth() != entity.getMaxHealth()) {
                event.getPlayer().sendMessage(ConfigMessages.GENERAL_PETHEALTH.get());
                return;
            }

            if (event.getHand() == EquipmentSlot.HAND)
                event.getPlayer().getInventory().setItemInMainHand(RPGPetsItem.removeSpawned(stack));
            else
                event.getPlayer().getInventory().setItemInOffHand(RPGPetsItem.removeSpawned(stack));

            if (entity.getEntityId() != petDescriptor.getEntityId())
                return;

            this.unregisterFromPlayer(event.getPlayer());
            entity.remove();
            return;
        }

        this.summon(petDescriptor, event.getPlayer().getInventory().getHeldItemSlot());

        LivingEntity entity = this.spawnedPets.get(event.getPlayer().getName());

        ItemStack itemStack = RPGPetsItem.encodeSpawned(event.getItem(), entity.getEntityId());

        if (event.getHand() == EquipmentSlot.HAND)
            event.getPlayer().getInventory().setItemInMainHand(itemStack);
        else
            event.getPlayer().getInventory().setItemInOffHand(itemStack);
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent e) {
        PetDescriptor petDescriptor = RPGPetsItem.decode(e.getItemDrop().getItemStack(), e.getPlayer());

        if (petDescriptor != null && petDescriptor.hasEntityId())
            e.setCancelled(true);
    }

    @EventHandler
    public void onPetLevelUp(PetLevelUpEvent e) {
        PetDescriptor descriptor = e.getPetDescriptor();
        descriptor.getOwner().sendMessage(ConfigMessages.GENERAL_LEVELUP.get(descriptor.getName(),
                Integer.toString(descriptor.getLevel())));
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (!this.spawnedPets.containsKey(e.getWhoClicked().getName()))
            return;

        if (e.getSlot() == this.petSlots.get(e.getWhoClicked().getName()))
            e.setCancelled(true);
    }

    @EventHandler
    public void onPlayerSwapItems(PlayerSwapHandItemsEvent e) {
        if (!this.spawnedPets.containsKey(e.getPlayer().getName()))
            return;

        if (e.getPlayer().getInventory().getHeldItemSlot() == this.petSlots.get(e.getPlayer().getName()))
            e.setCancelled(true);
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent e) {
        if (!this.spawnedPets.containsValue(e.getEntity()))
            return;

        PetDescriptor petDescriptor = this.plugin.getNMSHandler().getPetDescriptor(e.getEntity());

        if (petDescriptor == null)
            return;

        e.setDroppedExp(0);
        e.getDrops().clear();

        unregisterFromPlayer(petDescriptor.getOwner());
    }

    @EventHandler
    public void onEntityUnload(ChunkUnloadEvent event) {
        for (Entity entity : event.getChunk().getEntities()) {
            if (!(entity instanceof LivingEntity))
                continue;

            PetDescriptor petDescriptor = this.plugin.getNMSHandler().getPetDescriptor((LivingEntity) entity);

            if (petDescriptor == null)
                continue;

            unregisterFromPlayer(petDescriptor.getOwner());

            entity.remove();
        }
    }

    private void unregisterFromPlayer(Player player) {
        PetDescriptor petDescriptor = this.plugin.getNMSHandler().getPetDescriptor(this.spawnedPets.get(player.getName()));
        String ownerName = player.getName();
        this.spawnedPets.remove(ownerName);

        player.getInventory().setItem(petSlots.get(ownerName), RPGPetsItem.getPetCarrier(petDescriptor.getPetType(),
                petDescriptor.getName(), petDescriptor.getLevel(), petDescriptor.getExperience(),
                petDescriptor.getExperienceRequirement(), petDescriptor.isGrownUp()));

        this.petSlots.remove(ownerName);
    }

}
