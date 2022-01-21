package mc.fenderas.arrowroyale.events;

import mc.fenderas.arrowroyale.ArrowRoyale;
import mc.fenderas.arrowroyale.inventories.LobbySelection;
import mc.fenderas.arrowroyale.manager.GameManager;
import mc.fenderas.arrowroyale.manager.LobbyManager;
import mc.fenderas.arrowroyale.utils.LocationUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class InventoryEvents implements Listener
{

    public GameManager gameManager;

    public InventoryEvents(GameManager manager){
        gameManager = manager;
    }

    @EventHandler
    public void onClickItem(InventoryClickEvent event){
        ItemStack item = event.getCurrentItem();
        if (event.getClickedInventory() == null) {return;}
        if(event.getClickedInventory().getHolder() instanceof LobbySelection lobbySelection){
            event.setCancelled(true);
            Player player = (Player) event.getWhoClicked();
            LobbyManager lobbyManager = gameManager.getLobbyManager(player);
            if(item == null) {return;}
            if (lobbySelection.customItems.contains(item)){
                World teleportWorld = Bukkit.getWorld(ArrowRoyale.getSpecificCustomGUIItemSection(item.getItemMeta().getLocalizedName()).getString("world"));
                if(teleportWorld != null){
                    if(lobbyManager != null){
                        switch (lobbyManager.state){
                            case ACTIVE -> {
                                lobbyManager.getRoundScoreboard().removePlayer(player);
                            }
                            case LOBBY,END,START -> {
                                lobbyManager.getPlayerScoreboard().removeScoreboard(player);
                            }
                        }
                    }
                    LocationUtil.spawnPlayerToNewWorld(player, teleportWorld);
                }else{
                    player.sendMessage(ChatColor.RED + "This world doesn't exist");
                }
                player.closeInventory();
            }
        }
    }
}
