package me.yamakaja.rpgpets.plugin.command;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

/**
 * Created by Yamakaja on 1/2/18.
 */
public class ReloadPreprocessor implements Listener {

    @EventHandler
    public void onCommandPreprocess(PlayerCommandPreprocessEvent event) {
        if (event.getMessage().equals("/reload")) {
            Player player = event.getPlayer();
            player.sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + "===================================");
            player.sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + "   RPGPets does not support /reload,");
            player.sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + "         use at your own risk!");
            player.sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + " Run " + ChatColor.GREEN + ChatColor.BOLD + "/reload confirm " + ChatColor.RED + ChatColor.BOLD + "to reload anyways");
            player.sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + "===================================");
            event.setCancelled(true);
        }
    }

}
