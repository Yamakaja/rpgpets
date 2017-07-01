package me.yamakaja.rpgpets.api.logging;

import com.getsentry.raven.Raven;
import com.getsentry.raven.RavenFactory;
import com.getsentry.raven.event.Breadcrumb;
import com.getsentry.raven.event.BreadcrumbBuilder;
import me.yamakaja.rpgpets.api.RPGPets;
import me.yamakaja.rpgpets.api.config.ConfigGeneral;
import org.bukkit.Bukkit;

/**
 * Created by Yamakaja on 23.06.17.
 */
public class SentryManager {

    private Raven raven;
    private boolean active = false;
    private String userId;

    public SentryManager(RPGPets plugin, String user) {
        if (!ConfigGeneral.ENABLE_SENTRY.getAsBoolean())
            return;

        try {
            this.raven = RavenFactory.ravenInstance("https://ebbb5b6c46d04f6180bb841084ed7bec:acf0480726aa4db99ad45d4c083b76af@sentry.io/182971");
            this.active = true;
        } catch (Exception e) {
            plugin.getLogger().severe("Failed to enable automatic error reporting!");
            return;
        }

        userId = user.equals(new String(new char[]{'%', '%', '_', '_', 'U', 'S', 'E', 'R', '_', '_', '%', '%'})) ? "-1" : user;

        this.raven.addBuilderHelper(eventBuilder -> {
            eventBuilder.withTag("client", userId);
            eventBuilder.withTag("version", plugin.getDescription().getVersion());
            eventBuilder.withTag("server", Bukkit.getVersion());
            eventBuilder.withTag("nms", plugin.getNMSHandler().getNMSVersion());
            eventBuilder.withTag("bukkit", Bukkit.getBukkitVersion());
        });
    }

    public void logException(Throwable e) {
        if (active)
            raven.sendException(e);
    }

    /**
     * Records a breadcrumb
     *
     * @param breadcrumb The breadcrumb to record
     */
    public void recordBreadcrumb(Breadcrumb breadcrumb) {
        if (active)
            this.raven.getContext().recordBreadcrumb(breadcrumb);
    }

    /**
     * Records an initialization phase breadcrumb (Level: DEBUG, Category: INIT)
     *
     * @param message The message
     */
    public void recordInitializationCrumb(String message) {
        this.recordBreadcrumb(new BreadcrumbBuilder().setLevel(Breadcrumb.Level.DEBUG).setCategory("Initialization").setMessage(message).build());
    }

    /**
     * Records a shutdown phase breadcrumb (Level: DEBUG, Category: SHUTDOWN)
     *
     * @param message The message
     */
    public void recordShutdownCrumb(String message) {
        this.recordBreadcrumb(new BreadcrumbBuilder().setLevel(Breadcrumb.Level.DEBUG).setCategory("Shutdown").setMessage(message).build());
    }

    /**
     * Clears breadcrumbs
     */
    public void clearContext() {
        if (active)
            this.raven.getContext().clear();
    }

    public boolean isActive() {
        return active;
    }

}
