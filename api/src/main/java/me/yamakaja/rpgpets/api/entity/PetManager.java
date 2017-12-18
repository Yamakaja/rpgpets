package me.yamakaja.rpgpets.api.entity;

import com.comphenix.packetwrapper.WrapperPlayServerWindowData;
import com.getsentry.raven.event.Breadcrumb;
import com.getsentry.raven.event.BreadcrumbBuilder;
import me.yamakaja.rpgpets.api.RPGPets;
import me.yamakaja.rpgpets.api.config.ConfigMessages;
import me.yamakaja.rpgpets.api.event.PetLevelUpEvent;
import me.yamakaja.rpgpets.api.hook.FeudalHook;
import me.yamakaja.rpgpets.api.hook.Hooks;
import me.yamakaja.rpgpets.api.hook.WorldGuardHook;
import me.yamakaja.rpgpets.api.item.RPGPetsItem;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
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
import org.bukkit.event.entity.EntityPortalEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.*;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.bukkit.event.inventory.InventoryType.ANVIL;

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
        this.plugin.getSentryManager().recordBreadcrumb(new BreadcrumbBuilder().setMessage("Summoning Pet").setLevel(Breadcrumb.Level.DEBUG)
                .setCategory("RUNTIME").setData(Collections.singletonMap("petDescriptor", petDescriptor.toString())).build());

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
    public void onGameModeChange(PlayerGameModeChangeEvent event) {
        if (event.getNewGameMode() != GameMode.CREATIVE && event.getNewGameMode() != GameMode.SPECTATOR)
            return;

        if (!this.spawnedPets.containsKey(event.getPlayer().getName()))
            return;

        LivingEntity entity = this.spawnedPets.get(event.getPlayer().getName());
        this.unregisterFromPlayer(event.getPlayer());
        entity.remove();
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (!(event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK))
            return;

        if (!event.hasItem())
            return;

        if (event.getPlayer().getGameMode() == GameMode.CREATIVE)
            return;

        PetDescriptor petDescriptor = RPGPetsItem.decode(event.getItem());

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

        ItemStack itemStack = event.getItem();

        if (petDescriptor.getState() != PetState.READY) {
            event.getPlayer().sendMessage(ConfigMessages.GENERAL_STATUS.get());
            return;
        }
        petDescriptor.setOwner(event.getPlayer());
        LivingEntity entity = this.summon(petDescriptor, event.getHand() == EquipmentSlot.HAND
                ? event.getPlayer().getInventory().getHeldItemSlot() : 40);

        itemStack = RPGPetsItem.encodeSpawned(itemStack, entity.getEntityId());

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
        PetDescriptor petDescriptor = RPGPetsItem.decode(e.getItemDrop().getItemStack());

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

        int petSlot = this.petSlots.get(e.getPlayer().getName());
        if (petSlot == 40 || e.getPlayer().getInventory().getHeldItemSlot() == petSlot)
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
        petDescriptor.getOwner().sendMessage(ConfigMessages.GENERAL_PETDEATH.get());
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
        if (!this.spawnedPets.containsKey(name))
            return;

        PetDescriptor petDescriptor = this.plugin.getNMSHandler().getPetDescriptor(this.spawnedPets.get(name));

        player.getInventory().setItem(petSlots.get(name), RPGPetsItem.getPetCarrier(petDescriptor));

        this.spawnedPets.remove(name);
        this.petSlots.remove(name);
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
        PetDescriptor petDescriptor = RPGPetsItem.decode(item);

        if (event.getInventory().getItem(1) != null)
            return;

        if (petDescriptor == null)
            return;

        event.getInventory().setRepairCost(30);

        WrapperPlayServerWindowData packet = new WrapperPlayServerWindowData();
        packet.setWindowId(this.plugin.getNMSHandler().getWindowId(event.getInventory()));
        packet.setProperty(0);
        packet.setValue(30);
        packet.sendPacket((Player) event.getViewers().get(0));

        petDescriptor.setName(ChatColor.GOLD + ChatColor.stripColor(event.getInventory().getRenameText()).replace("§", ""));
        event.setResult(RPGPetsItem.getPetCarrier(petDescriptor));
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent e) {
        if (e.isCancelled())
            return;

        if (e.getFrom() == null || e.getFrom().getWorld() == null || e.getTo() == null || e.getTo().getWorld() == null)
            return;

        if (e.getFrom().getWorld() == e.getTo().getWorld() && e.getFrom().distanceSquared(e.getTo()) < 30 * 30)
            return;

        if (!this.spawnedPets.containsKey(e.getPlayer().getName()))
            return;

        LivingEntity entity = this.spawnedPets.get(e.getPlayer().getName());
        PetDescriptor petDescriptor = this.plugin.getNMSHandler().getPetDescriptor(entity);

        petDescriptor.setState((entity.getHealth() == entity.getMaxHealth()) ? PetState.READY : PetState.DEAD);
        this.unregisterFromPlayer(e.getPlayer());
        entity.remove();
    }

    @EventHandler
    public void onPortal(EntityPortalEvent e) {
        if (e.getEntity() instanceof LivingEntity
                && this.plugin.getNMSHandler().getPetDescriptor((LivingEntity) e.getEntity()) != null
                && e.getTo().getWorld() != e.getFrom().getWorld())

            e.setCancelled(true);
    }

    @EventHandler
    public void onAnvilClick(InventoryClickEvent event) {
        PetDescriptor petDescriptor;

        if (event.getClick() == ClickType.SHIFT_LEFT || event.getClick() == ClickType.SHIFT_RIGHT) {
            if (event.getWhoClicked().getOpenInventory().getType() != ANVIL)
                return;

            ItemStack stack = event.getCurrentItem();

            if (stack == null || stack.getType() == Material.AIR)
                return;

            petDescriptor = RPGPetsItem.decode(stack);

        } else {
            if (event.getClickedInventory() == null || event.getClickedInventory().getType() != ANVIL)
                return;

            if (event.getSlot() != 0 && event.getSlot() != 1)
                return;

            ItemStack stack = event.getCursor();

            if (event.getAction() == InventoryAction.HOTBAR_MOVE_AND_READD || event.getAction() == InventoryAction.HOTBAR_SWAP) {
                stack = event.getWhoClicked().getInventory().getItem(event.getHotbarButton());
            }

            if (stack == null || stack.getType() == Material.AIR)
                return;

            petDescriptor = RPGPetsItem.decode(stack);

        }

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
        if (event.getInventory().getType() != ANVIL)
            return;

        PetDescriptor petDescriptor = RPGPetsItem.decode(event.getOldCursor());
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

        Player playerOne;
        Player playerTwo;

        if (e.getDamager().getType() == EntityType.PLAYER) {
            PetDescriptor petDescriptor = this.plugin.getNMSHandler().getPetDescriptor((LivingEntity) e.getEntity());
            if (petDescriptor == null)
                return;

            playerOne = (Player) e.getDamager();
            playerTwo = petDescriptor.getOwner();
        } else if (e.getEntity().getType() == EntityType.PLAYER) {
            PetDescriptor petDescriptor = this.plugin.getNMSHandler().getPetDescriptor((LivingEntity) e.getDamager());
            if (petDescriptor == null)
                return;

            playerOne = (Player) e.getEntity();
            playerTwo = petDescriptor.getOwner();
        } else {
            PetDescriptor damager = this.plugin.getNMSHandler().getPetDescriptor((LivingEntity) e.getDamager());
            PetDescriptor entity = this.plugin.getNMSHandler().getPetDescriptor((LivingEntity) e.getEntity());

            if (damager == null || entity == null)
                return;

            playerOne = damager.getOwner();
            playerTwo = entity.getOwner();
        }

        if (Hooks.WORLDGUARD.isEnabled() && !WorldGuardHook.isPvpEnabled(playerOne, e.getEntity().getLocation()))
            e.setCancelled(true);

        else if (Hooks.FEUDAL.isEnabled() && FeudalHook.areAllied(playerOne, playerTwo))
            e.setCancelled(true);
    }

}
