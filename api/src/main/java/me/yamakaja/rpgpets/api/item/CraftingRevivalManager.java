package me.yamakaja.rpgpets.api.item;

import me.yamakaja.rpgpets.api.RPGPets;
import me.yamakaja.rpgpets.api.config.ConfigGeneral;
import me.yamakaja.rpgpets.api.config.ConfigMessages;
import me.yamakaja.rpgpets.api.entity.PetDescriptor;
import me.yamakaja.rpgpets.api.entity.PetState;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.material.MaterialData;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Yamakaja on 17.06.17.
 */
public class CraftingRevivalManager extends AbstractRevivalManager {

    private ShapelessRecipe recipe;

    public CraftingRevivalManager(RPGPets plugin) {
        super(plugin);

        recipe = new ShapelessRecipe(new ItemStack(Material.SKULL_ITEM, 1, (short) 3));

        recipe.addIngredient(Material.SLIME_BALL);
        recipe.addIngredient(new MaterialData(Material.SKULL_ITEM, (byte) 3));

        plugin.getServer().addRecipe(recipe);
        plugin.getServer().getPluginManager().registerEvents(this, plugin);

        plugin.getServer().getScheduler().runTaskTimer(plugin, this, 60 * 60 * 20, 60 * 60 * 20);
    }

    @EventHandler
    public void onPrepareCraft(PrepareItemCraftEvent event) {
        if (event.getRecipe() == null || event.getRecipe().getResult() == null)
            return;

        MaterialData data = event.getRecipe().getResult().getData();

        if (data.getItemType() != Material.SKULL_ITEM || data.getData() != 3)
            return;

        ItemStack[] matrix = event.getInventory().getMatrix();

        PetDescriptor pet = null;
        ItemStack food = null;

        for (ItemStack iterItem : matrix) {
            if (iterItem == null)
                continue;

            if (iterItem.getType() == Material.SLIME_BALL && iterItem.hasItemMeta() && iterItem.getItemMeta().hasDisplayName()
                    && iterItem.getItemMeta().getDisplayName().equals(ConfigMessages.ITEM_FOOD_NAME.get())) {
                food = iterItem;
            } else {
                PetDescriptor petDescriptor = RPGPetsItem.decode(iterItem);
                if (petDescriptor != null)
                    pet = petDescriptor;
            }

            if (pet != null && food != null)
                break;
        }

        if (pet == null || food == null || pet.getState() != PetState.DEAD) {
            event.getInventory().setResult(null);
            return;
        }

        pet.setState(PetState.READY);
        event.getInventory().setResult(RPGPetsItem.getPetCarrier(pet));
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getClickedInventory() == null || (e.getClickedInventory().getType() != InventoryType.CRAFTING &&
                e.getClickedInventory().getType() != InventoryType.WORKBENCH) || e.getSlot() != 0 ||
                !(e.getClickedInventory() instanceof CraftingInventory))
            return;

        CraftingInventory inventory = (CraftingInventory) e.getClickedInventory();

        if (inventory.getRecipe() == null || inventory.getResult() == null)
            return;

        MaterialData resultData = inventory.getRecipe().getResult().getData();

        if (resultData.getItemType() != Material.SKULL_ITEM || resultData.getData() != (byte) 3)
            return;

        if (this.isCoolingDown(e.getWhoClicked().getUniqueId())) {
            e.setCancelled(true);
            ((Player) e.getWhoClicked()).updateInventory();
            e.getWhoClicked().sendMessage(ConfigMessages.GENERAL_FEEDCOOLDOWN.get());
            return;
        }

        this.setCooldown(e.getWhoClicked().getUniqueId());
    }

    @EventHandler(priority = EventPriority.MONITOR) // ARGH ... BAD
    public void onCraft(CraftItemEvent event) {
        if (!ConfigGeneral.ENABLE_CRAFTING_HACK.getAsBoolean())
            return;

        PetDescriptor descriptor = RPGPetsItem.decode(event.getCurrentItem());
        if (descriptor != null)
            event.setCancelled(false);
    }

}
