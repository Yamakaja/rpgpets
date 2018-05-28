package me.yamakaja.rpgpets.api.item;

import me.yamakaja.rpgpets.api.RPGPets;
import me.yamakaja.rpgpets.api.config.ConfigItems;
import me.yamakaja.rpgpets.api.config.ConfigMessages;
import me.yamakaja.rpgpets.api.entity.PetDescriptor;
import me.yamakaja.rpgpets.api.entity.PetState;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Yamakaja on 2/5/18.
 */
public class InventoryRevivalManager extends AbstractRevivalManager {

    public InventoryRevivalManager(RPGPets plugin) {
        super(plugin);

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent e) {
        ItemStack cursor = e.getCursor();
        if (!(cursor.getType() == ConfigItems.FOOD_MATERIAL && cursor.hasItemMeta() && cursor.getItemMeta().hasDisplayName()
                && cursor.getItemMeta().getDisplayName().equals(ConfigMessages.ITEM_FOOD_NAME.get())))
            return;

        PetDescriptor descriptor = RPGPetsItem.decode(e.getCurrentItem());

        if (descriptor == null)
            return;

        e.setCancelled(true);

        if (descriptor.getState() != PetState.DEAD)
            return;

        if (isCoolingDown(e.getWhoClicked().getUniqueId())) {
            e.getWhoClicked().sendMessage(ConfigMessages.GENERAL_FEEDCOOLDOWN.get());
            return;
        }

        descriptor.setState(PetState.READY);
        e.setCurrentItem(RPGPetsItem.getPetCarrier(descriptor));
        if (cursor.getAmount() > 1)
            cursor.setAmount(cursor.getAmount() - 1);
        else
            cursor = new ItemStack(Material.AIR);

        e.setCursor(cursor);
        setCooldown(e.getWhoClicked().getUniqueId());
    }

}
