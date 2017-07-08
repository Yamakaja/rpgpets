package me.yamakaja.rpgpets.plugin.version;

import com.google.gson.Gson;
import me.yamakaja.rpgpets.api.RPGPets;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by Yamakaja on 08.07.17.
 */
public class UpdateChecker extends BukkitRunnable implements Listener {

    private final Gson gson = new Gson();
    private RPGPets plugin;
    private int currentVersion;
    private String newVersion;
    private List<String> changelog;

    public UpdateChecker(RPGPets pets) {
        this.plugin = pets;
        pets.getServer().getPluginManager().registerEvents(this, pets);
        this.runTaskTimerAsynchronously(pets, 0, 10 * 60 * 20);
        this.currentVersion = parseVersion(pets.getDescription().getVersion());
    }

    @Override
    public void run() {
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) new URL("https", "vps.pub.yamakaja.me", 443, "/rpgpets").openConnection();

            InputStream inputStream = connection.getInputStream();
            Version[] versions = gson.fromJson(new InputStreamReader(inputStream), Version[].class);
            inputStream.close();

            for (Version version : versions)
                version.setEncodedVersion(parseVersion(version.getVersion()));

            Optional<Version> newestVersion = Arrays.stream(versions)
                    .sorted((o1, o2) -> (int) (o2.getRelease() / 1000 - o1.getRelease() / 1000))
                    .findFirst();

            if (!newestVersion.isPresent()) {
                plugin.getLogger().warning("No versions could be found! Update checker aborting ...");
                this.cancel();
                return;
            }

            if (newestVersion.get().getEncodedVersion() <= currentVersion)
                return;

            this.newVersion = newestVersion.get().getVersion();

            changelog = Arrays.stream(versions)
                    .filter(version -> version.getEncodedVersion() > currentVersion)
                    .map(Version::getChanges)
                    .collect(Collectors.toList());

        } catch (IOException e) {
            // Fail silently
        } finally {
            if (connection != null)
                connection.disconnect();
        }

        plugin.getLogger().info("A new version is available, please update now (" + newVersion + ")");
    }

    @EventHandler
    public void onPlayerLogin(PlayerJoinEvent event) {
        if (newVersion == null || !event.getPlayer().isOp())
            return;

        new UpdateNotifier(event.getPlayer()).runTaskLater(plugin, 5 * 20);
    }

    private int parseVersion(String versionString) {
        try {
            String[] split = versionString.split("\\.");

            if (split.length == 0)
                return 0;

            int version = 0;

            version += Integer.parseInt(split[0]) * 10000;
            version += Integer.parseInt(split[1]) * 100;
            version += Integer.parseInt(split[2]);

            return version;
        } catch (ArrayIndexOutOfBoundsException e) {
            return 0;
        }

    }

    private class UpdateNotifier extends BukkitRunnable {

        private final String PREFIX = ChatColor.GRAY + "[" + ChatColor.AQUA + "RPGPets" + ChatColor.GRAY + "] " + ChatColor.GREEN;
        private Player target;

        public UpdateNotifier(Player target) {
            this.target = target;
        }

        @Override
        public void run() {
            target.sendMessage(PREFIX + "You are " + changelog.size() + " version" + (changelog.size() > 1 ? "s" : "") + " behind. Newest: " + newVersion);
            target.sendMessage(PREFIX + ChatColor.GOLD + "Changelog:");
            changelog.forEach(entry -> target.sendMessage(PREFIX + ChatColor.YELLOW + " - " + ChatColor.GOLD + entry));
        }

    }

}
