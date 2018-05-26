package me.yamakaja.rpgpets.api.item;

import me.yamakaja.rpgpets.api.RPGPets;
import me.yamakaja.rpgpets.api.config.ConfigGeneral;
import me.yamakaja.rpgpets.api.config.ConfigItems;
import me.yamakaja.rpgpets.api.config.ConfigMessages;
import me.yamakaja.rpgpets.api.entity.PetDescriptor;
import me.yamakaja.rpgpets.api.entity.PetState;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.material.MaterialData;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Created by Yamakaja on 17.06.17.
 */
public class CraftingRevivalManager extends AbstractRevivalManager {

    private ShapelessRecipe recipe;

    public CraftingRevivalManager(RPGPets plugin) {
        super(plugin);

        recipe = new ShapelessRecipe(new ItemStack(Material.SKULL_ITEM, 1, (short) 3));

        recipe.addIngredient(ConfigItems.FOOD_MATERIAL);
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

            if (iterItem.getType() == ConfigItems.FOOD_MATERIAL && iterItem.hasItemMeta() && iterItem.getItemMeta().hasDisplayName()
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

        if (ConfigGeneral.EXPENSIVE_REVIVAL.getAsBoolean() && food.getAmount() < Math.min(64, pet.getLevel())) {
            event.getInventory().setResult(null);
            event.getView().getPlayer().sendMessage(ConfigMessages.GENERAL_EXPENSIVEREVIVAL.get(String.valueOf(Math.min(64, pet.getLevel()))));
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
    public void onMonitorCraft(CraftItemEvent event) {
        PetDescriptor petDescriptor = null;
        if (ConfigGeneral.EXPENSIVE_REVIVAL.getAsBoolean()) {
            int[] foodSlot = {0}, petSlot = {0};
            ItemStack[] matrix = event.getInventory().getMatrix();

            petDescriptor = RPGPetsItem.decode(event.getCurrentItem());
            if (petDescriptor == null)
                return;

            for (int i = 0; i < matrix.length; i++)
                if (matrix[i] == null) ;
                else if (matrix[i].getType() == ConfigItems.FOOD_MATERIAL)
                    foodSlot[0] = i;
                else if (matrix[i].getType() == Material.SKULL_ITEM)
                    petSlot[0] = i;

            PetDescriptor pet = RPGPetsItem.decode(matrix[petSlot[0]]);

            if (pet == null)
                throw new RuntimeException("Logic error, expected pet!");

            int amountToTake = Math.max(1, Math.min(64, pet.getLevel())) - 1;

            if (amountToTake > 0)
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        int newAmount = matrix[foodSlot[0]].getAmount() - amountToTake;
                        if (newAmount < 1)
                            matrix[foodSlot[0]] = null;
                        else
                            matrix[foodSlot[0]].setAmount(newAmount);

                        event.getInventory().setMatrix(matrix);
                    }
                }.runTask(super.plugin);

        }

        if (!ConfigGeneral.ENABLE_CRAFTING_HACK.getAsBoolean())
            return;

        if (petDescriptor == null)
            petDescriptor = RPGPetsItem.decode(event.getCurrentItem());

        if (petDescriptor != null)
            event.setCancelled(false);
    }

}
