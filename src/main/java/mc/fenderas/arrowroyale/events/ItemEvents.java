package mc.fenderas.arrowroyale.events;

import mc.fenderas.arrowroyale.ArrowRoyale;
import mc.fenderas.arrowroyale.inventories.LobbySelection;
import mc.fenderas.arrowroyale.items.EditAxeModes;
import mc.fenderas.arrowroyale.items.ItemManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class ItemEvents implements Listener
{

    public EditAxeModes modes = EditAxeModes.RANDOMSPAWN;

    @EventHandler
    public void itemUsed(PlayerInteractEvent event){
        Player player = event.getPlayer();
        if(modes == EditAxeModes.RANDOMSPAWN){
            if(event.getAction() == Action.RIGHT_CLICK_BLOCK){
                if(event.getItem() != null){
                    if(event.getItem().getItemMeta().equals(ItemManager.editAxe.getItemMeta())){
                        ArrowRoyale.getCoordinatesSection(player.getWorld().getName()).set("xMin", event.getClickedBlock().getX());
                        ArrowRoyale.getCoordinatesSection(player.getWorld().getName()).set("zMin", event.getClickedBlock().getZ());
                        ArrowRoyale.saveMainConfig();
                        event.getPlayer().sendMessage("Minimum coordinates x = " + event.getClickedBlock().getX() + " and z = " + event.getClickedBlock().getZ() + " have been set");
                        event.setCancelled(true);
                    }
                }
            }
            if(event.getAction() == Action.LEFT_CLICK_BLOCK){
                if(event.getItem() != null){
                    if(event.getItem().getItemMeta().equals(ItemManager.editAxe.getItemMeta())){
                        ArrowRoyale.getCoordinatesSection(player.getWorld().getName()).set("xMax", event.getClickedBlock().getX());
                        ArrowRoyale.getCoordinatesSection(player.getWorld().getName()).set("zMax", event.getClickedBlock().getZ());
                        ArrowRoyale.saveMainConfig();
                        event.getPlayer().sendMessage("Maximum coordinates x = " + event.getClickedBlock().getX() + " and z = " + event.getClickedBlock().getZ() + " have been set");
                        event.setCancelled(true);
                    }
                }
            }
        }else if(modes == EditAxeModes.SPAWNPOINT){
            if(event.getAction() == Action.RIGHT_CLICK_BLOCK){
                if(event.getItem() != null){
                    if(event.getItem().getItemMeta().equals(ItemManager.editAxe.getItemMeta())){
                        Bukkit.dispatchCommand(player, "setworldspawn " + event.getClickedBlock().getX() + " " + event.getClickedBlock().getY() + " " + event.getClickedBlock().getZ());
                    }
                }
            }
        }

        if(event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR){
            if (event.getItem() != null){
                if(event.getItem().getItemMeta().equals(PlayerEvents.selectionOpener.getItemMeta())){
                    LobbySelection selection = new LobbySelection();
                    player.openInventory(selection.getInventory());
                    event.setCancelled(true);
                }
            }
        }
    }

    public void setEditAxeMode(EditAxeModes modes){
        this.modes = modes;
    }
}
