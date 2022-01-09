package mc.fenderas.arrowroyale.events;

import mc.fenderas.arrowroyale.ArrowRoyale;
import mc.fenderas.arrowroyale.manager.GameManager;
import mc.fenderas.arrowroyale.manager.GameStates;
import mc.fenderas.arrowroyale.utils.LocationUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;

public class PlayerEvents implements Listener
{
    private GameManager manager;

    public PlayerEvents(GameManager manager){
        this.manager = manager;
    }

    @EventHandler
    public void dropItems(PlayerDropItemEvent event)
    {
        Player player = event.getPlayer();
        if (manager.state == GameStates.ACTIVE){
            if (player.getWorld() == ArrowRoyale.getMinigameWorld()){
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void playerJoin(PlayerJoinEvent event)
    {
        Player player = event.getPlayer();
        if (player.getWorld() == ArrowRoyale.getMinigameWorld()){
            player.sendMessage("Joined");
            manager.getScoreboardInit().addPlayer(player, GameStates.LOBBY, true);
        }
    }

    @EventHandler
    public void playerSpawn(PlayerChangedWorldEvent event)
    {
        Player player = event.getPlayer();
        if (player.getWorld() == ArrowRoyale.getMinigameWorld()){
            manager.getScoreboardInit().addPlayer(player, GameStates.LOBBY, true);
        }
    }

    @EventHandler
    public void playerRespawn(PlayerRespawnEvent event)
    {
        Player player = event.getPlayer();
        if (manager.state == GameStates.ACTIVE){
            if (player.getWorld() == ArrowRoyale.getMinigameWorld()){

            }
        }
    }

    @EventHandler
    public void playerKilled(PlayerDeathEvent event)
    {
        Player player = event.getEntity().getKiller();
        if (manager.state == GameStates.ACTIVE){
            if(player != null){
                manager.getScoreboardInit().setPlayerCurrentScore(player.getName(), 1);
            }
        }
    }
}
