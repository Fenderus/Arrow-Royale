package mc.fenderas.arrowroyale.inventories;

import mc.fenderas.arrowroyale.ArrowRoyale;
import mc.fenderas.arrowroyale.utils.ItemUtil;
import mc.fenderas.arrowroyale.utils.ColorCodeUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class LobbySelection implements InventoryHolder
{

    private Material border = Material.GRAY_STAINED_GLASS_PANE;
    public List<ItemStack> customItems = new ArrayList<>();
    public Inventory inventory;

    public LobbySelection(){
        inventory = Bukkit.createInventory(this, 36, "Lobby Selector");
        for (String itemNames : ArrowRoyale.getCustomGUIItemNames()){
            ConfigurationSection section = ArrowRoyale.getSpecificCustomGUIItemSection(itemNames);
            ItemStack newItem = ItemUtil.newItem(ColorCodeUtils.translateColorsInString(section.getString("display_name")),
                    itemNames,
                    Material.valueOf(section.getString("material").toUpperCase(Locale.ROOT)),
                    ColorCodeUtils.translateColorsInStringList(section.getStringList("lore")));
            customItems.add(newItem);
        }
        init();
    }

    public void init(){
        ItemStack item;
        for(int i = 0; i < 10; i++){
            item = ItemUtil.newItem(" ", border, null);
            inventory.setItem(i, item);
        }

        item = ItemUtil.newItem(" ", border, null);
        inventory.setItem(17, item);

        for (ItemStack items : customItems){
            ConfigurationSection section = ArrowRoyale.getSpecificCustomGUIItemSection(items.getItemMeta().getLocalizedName());
            inventory.setItem(section.getInt("slot"), items);
        }

        item = ItemUtil.newItem(" ", border, null);
        inventory.setItem(18, item);

        for(int i = 26; i < 36; i++){
            item = ItemUtil.newItem(" ", border, null);
            inventory.setItem(i, item);
        }
    }



    @NotNull
    @Override
    public Inventory getInventory() {
        return inventory;
    }

    public ItemStack getSpecificItem(String name){
        for (ItemStack item : customItems) {
            if(item.getItemMeta().getDisplayName().equals(name)){
                return item;
            }
        }
        return null;
    }
}
