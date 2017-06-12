package me.yamakaja.rpgpets.plugin.item;

import me.yamakaja.rpgpets.api.RPGPets;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by david on 12.06.17.
 */
public class EggManager implements Listener, Runnable {

    private RPGPets plugin;

    private Map<Player, Float> carryingPlayers = new HashMap<>();

    public EggManager(RPGPets plugin) {
        this.plugin = plugin;

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        plugin.getServer().getScheduler().runTaskTimer(plugin, this, 0, 20 * 60);
    }

    @Override
    public void run() {
//        Bukkit.getOnlinePlayers().forEach(player -> {
//            ItemStack stack = player.getInventory().getItemInOffHand();
//
//            if (stack.getType() != Material.MONSTER_EGG || stack.getAmount() != 1 || !stack.hasItemMeta())
//                return;
//
//            ItemMeta meta = stack.getItemMeta();
//            if (!meta.hasDisplayName())
//                return;
//
//            if (!meta.getDisplayName().equals(ConfigMessages.ITEM_EGG_NAME.get()))
//                return;
//
//            List<String> lore = meta.getLore();
//            try {
//                int start = Integer.parseInt(ChatColor.stripColor(lore.get(1)));
//                int togo = getDistanceMoved(player) - start;
//                lore.set(0, ConfigMessages.ITEM_EGG_REMAINING.get(Integer.toString(togo / 100)));
//            } catch (IndexOutOfBoundsException | NumberFormatException e) {
//            }
//
//        });
    }

//    public void onInventoryInteract(InventoryInteractEvent e) {
//        System.out.println("Interacted with inventory!");
//    }

//    @SuppressWarnings("SuspiciousMethodCalls")
//    @EventHandler
//    public void onPlayerOpenInventory(InventoryOpenEvent e) {
//        if (!carryingPlayers.contains(e.getPlayer()))
//            return;
//
//        ItemStack[] inventory = e.getPlayer().getInventory().getContents();
//        for (int i = 0; i < inventory.length; i++) {
//            if (inventory[i].getType() != Material.MONSTER_EGG)
//                continue;
//
//            ItemStack egg = inventory[i];
//            if (!egg.hasItemMeta() || egg.getAmount() != 1)
//                continue;
//
//            ItemMeta eggMeta = egg.getItemMeta();
//            if (!eggMeta.hasDisplayName())
//                continue;
//
//            if (!eggMeta.getDisplayName().equals(ConfigMessages.ITEM_EGG_NAME.get()))
//                continue;
//
//            List<String> lore;
//
//            if (!eggMeta.hasLore()) {
//                lore = new ArrayList<>();
//
//
//            }
//
//        }
//
//    }

//    private List<String> initialize(Player player) {
//        List<String> lore = new ArrayList<>(2);
//        int distanceMoved = getDistanceMoved(player);
//        lore.add(ConfigMessages.ITEM_EGG_REMAINING.get("5000"));
//
//        return lore;
//    }

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
