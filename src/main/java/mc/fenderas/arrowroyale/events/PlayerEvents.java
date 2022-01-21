package mc.fenderas.arrowroyale.events;

import mc.fenderas.arrowroyale.ArrowRoyale;
import mc.fenderas.arrowroyale.inventories.LobbySelection;
import mc.fenderas.arrowroyale.manager.GameManager;
import mc.fenderas.arrowroyale.manager.GameStates;
import mc.fenderas.arrowroyale.manager.LobbyManager;
import mc.fenderas.arrowroyale.tasks.ScoreboardChangeTimer;
import mc.fenderas.arrowroyale.utils.ColorCodeUtils;
import mc.fenderas.arrowroyale.utils.ItemUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;

import java.util.Locale;

public class PlayerEvents implements Listener
{
    private GameManager manager;

    public static ItemStack selectionOpener = ItemUtil.newItem(ColorCodeUtils.translateColorsInString(ArrowRoyale.getLobbyItemSection().getString("display_name")),
            Material.valueOf(ArrowRoyale.getLobbyItemSection().getString("material").toUpperCase(Locale.ROOT)),
            ColorCodeUtils.translateColorsInStringList(ArrowRoyale.getLobbyItemSection().getStringList("lore")));

    public PlayerEvents(GameManager manager){
        this.manager = manager;
    }

    @EventHandler
    public void dropItems(PlayerDropItemEvent event)
    {
        Player player = event.getPlayer();
        LobbyManager lobbyManager = manager.getLobbyManager(player);
        if (lobbyManager != null){
            if (lobbyManager.state == GameStates.ACTIVE){
                if (player.getWorld() == lobbyManager.getWorld()){
                    event.setCancelled(true);
                }
            }
        }

    }

    @EventHandler
    public void playerJoin(PlayerJoinEvent event)
    {
        Player player = event.getPlayer();
        LobbyManager lobbyManager = manager.getLobbyManager(player);
        if (lobbyManager != null){
            if (player.getWorld() == lobbyManager.getWorld()){
                lobbyManager.getPlayerScoreboard().addScoreboard(player);
                player.getInventory().setItem(ArrowRoyale.getLobbyItemSection().getInt("slot"), selectionOpener);
            }
        }
    }

    @EventHandler
    public void playerSpawn(PlayerChangedWorldEvent event)
    {
        Player player = event.getPlayer();
        LobbyManager lobbyManager = manager.getLobbyManager(player);
        if (lobbyManager != null){
            if (player.getWorld() == lobbyManager.getWorld()){
                lobbyManager.getPlayerScoreboard().addScoreboard(player);
                player.getInventory().setItem(ArrowRoyale.getLobbyItemSection().getInt("slot"), selectionOpener);
            }
        }
    }

    @EventHandler
    public void playerDisconnect(PlayerQuitEvent event)
    {
        Player player = event.getPlayer();
        LobbyManager lobbyManager = manager.getLobbyManager(player);
        if (lobbyManager != null){
            if (player.getWorld() == lobbyManager.getWorld()){
                lobbyManager.getPlayerScoreboard().removeScoreboard(player);
            }
        }
    }

    @EventHandler
    public void playerRespawn(PlayerRespawnEvent event)
    {
        Player player = event.getPlayer();
        LobbyManager lobbyManager = manager.getLobbyManager(player);
        if (lobbyManager != null){
            if (lobbyManager.state == GameStates.ACTIVE){
                if (player.getWorld() == lobbyManager.getWorld()){
                    ScoreboardChangeTimer timer = new ScoreboardChangeTimer(Bukkit.getPlayer(player.getUniqueId()), lobbyManager, GameStates.LOBBY, 1);
                    timer.runTaskTimer(ArrowRoyale.getPlugin(), 0, 2);
                }
            }
        }
    }

    @EventHandler
    public void playerKilled(PlayerDeathEvent event)
    {
        Player player = event.getEntity().getKiller();
        if (player != null){
            LobbyManager lobbyManager = manager.getLobbyManager(player);
            if (lobbyManager != null){
                if (lobbyManager.state == GameStates.ACTIVE){
                    if (player.getWorld() == lobbyManager.getWorld()){
                        lobbyManager.getRoundScoreboard().setScore(player, 1);
                    }
                }
            }
        }
    }


}
