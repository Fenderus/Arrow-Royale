package mc.fenderas.arrowroyale.items;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ItemManager {

    public static ItemStack editAxe;

    public static void init(){
        createAxe();
    }

    private static void createAxe(){
        ItemStack item = new ItemStack(Material.STONE_AXE, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§6Arrow Royale World Edit");
        List<String> lore = new ArrayList<>();
        lore.add("§dThis is used to set");
        lore.add("§dlocations around the world");
        meta.setLore(lore);
        item.setItemMeta(meta);
        editAxe = item;
    }
}
