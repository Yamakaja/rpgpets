package me.yamakaja.rpgpets.plugin.command;

import com.getsentry.raven.event.Breadcrumb;
import com.getsentry.raven.event.BreadcrumbBuilder;
import me.yamakaja.rpgpets.api.RPGPets;
import me.yamakaja.rpgpets.api.config.ConfigGeneral;
import me.yamakaja.rpgpets.api.config.ConfigMessages;
import me.yamakaja.rpgpets.api.config.ConfigPermissions;
import me.yamakaja.rpgpets.api.entity.PetDescriptor;
import me.yamakaja.rpgpets.api.entity.PetType;
import me.yamakaja.rpgpets.api.item.RPGPetsItem;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Yamakaja on 10.06.17.
 */
public class CommandRPGPets implements CommandExecutor, TabCompleter {

    private RPGPets plugin;
    private List<String> subcommands = Arrays.asList("give", "help", "minify", "deminify", "reload");

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
            if (checkPerms(commandSender, ConfigPermissions.COMMAND_HELP.get()))
                commandSender.sendMessage(ConfigMessages.COMMAND_HELP_HINT.get());
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "give":
                if (!checkPerms(commandSender, ConfigPermissions.COMMAND_GIVE.get()))
                    return true;

                processGive(commandSender, args);
                break;
            case "help":
                if (!checkPerms(commandSender, ConfigPermissions.COMMAND_HELP.get()))
                    return true;


                commandSender.sendMessage(ConfigMessages.COMMAND_HELP_SUBCOMMANDS.get());
                commandSender.sendMessage(ConfigMessages.COMMAND_HELP_SUBCOMMAND.get("help", ConfigMessages.COMMAND_HELP_DESCRIPTION.get()));
                commandSender.sendMessage(ConfigMessages.COMMAND_HELP_SUBCOMMAND.get("give", ConfigMessages.COMMAND_GIVE_DESCRIPTION.get()));
                commandSender.sendMessage(ConfigMessages.COMMAND_HELP_SUBCOMMAND.get("minify", ConfigMessages.COMMAND_MINIFY_DESCRIPTION.get()));
                commandSender.sendMessage(ConfigMessages.COMMAND_HELP_SUBCOMMAND.get("deminify", ConfigMessages.COMMAND_DEMINIFY_DESCRIPTION.get()));
                commandSender.sendMessage(ConfigMessages.COMMAND_HELP_SUBCOMMAND.get("reload", ConfigMessages.COMMAND_RELOAD_DESCRIPTION.get()));
                break;

            case "minify": {
                if (!checkPerms(commandSender, ConfigPermissions.COMMAND_MINIFY.get()))
                    return true;


                if (!(commandSender instanceof Player)) {
                    commandSender.sendMessage(ConfigMessages.COMMAND_NOCONSOLE.get());
                    return true;
                }

                Player player = (Player) commandSender;
                ItemStack item = player.getInventory().getItemInMainHand();
                PetDescriptor petDescriptor = RPGPetsItem.decode(item);

                if (petDescriptor == null) {
                    player.sendMessage(ConfigMessages.COMMAND_MINIFY_ITEM.get());
                    return true;
                }

                if (petDescriptor.getEntityId() != 0) {
                    player.sendMessage(ConfigMessages.COMMAND_MINIFY_ACTIVE.get());
                    return true;
                }

                if (petDescriptor.isMinified()) {
                    player.sendMessage(ConfigMessages.COMMAND_MINIFY_MINIFIED.get());
                    return true;
                }

                if (petDescriptor.getLevel() < ConfigGeneral.MINIFY_LEVEL.getAsInt()) {
                    player.sendMessage(ConfigMessages.COMMAND_MINIFY_LEVEL.get(String.valueOf(ConfigGeneral.MINIFY_LEVEL.getAsInt())));
                    return true;
                }

                petDescriptor.setMinified(true);
                player.getInventory().setItemInMainHand(RPGPetsItem.getPetCarrier(petDescriptor));
                player.sendMessage(ConfigMessages.COMMAND_MINIFY_SUCCESS.get());
                break;
            }

            case "deminify": {
                if (!checkPerms(commandSender, ConfigPermissions.COMMAND_DEMINIFY.get()))
                    return true;


                if (!(commandSender instanceof Player)) {
                    commandSender.sendMessage(ConfigMessages.COMMAND_NOCONSOLE.get());
                    return true;
                }

                Player player = (Player) commandSender;
                ItemStack item = player.getInventory().getItemInMainHand();
                PetDescriptor petDescriptor = RPGPetsItem.decode(item);

                if (petDescriptor == null) {
                    player.sendMessage(ConfigMessages.COMMAND_MINIFY_ITEM.get());
                    return true;
                }

                if (petDescriptor.getEntityId() != 0) {
                    player.sendMessage(ConfigMessages.COMMAND_MINIFY_ACTIVE.get());
                    return true;
                }

                if (!petDescriptor.isMinified()) {
                    player.sendMessage(ConfigMessages.COMMAND_DEMINIFY_NORMAL.get());
                    return true;
                }

                petDescriptor.setMinified(false);
                player.getInventory().setItemInMainHand(RPGPetsItem.getPetCarrier(petDescriptor));
                player.sendMessage(ConfigMessages.COMMAND_DEMINIFY_SUCCESS.get());
                break;
            }

            case "reload":
                if (!commandSender.isOp()) {
                    commandSender.sendMessage(ConfigMessages.COMMAND_NOPERM.get());
                    return true;
                }

                try {
                    this.plugin.getConfigManager().injectConfigs();
                    commandSender.sendMessage(ChatColor.GREEN + "Successfully reloaded RPGPets config!");
                } catch (InvalidConfigurationException | IOException e) {
                    e.printStackTrace();
                    commandSender.sendMessage(ChatColor.RED + "An error occurred while reloading the RPGPets config, please check the console!");
                }
                break;
            default:
                if (checkPerms(commandSender, ConfigPermissions.COMMAND_HELP.get()))
                    commandSender.sendMessage(ConfigMessages.COMMAND_HELP_HINT.get());
        }

        return true;
    }

    /**
     * Checks if the player has the passed permission, and if no, sends them the appropriate error message
     *
     * @param sender     The command sender that initiated the command
     * @param permission The permission to check
     * @return Whether the check succeeded
     */
    private boolean checkPerms(CommandSender sender, String permission) {
        if (sender.hasPermission(permission))
            return true;

        if (!sender.hasPermission(ConfigPermissions.COMMAND_HELP.get())) {
            sender.sendMessage(ChatColor.AQUA + "Running " + ChatColor.RED + "RPGPets" + ChatColor.AQUA + " version "
                    + ChatColor.RED + this.plugin.getDescription().getVersion() + ChatColor.AQUA + " by "
                    + ChatColor.RED + "Yamakaja");
            return false;
        }

        sender.sendMessage(ConfigMessages.COMMAND_NOPERM.get());
        return false;
    }

    private void processGive(CommandSender sender, String[] args) {
        if (args.length <= 2) {
            sender.sendMessage(ConfigMessages.COMMAND_SYNTAX.get(ConfigMessages.COMMAND_GIVE_SYNTAX.get()));
            return;
        }

        boolean all = false;

        Player targetPlayer = Bukkit.getPlayer(args[1]);
        if (targetPlayer == null && !(all = "@a".equals(args[1]))) {
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
                if (item == RPGPetsItem.PET)
                    stack = RPGPetsItem.getPetCarrier(new PetDescriptor(PetType.valueOf(args[3].toUpperCase()),
                            null, ConfigMessages.ITEM_PET_DEFAULTNAME.get(), 0, 0, false, false));
                else
                    stack.setAmount(Math.min(64, Math.max(0, Integer.parseInt(args[3]))));
            } catch (NumberFormatException e) {
            } catch (IllegalArgumentException e) {
                sender.sendMessage(ConfigMessages.COMMAND_GIVE_PETTYPE.get());
                return;
            }
        }

        if (all)
            for (Player p : Bukkit.getOnlinePlayers()) dropItem(p, stack);
        else dropItem(targetPlayer, stack);

        sender.sendMessage(ConfigMessages.COMMAND_GIVE_SUCCESS.get(Integer.toString(stack.getAmount()),
                stack.hasItemMeta() && stack.getItemMeta().hasDisplayName() ? stack.getItemMeta().getDisplayName() : item.name(),
                all ? "all online players" : targetPlayer.getName()));

    }

    private void dropItem(Player targetPlayer, ItemStack stack) {
        if (targetPlayer.getInventory().firstEmpty() == -1)
            targetPlayer.getWorld().dropItem(targetPlayer.getEyeLocation(), stack).setVelocity(targetPlayer.getLocation().getDirection().multiply(0.2));
        else
            targetPlayer.getInventory().addItem(stack);
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] args) {
        if (!commandSender.hasPermission(ConfigPermissions.COMMAND_HELP.get()))
            return Collections.emptyList();

        if (args.length == 0)
            return subcommands;

        if (args.length == 1)
            return subcommands.stream().filter(cmd -> cmd.startsWith(args[0])).collect(Collectors.toList());

        if (args[0].equals("give")) {
            if (!commandSender.hasPermission(ConfigPermissions.COMMAND_GIVE.get()))
                return Collections.emptyList();

            if (args.length == 2) {
                String prefix = args[1].toLowerCase();
                return Bukkit.getOnlinePlayers().stream()
                        .map(Player::getName)
                        .filter(player -> player.toLowerCase().startsWith(prefix))
                        .collect(Collectors.toList());
            }

            if (args.length == 3) {
                String prefix = args[2].toLowerCase();
                return Arrays.stream(RPGPetsItem.values())
                        .map(Enum::name)
                        .filter(name -> name.toLowerCase().startsWith(prefix))
                        .collect(Collectors.toList());
            }

            if (args.length == 4 && args[2].equalsIgnoreCase("PET")) {
                String prefix = args[3].toLowerCase();
                return Arrays.stream(PetType.values())
                        .map(Enum::name)
                        .filter(name -> name.toLowerCase().startsWith(prefix))
                        .collect(Collectors.toList());
            }
        }
        return Collections.emptyList();
    }

}
