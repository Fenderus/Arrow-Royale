package mc.fenderas.arrowroyale.manager;

import mc.fenderas.arrowroyale.ArrowRoyale;
import mc.fenderas.arrowroyale.utils.LocationUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ChestSpawners
{

    public List<Block> chestsSpawned = new ArrayList<>();

    public void SpawnChests(World world){
        removeChests();
        for (int i = 0; i < ArrowRoyale.getChestSection(world.getName()).getInt("max_chests"); i++){
            Location location = LocationUtil.generateRandomLocation(world, true);
            double lastY = location.getY();
            location.setY(lastY + 1);

            Block block = location.getBlock();
            chestsSpawned.add(block);
            block.setType(Material.CHEST);
            Chest chest = (Chest) block.getState();

            Inventory inv = chest.getBlockInventory();
            inv.clear();
            fillChests(inv);
        }
    }

    private void fillChests(Inventory inventory){
        Random rand = new Random();

        List<ItemStack> items = new ArrayList<>();
        ItemStack arrows = rand.nextBoolean() ? new ItemStack(Material.ARROW, rand.nextInt(1,16)) : new ItemStack(Material.FIREWORK_ROCKET, rand.nextInt(1,16));
        items.add(arrows);
        ItemStack crossbow = rand.nextBoolean() ? new ItemStack(Material.CROSSBOW, 1) : null;
        if(crossbow != null){
            if(rand.nextBoolean()){
                int randNum = rand.nextInt(0, 2);
                switch (randNum) {
                    case 0:
                        crossbow.addEnchantment(Enchantment.MULTISHOT, 1);
                        break;
                    case 1:
                        crossbow.addEnchantment(Enchantment.QUICK_CHARGE, 1);
                        break;
                    case 2:
                        crossbow.addEnchantment(Enchantment.PIERCING, 1);
                        break;
                }
            }
        }
        items.add(crossbow);
        ItemStack swiftness = rand.nextBoolean() ? new ItemStack(Material.POTION, 1) : null;
        if (swiftness != null){
            ItemMeta itemMeta = swiftness.getItemMeta();
            itemMeta.setDisplayName("Swiftness Potion");
            swiftness.setItemMeta(itemMeta);
            PotionMeta meta = (PotionMeta) swiftness.getItemMeta();
            meta.addCustomEffect(new PotionEffect(PotionEffectType.SPEED, 20 * 60, 1), true);
            swiftness.setItemMeta(meta);
        }
        items.add(swiftness);

        for (ItemStack item : items){
            if(item != null){
                int ind = rand.nextInt(0, 26);
                if (inventory.getItem(ind) == null){
                    inventory.setItem(ind, item);
                }
            }
        }
    }

    public void removeChests(){
        chestsSpawned.forEach(this::removeChest);
        chestsSpawned.clear();
    }


    void removeChest(Block block){
        block.setType(Material.AIR);
    }
}
