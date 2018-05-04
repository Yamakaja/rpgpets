package me.yamakaja.rpgpets.api.item;

import me.yamakaja.rpgpets.api.RPGPets;
import me.yamakaja.rpgpets.api.config.ConfigMessages;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Yamakaja on 12.06.17.
 */
public class EggManager implements Listener, Runnable {

    private RPGPets plugin;

    private Map<Player, Integer> carryingPlayers = new HashMap<>();

    public EggManager(RPGPets plugin) {
        this.plugin = plugin;

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        plugin.getServer().getScheduler().runTaskTimer(plugin, this, 0, 20 * 5);
    }

    /**
     * Checks whether the passed item is a mystery egg
     *
     * @param itemStack The {@link ItemStack} to check
     * @return true if the passed item is a mystery egg
     */
    public static boolean isItemEgg(ItemStack itemStack) {
        if (itemStack.getType() != Material.MONSTER_EGG || !itemStack.hasItemMeta())
            return false;

        ItemMeta meta = itemStack.getItemMeta();

        return meta.hasDisplayName() && meta.getDisplayName().equals(ConfigMessages.ITEM_EGG_NAME.get())
                && meta.hasLore() && meta.getLore().size() == 3 && meta.getLore().get(2).startsWith(ChatColor.BLACK.toString() +
                ChatColor.MAGIC);
    }

    @Override
    public void run() {
        carryingPlayers.forEach((player, startDistance) -> {
            ItemStack eggStack = player.getInventory().getItemInOffHand();

            if (!isItemEgg(eggStack)) {
                update(player);
                return;
            }

            ItemMeta meta = eggStack.getItemMeta();
            List<String> lore = meta.getLore();

            int distanceToGo = Integer.parseInt(ChatColor.stripColor(lore.get(2)).split(":")[0]);
            int totalDistance = getDistanceMoved(player);
            distanceToGo -= totalDistance - this.carryingPlayers.get(player);

            if (distanceToGo <= 0) {
                player.sendMessage(ConfigMessages.ITEM_EGG_HATCH.get());
                player.getInventory().setItemInOffHand(RPGPetsItem.PET.get());
                update(player);
                return;
            }

            this.carryingPlayers.put(player, totalDistance);
            lore.set(0, ConfigMessages.ITEM_EGG_LORE_REMAINING.get(Integer.toString(distanceToGo / 100)));
            lore.set(2, ChatColor.BLACK.toString() + ChatColor.MAGIC + Integer.toString(distanceToGo) + ":"
                    + Double.toString(Math.random()).substring(2));

            meta.setLore(lore);
            eggStack.setItemMeta(meta);
            player.getInventory().setItemInOffHand(eggStack);
        });
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        update(e.getPlayer());
    }

    @EventHandler
    public void onGameModeChange(PlayerGameModeChangeEvent e) {
        update(e.getPlayer());
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent e) {
        this.carryingPlayers.remove(e.getPlayer());
    }

    @EventHandler
    public void onInventoryInteract(InventoryClickEvent e) {
        if (e.getSlot() != 40)
            return;

        update((Player) e.getWhoClicked());
    }

    @EventHandler
    public void onPlayerSwapHandItems(PlayerSwapHandItemsEvent e) {
        update(e.getPlayer());
    }

    public void update(final Player player) {

        new BukkitRunnable() {
            @Override
            public void run() {

                if (carryingPlayers.containsKey(player) &&
                        (player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.SPECTATOR)) {
                    carryingPlayers.remove(player);
                    return;
                }

                ItemStack stack = player.getInventory().getItemInOffHand();

                if (stack == null || stack.getType() != Material.MONSTER_EGG || !stack.hasItemMeta()) {
                    carryingPlayers.remove(player);
                    return;
                }

                ItemMeta meta = stack.getItemMeta();

                if (!meta.hasDisplayName() || !meta.hasLore() ||
                        !meta.getDisplayName().equals(ConfigMessages.ITEM_EGG_NAME.get())) {
                    carryingPlayers.remove(player);
                    return;
                }

                if (carryingPlayers.containsKey(player.getPlayer()))
                    return;

                carryingPlayers.put(player, getDistanceMoved(player));
            }

        }.runTask(plugin);

    }

    /**
     * Returns the distance the player moved
     *
     * @param player The player to check
     * @return Distance in cm
     */
    private int getDistanceMoved(Player player) {
        return player.getStatistic(Statistic.WALK_ONE_CM)
                + player.getStatistic(Statistic.SPRINT_ONE_CM)
                + player.getStatistic(Statistic.SWIM_ONE_CM)
                + player.getStatistic(Statistic.CROUCH_ONE_CM)
                + player.getStatistic(Statistic.DIVE_ONE_CM);
    }

}
