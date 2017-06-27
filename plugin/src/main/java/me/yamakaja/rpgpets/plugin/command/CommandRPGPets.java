package me.yamakaja.rpgpets.plugin.command;

import com.getsentry.raven.event.Breadcrumb;
import com.getsentry.raven.event.BreadcrumbBuilder;
import me.yamakaja.rpgpets.api.RPGPets;
import me.yamakaja.rpgpets.api.config.ConfigMessages;
import me.yamakaja.rpgpets.api.config.ConfigPermissions;
import me.yamakaja.rpgpets.api.item.RPGPetsItem;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Yamakaja on 10.06.17.
 */
public class CommandRPGPets implements CommandExecutor, TabCompleter {

    private RPGPets plugin;
    private List<String> subcommands = Arrays.asList("give", "help");
    ;

    public CommandRPGPets(RPGPets plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String alias, String[] args) {
        this.plugin.getSentryManager().recordBreadcrumb(
                new BreadcrumbBuilder().setMessage("Command executed").setCategory("Command").setLevel(Breadcrumb.Level.DEBUG)
                        .setData(Collections.singletonMap("args", String.join(" ", args))).build()
        );

        if (args.length == 0) {
            commandSender.sendMessage(ConfigMessages.COMMAND_HELP_HINT.get());
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "give":
                if (!commandSender.hasPermission(ConfigPermissions.COMMAND_GIVE.get())) {
                    commandSender.sendMessage(ConfigMessages.COMMAND_NOPERM.get());
                    return true;
                }

                processGive(commandSender, args);
                break;
            case "help":
                if (!commandSender.hasPermission(ConfigPermissions.COMMAND_HELP.get())) {
                    commandSender.sendMessage(ConfigMessages.COMMAND_NOPERM.get());
                    return true;
                }

                commandSender.sendMessage(ConfigMessages.COMMAND_HELP_SUBCOMMANDS.get());
                commandSender.sendMessage(ConfigMessages.COMMAND_HELP_SUBCOMMAND.get("help",
                        ConfigMessages.COMMAND_HELP_DESCRIPTION.get()));
                commandSender.sendMessage(ConfigMessages.COMMAND_HELP_SUBCOMMAND.get("give",
                        ConfigMessages.COMMAND_GIVE_DESCRIPTION.get()));
                break;
            default:
                commandSender.sendMessage(ConfigMessages.COMMAND_HELP_HINT.get());
        }

        return true;
    }

    private void processGive(CommandSender sender, String[] args) {
        if (args.length <= 2) {
            sender.sendMessage(ConfigMessages.COMMAND_SYNTAX.get(ConfigMessages.COMMAND_GIVE_SYNTAX.get()));
            return;
        }

        Player player = Bukkit.getPlayer(args[1]);
        if (player == null) {
            sender.sendMessage(ConfigMessages.COMMAND_GIVE_TARGETNOTFOUND.get());
            return;
        }

        RPGPetsItem item;
        try {
            item = RPGPetsItem.valueOf(args[2].toUpperCase());
        } catch (IllegalArgumentException e) {
            sender.sendMessage(ConfigMessages.COMMAND_GIVE_UNKNOWN.get());
            return;
        }

        ItemStack stack = item.get();

        if (args.length >= 4) {
            try {
                stack.setAmount(Math.min(64, Integer.parseInt(args[3])));
            } catch (NumberFormatException e) {
            }
        }

        if (player.getInventory().firstEmpty() == -1)
            player.getWorld().dropItem(player.getLocation(), stack);
        else
            player.getInventory().addItem(stack);

        sender.sendMessage(ConfigMessages.COMMAND_GIVE_SUCCESS.get(Integer.toString(stack.getAmount()),
                stack.hasItemMeta() && stack.getItemMeta().hasDisplayName() ? stack.getItemMeta().getDisplayName() : item.name(), player.getName()));

    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] args) {
        if (args.length == 0) {
            return subcommands;
        }

        if (args.length == 1)
            return subcommands.stream().filter(cmd -> cmd.startsWith(args[0])).collect(Collectors.toList());

        if (args.length >= 2 && args[0].equals("give")) {
            if (args.length == 2)
                return Bukkit.getOnlinePlayers().stream().map(Player::getName).filter(player -> player.startsWith(args[1]))
                        .collect(Collectors.toList());
            if (args.length == 3)
                return Arrays.stream(RPGPetsItem.values()).map(Enum::name).filter(name -> name.startsWith(args[2])).collect(Collectors.toList());
        }
        return null;
    }

}
