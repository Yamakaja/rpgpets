package me.yamakaja.rpgpets.api.entity;

import me.yamakaja.rpgpets.api.RPGPets;
import me.yamakaja.rpgpets.api.config.ConfigMessages;
import me.yamakaja.rpgpets.api.event.PetLevelUpEvent;
import me.yamakaja.rpgpets.api.item.RPGPetsItem;
import me.yamakaja.rpgpets.api.util.PartiesHook;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
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
     * @param slot          The slot to save
     * @return Whether or not the pet has been spawned, this may be false when the player already has a pet active
     */
    public LivingEntity summon(PetDescriptor petDescriptor, int slot) {
        if (this.spawnedPets.containsKey(petDescriptor.getOwner().getName()))
            return null;

        LivingEntity entity = this.plugin.getNMSHandler().addToWorld(petDescriptor.getPetType().summon(petDescriptor),
                petDescriptor.getOwner().getWorld());

        entity.setVelocity(petDescriptor.getOwner().getLocation().getDirection());

        this.spawnedPets.put(petDescriptor.getOwner().getName(), entity);
        this.petSlots.put(petDescriptor.getOwner().getName(), slot);

        return entity;
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
                event.getPlayer().getInventory().setItemInMainHand(RPGPetsItem.resetPet(stack));
            else
                event.getPlayer().getInventory().setItemInOffHand(RPGPetsItem.resetPet(stack));

            if (entity.getEntityId() != petDescriptor.getEntityId())
                return;

            this.unregisterFromPlayer(event.getPlayer());
            entity.remove();
            return;
        }

        if (petDescriptor.getState() != PetState.READY) {
            event.getPlayer().sendMessage(ConfigMessages.GENERAL_STATUS.get());
            return;
        }

        LivingEntity entity = this.summon(petDescriptor, event.getHand() == EquipmentSlot.HAND
                ? event.getPlayer().getInventory().getHeldItemSlot() : 40);

        ItemStack itemStack = RPGPetsItem.encodeSpawned(event.getItem(), entity.getEntityId());

        if (event.getHand() == EquipmentSlot.HAND)
            event.getPlayer().getInventory().setItemInMainHand(itemStack);
        else
            event.getPlayer().getInventory().setItemInOffHand(itemStack);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        if (!this.spawnedPets.containsKey(e.getEntity().getName()))
            return;

        int slot = this.petSlots.get(e.getEntity().getName());

        ItemStack itemStackInInventory = e.getEntity().getInventory().getItem(slot);
        int dropsSlot = -1;
        for (int i = 0; i < e.getDrops().size(); i++)
            if (e.getDrops().get(i).isSimilar(itemStackInInventory)) {
                dropsSlot = i;
                break;
            }

        if (dropsSlot == -1)
            throw new RuntimeException("Logic error! Cannot find inventory item in drops!");

        LivingEntity entity = this.spawnedPets.get(e.getEntity().getName());
        PetDescriptor petDescriptor = this.plugin.getNMSHandler().getPetDescriptor(entity);
        petDescriptor.setState(PetState.DEAD);

        entity.remove();
        this.spawnedPets.remove(e.getEntity().getName());

        e.getDrops().set(dropsSlot, RPGPetsItem.getPetCarrier(petDescriptor));
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        if (!this.spawnedPets.containsKey(e.getPlayer().getName()))
            return;

        int slot = this.petSlots.get(e.getPlayer().getName());

        LivingEntity entity = this.spawnedPets.get(e.getPlayer().getName());
        PetDescriptor petDescriptor = this.plugin.getNMSHandler().getPetDescriptor(entity);

        entity.remove();
        this.spawnedPets.remove(e.getPlayer().getName());

        petDescriptor.setState(entity.getHealth() == entity.getMaxHealth() ? PetState.READY : PetState.DEAD);
        e.getPlayer().getInventory().setItem(slot, RPGPetsItem.getPetCarrier(petDescriptor));
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

        int slot = this.petSlots.get(e.getWhoClicked().getName());

        if (e.getClickedInventory() == null)
            return;

        if (e.getHotbarButton() == slot) {
            e.setCancelled(true);
            return;
        }

        if (e.getClickedInventory().getType() == InventoryType.PLAYER && e.getSlot() == slot)
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

        petDescriptor.setState(PetState.DEAD);
        this.unregisterFromPlayer(petDescriptor.getOwner());
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

    /**
     * Gets the {@link PetDescriptor} from the players pet and saves it to their inventory.
     * This does <b>NOT</b> de-spawn the pet.
     * Neither does this method check whether the player actually has a pet spawned
     *
     * @param player The player which has the pet spawned
     */
    private void unregisterFromPlayer(Player player) {
        String name = player.getName();
        PetDescriptor petDescriptor = this.plugin.getNMSHandler().getPetDescriptor(this.spawnedPets.get(name));

        player.getInventory().setItem(petSlots.get(name), RPGPetsItem.getPetCarrier(petDescriptor));

        this.spawnedPets.remove(name);
        this.petSlots.remove(name);
    }

    /**
     * Same as {@link PetManager#unregisterFromPlayer(Player)}
     */
    private void unregisterFromPlayer(String playerName) {
        PetDescriptor petDescriptor = this.plugin.getNMSHandler().getPetDescriptor(this.spawnedPets.get(playerName));

        Bukkit.getPlayer(playerName).getInventory().setItem(petSlots.get(playerName), RPGPetsItem.getPetCarrier(petDescriptor));

        this.spawnedPets.remove(playerName);
        this.petSlots.remove(playerName);
    }

    /**
     * Clean up all players pets
     */
    public void cleanup() {
        this.spawnedPets.forEach((playerName, entity) -> {

            PetDescriptor petDescriptor = this.plugin.getNMSHandler().getPetDescriptor(this.spawnedPets.get(playerName));

            if (petDescriptor == null)
                return;

            petDescriptor.setState(PetState.READY);
            Bukkit.getPlayer(playerName).getInventory().setItem(petSlots.get(playerName), RPGPetsItem.getPetCarrier(petDescriptor));

            entity.remove();
        });

        this.spawnedPets.clear();
        this.petSlots.clear();
    }

    @EventHandler
    public void onAnvilPrepare(PrepareAnvilEvent event) {
        ItemStack item = event.getInventory().getItem(0);
        PetDescriptor petDescriptor = RPGPetsItem.decode(item, null);

        if (event.getInventory().getItem(1) != null)
            return;

        if (petDescriptor == null)
            return;

        event.getInventory().setRepairCost(30);
        petDescriptor.setName(ChatColor.GOLD + ChatColor.stripColor(event.getInventory().getRenameText()).replace("§", ""));
        event.setResult(RPGPetsItem.getPetCarrier(petDescriptor));
    }

    @EventHandler
    public void onAnvilClick(InventoryClickEvent event) {
        if (event.getClickedInventory() == null || event.getClickedInventory().getType() != InventoryType.ANVIL)
            return;

        if (event.getSlot() != 0 && event.getSlot() != 1)
            return;

        ItemStack stack = event.getCursor();

        if (event.getAction() == InventoryAction.HOTBAR_MOVE_AND_READD || event.getAction() == InventoryAction.HOTBAR_SWAP) {
            stack = event.getWhoClicked().getInventory().getItem(event.getHotbarButton());
        }

        if (stack.getType() == Material.AIR)
            return;

        PetDescriptor petDescriptor = RPGPetsItem.decode(stack, null);

        if (petDescriptor == null)
            return;

        if (!petDescriptor.getName().equals(ConfigMessages.ITEM_PET_DEFAULTNAME.get())) {
            event.getWhoClicked().sendMessage(ConfigMessages.GENERAL_NAMEONCE.get());
            event.setCancelled(true);
            ((Player) event.getWhoClicked()).updateInventory();
        }
    }

    @EventHandler
    public void onAnvilDrag(InventoryDragEvent event) {
        if (event.getInventory().getType() != InventoryType.ANVIL)
            return;

        PetDescriptor petDescriptor = RPGPetsItem.decode(event.getOldCursor(), null);
        if (petDescriptor == null)
            return;

        if (event.getRawSlots().contains(0) || event.getRawSlots().contains(1)) {
            event.setCancelled(true);
            event.getWhoClicked().sendMessage(ConfigMessages.GENERAL_NAMEONCE.get());
        }

    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
        if (!(e.getEntity() instanceof LivingEntity) || !(e.getDamager() instanceof LivingEntity))
            return;

        if (e.getDamager().getType() == EntityType.PLAYER) {
            PetDescriptor petDescriptor = this.plugin.getNMSHandler().getPetDescriptor((LivingEntity) e.getEntity());
            if (petDescriptor == null)
                return;

            if (PartiesHook.areInSameParty((Player) e.getDamager(), petDescriptor.getOwner()))
                e.setCancelled(true);
            return;
        }

        if (e.getEntity().getType() == EntityType.PLAYER) {
            PetDescriptor petDescriptor = this.plugin.getNMSHandler().getPetDescriptor((LivingEntity) e.getDamager());
            if (petDescriptor == null)
                return;

            if (PartiesHook.areInSameParty((Player) e.getEntity(), petDescriptor.getOwner()))
                e.setCancelled(true);

            return;
        }

        PetDescriptor damager = this.plugin.getNMSHandler().getPetDescriptor((LivingEntity) e.getDamager());
        PetDescriptor entity = this.plugin.getNMSHandler().getPetDescriptor((LivingEntity) e.getEntity());

        if (damager == null || entity == null)
            return;

        if (PartiesHook.areInSameParty(damager.getOwner(), entity.getOwner()))
            e.setCancelled(true);

    }

}
