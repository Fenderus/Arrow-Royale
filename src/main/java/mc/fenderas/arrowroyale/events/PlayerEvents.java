package mc.fenderas.arrowroyale.events;

import mc.fenderas.arrowroyale.ArrowRoyale;
import mc.fenderas.arrowroyale.manager.GameManager;
import mc.fenderas.arrowroyale.manager.GameStates;
import mc.fenderas.arrowroyale.tasks.ScoreboardChangeTimer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;

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
            manager.getPlayerScoreboard().addScoreboard(player);
        }
    }

    @EventHandler
    public void playerSpawn(PlayerChangedWorldEvent event)
    {
        Player player = event.getPlayer();
        if (player.getWorld() == ArrowRoyale.getMinigameWorld()){
            manager.getPlayerScoreboard().addScoreboard(player);
        }
    }

    @EventHandler
    public void playerDisconnect(PlayerQuitEvent event)
    {
        Player player = event.getPlayer();
        if (player.getWorld() == ArrowRoyale.getMinigameWorld()){
            manager.getPlayerScoreboard().removeScoreboard(player);
        }
    }

    @EventHandler
    public void playerRespawn(PlayerRespawnEvent event)
    {
        Player player = event.getPlayer();
        if (manager.state == GameStates.ACTIVE){
            if (player.getWorld() == ArrowRoyale.getMinigameWorld()){
                ScoreboardChangeTimer timer = new ScoreboardChangeTimer(Bukkit.getPlayer(player.getUniqueId()), manager, GameStates.LOBBY, 1);
                timer.runTaskTimer(ArrowRoyale.getPlugin(), 0, 2);
            }
        }
    }

    @EventHandler
    public void playerKilled(PlayerDeathEvent event)
    {
        Player player = event.getEntity().getKiller();
        if (manager.state == GameStates.ACTIVE){
            if(player != null){
                manager.getRoundScoreboard().setScore(player, 1);
            }
        }
    }
}
